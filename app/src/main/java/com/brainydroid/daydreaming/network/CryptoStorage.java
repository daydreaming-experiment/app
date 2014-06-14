package com.brainydroid.daydreaming.network;

import android.app.Application;
import android.content.Context;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Json;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

@Singleton
public class CryptoStorage {

    private static String TAG = "CryptoStorage";

    private static final String MAI_ID_FILENAME = "mai_id";
    private static final String PUBLIC_FILENAME = "key.pub";
    private static final String PRIVATE_FILENAME = "key";
    private static final String STORAGE_DIRNAME = "cryptoStorage";

    private static final String DEFAULT_CURVE_NAME = "secp256r1"; // Corresponds to NIST256p in python-ecdsa;
    private static final String JWS_HEADER = "{\"alg\": \"ES256\"}";

    @Inject Crypto crypto;
    @Inject ServerTalker serverTalker;
    @Inject Json json;
    @Inject Context context;

    private final File maiIdFile;
    private final File publicFile;
    private final File privateFile;

    @Inject
    public CryptoStorage(Application application, StatusManager statusManager) {
        Logger.d(TAG, "Initializing CryptoStorage for profile {}", statusManager.getCurrentModeName());

        File storageDir = application.getDir(statusManager.getCurrentModeName() + STORAGE_DIRNAME,
                Context.MODE_PRIVATE);
        maiIdFile = new File(storageDir, MAI_ID_FILENAME);
        publicFile = new File(storageDir,PUBLIC_FILENAME);
        privateFile = new File(storageDir, PRIVATE_FILENAME);
    }

    public synchronized void onReady(CryptoStorageCallback callback) {
        if (!hasStoredKeyPairAndMaiId()) {
            Logger.i(TAG, "CryptoStorage not ready -> " +
                    "registering on the server");
            register(callback);
        } else {
            Logger.i(TAG, "CryptoStorage is ready -> calling back callback " +
                    "straight away");
            callback.onCryptoStorageReady(true);
        }
    }

    public synchronized void register(CryptoStorageCallback callback) {

        Logger.d(TAG, "Generating a keypair for registration");
        final KeyPair kp = crypto.generateKeyPairNamedCurve(DEFAULT_CURVE_NAME);
        final CryptoStorageCallback parentCallback = callback;

        final HttpConversationCallback registrationCallback = new HttpConversationCallback() {

            private final String TAG =
                    "Registration HttpConversationCallback";

            @Override
            public void onHttpConversationFinished(boolean success, String serverAnswer) {
                Logger.d(TAG, "Registration HttpConversation finished");

                boolean storageSuccess = false;

                if (success) {
                    Logger.d(TAG, "Registration HttpConversation " +
                            "successful");
                    ProfileWrapper registrationAnswer = json.fromJson(serverAnswer,
                            ProfileWrapper.class);
                    // TODO: handle the case where returned JSON is in fact an error.
                    Logger.td(context, "Server answer: ",
                            serverAnswer.replace("{", "'{'")
                                    .replace("}", "'}'"));
                    String maiId = registrationAnswer.getProfile().getId();
                    storageSuccess = storeKeyPairAndMaiId(kp, maiId);

                    if (storageSuccess) {
                        Logger.i(TAG, "Full registration successful");
                    } else {
                        Logger.e(TAG, "Registration successful on server " +
                                "but failed to store locally");
                    }
                }

                parentCallback.onCryptoStorageReady(success && storageSuccess);
            }

        };

        Logger.i(TAG, "Launching registration");
        serverTalker.register(kp, registrationCallback);
    }

    private synchronized boolean hasStoredMaiId() {
        return maiIdFile.exists();
    }

    private synchronized boolean hasStoredPrivateKey() {
        return privateFile.exists();
    }

    private synchronized boolean hasStoredPublicKey() {
        return publicFile.exists();
    }

    public synchronized boolean hasStoredKeyPairAndMaiId() {
        if (hasStoredPrivateKey() && hasStoredPublicKey() && hasStoredMaiId()) {
            return true;
        } else {
            Logger.w(TAG, "Incomplete store: missing a key or the maiId -> " +
                    "clearing the whole store");
            clearStore();
            return false;
        }
    }

