package com.brainydroid.daydreaming.network;

import android.app.Application;
import android.content.Context;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

@Singleton
public class CryptoStorage {

    private static String TAG = "CryptoStorage";

    private static final String MAI_ID_FILENAME = "mai_id";
    private static final String PUBLIC_FILENAME = "key.pub";
    private static final String PRIVATE_FILENAME = "key";
    private static final String STORAGE_DIRNAME = "cryptoStorage";

    private static final String NOKP_EXCEPTION_MSG = "No keypair present";

    private static final String DEFAULT_CURVE_NAME = "secp256r1"; // Corresponds to NIST256p in python-ecdsa;
    private static final String JWS_HEADER = "{\"alg\": \"ES256\"}";

    @Inject Crypto crypto;
    @Inject ServerTalker serverTalker;

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    private final File maiIdFile;
    private final File publicFile;
    private final File privateFile;

    @Inject
    public CryptoStorage(Application application) {
        Logger.d(TAG, "Initializing CryptoStorage");

        File storageDir = application.getDir(STORAGE_DIRNAME, Context.MODE_PRIVATE);
        maiIdFile = new File(storageDir, MAI_ID_FILENAME);
        publicFile = new File(storageDir, PUBLIC_FILENAME);
        privateFile = new File(storageDir, PRIVATE_FILENAME);
    }

    public void onReady(CryptoStorageCallback callback) {
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
                    RegistrationAnswer registrationAnswer = gson.fromJson(serverAnswer,
                            RegistrationAnswer.class);
                    String maiId = registrationAnswer.getId();
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
        serverTalker.register(kp.getPublic(), registrationCallback);
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

    public synchronized String createArmoredPublicKeyJson(PublicKey publicKey) {
        Logger.d(TAG, "Armouring public key in json");
        HashMap<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("vk_pem", Crypto.armorPublicKey(publicKey));
        return gson.toJson(pkMap);
    }

    private synchronized byte[] sign(byte[] data) {
        if (!hasStoredKeyPairAndMaiId()) {
            Logger.e(TAG, "Asked to sign data but we don't have a complete " +
                    "storage initialized");
            throw new RuntimeException(NOKP_EXCEPTION_MSG);
        }

        try {
            Logger.d(TAG, "Signing data");
            return crypto.sign(getPrivateKey(), data);
        } catch (InvalidKeyException e) {
            Logger.e(TAG, "Asked to sign data but our key was invalid");
            throw new RuntimeException(e);
        }
    }

    public synchronized String signJws(String data) {
        Logger.i(TAG, "Creating JWS for data");

        String b64Header = Crypto.base64urlEncode(JWS_HEADER.getBytes());
        String b64Payload = Crypto.base64urlEncode(data.getBytes());

        String b64Input = b64Header + "." + b64Payload;
        String b64sig = Crypto.base64urlEncode(sign(b64Input.getBytes()));

        return b64Input + "." + b64sig;
    }

}