    private synchronized boolean storeKeyPairAndMaiId(KeyPair kp, String maiId) {
        try {
            clearStore();

            if (maiIdFile.createNewFile()) {
                Logger.d(TAG, "Created new file for maiId");
            } else {
                Logger.w(TAG, "Overwriting existing file for maiId");
            }
            BufferedWriter maiIdBuf = new BufferedWriter(new FileWriter(maiIdFile));
            maiIdBuf.write(maiId);
            maiIdBuf.close();
            Logger.d(TAG, "Written maiId to file");

            if (publicFile.createNewFile()) {
                Logger.d(TAG, "Created new file for public key");
            } else {
                Logger.w(TAG, "Overwriting existing file for public key");
            }
            BufferedWriter publicBuf = new BufferedWriter(new FileWriter(publicFile));
            publicBuf.write(Crypto.base64Encode(kp.getPublic().getEncoded()));
            publicBuf.close();
            Logger.d(TAG, "Written public key to file");

            if (privateFile.createNewFile()) {
                Logger.d(TAG, "Created new file for private key");
            } else {
                Logger.w(TAG, "Overwriting existing file for private key");
            }
            BufferedWriter privateBuf = new BufferedWriter(new FileWriter(privateFile));
            privateBuf.write(Crypto.base64Encode(kp.getPrivate().getEncoded()));
            privateBuf.close();
            Logger.d(TAG, "Written private key to file");

            return true;
        } catch (IOException e) {
            Logger.e(TAG, "IO error creating files for crypto storage");
            throw new RuntimeException(e);
        }
    }

    public synchronized String getMaiId() {
        Logger.d(TAG, "Reading maiId from file");

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(maiIdFile));
            String maiId = buf.readLine();
            buf.close();
            return maiId;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "maiId file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "IO error reading maiId file");
            throw new RuntimeException(e);
        }
    }

    public synchronized PublicKey getPublicKey() {
        Logger.d(TAG, "Reading public key from file");

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(publicFile));
            String keyStr = buf.readLine();
            buf.close();
            return crypto.readPublicKey(keyStr);
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "Public key file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "IO error reading public key file");
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Logger.e(TAG, "Public key read from file but badly formatted");
            throw new RuntimeException(e);
        }
    }

    public synchronized PrivateKey getPrivateKey() {
        Logger.d(TAG, "Reading private key from file");

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(privateFile));
            String keyStr = buf.readLine();
            buf.close();
            return crypto.readPrivateKey(keyStr);
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "Private key file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "IO error reading private key file");
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Logger.e(TAG, "Private key read from file but badly formatted");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized KeyPair getKeyPair() {
        return new KeyPair(getPublicKey(), getPrivateKey());
    }

    private synchronized boolean clearMaiId() {
        Logger.v(TAG, "Clearing maiId file");
        return maiIdFile.delete();
    }

    private synchronized boolean clearPrivateKey() {
        Logger.v(TAG, "Clearing private key file");
        return privateFile.delete();
    }

    private synchronized boolean clearPublicKey() {
        Logger.v(TAG, "Clearing public key file");
        return publicFile.delete();
    }

    public synchronized boolean clearStore() {
        Logger.v(TAG, "Clearing whole store");
        return clearPrivateKey() && clearPublicKey() && clearMaiId();
    }

    public synchronized String createArmoredPublicKey(PublicKey publicKey) {
        return Crypto.armorPublicKey(publicKey);
    }

    private synchronized byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Logger.d(TAG, "Signing data");
            return crypto.sign(privateKey, data);
        } catch (InvalidKeyException e) {
            Logger.e(TAG, "Asked to sign data but our key was invalid");
            throw new RuntimeException(e);
        }
    }

    public synchronized String signJws(String data) {
        return signJws(data, getPrivateKey());
    }

    public synchronized String signJws(String data, PrivateKey privateKey) {
        Logger.i(TAG, "Creating JWS for data");

        String b64Header = Crypto.base64urlEncode(JWS_HEADER.getBytes());
        String b64Payload = Crypto.base64urlEncode(data.getBytes());

        String b64Input = b64Header + "." + b64Payload;
        String b64Sig = Crypto.base64urlEncode(
                sign(b64Input.getBytes(), privateKey));

        JWSSignature jwsSignature = new JWSSignature(b64Header, b64Sig);
        ArrayList<JWSSignature> jwsSignatures = new ArrayList<JWSSignature>();
        jwsSignatures.add(jwsSignature);
        JWS jws = new JWS(b64Payload, jwsSignatures);

        return json.toJsonExposed(jws);
    }

}
