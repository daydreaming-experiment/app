package com.brainydroid.daydreaming.network;

import android.app.Application;
import android.content.Context;

import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Json;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.json.JSONException;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

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
    @Inject StatusManager statusManager;
    @Inject ErrorHandler errorHandler;
    @Inject SntpClient sntpClient;

    private final File storageDir;
    @Inject private HashMap<String,File> maiIdFiles;
    @Inject private HashMap<String,File> publicFiles;
    @Inject private HashMap<String,File> privateFiles;

    @Inject
    public CryptoStorage(Application application) {
        Logger.d(TAG, "Initializing CryptoStorage");
        storageDir = application.getDir(STORAGE_DIRNAME, Context.MODE_PRIVATE);
    }

    private synchronized File getMaiIdFile() {
        String currentModeName = statusManager.getCurrentModeName();
        Logger.v(TAG, "{} - Getting maiIdFile", currentModeName);

        if (! maiIdFiles.containsKey(currentModeName)) {
            maiIdFiles.put(currentModeName,
                    new File(storageDir, currentModeName + MAI_ID_FILENAME));
        }
        return maiIdFiles.get(currentModeName);
    }

    private synchronized File getPublicFile() {
        String currentModeName = statusManager.getCurrentModeName();
        Logger.v(TAG, "{} - Getting publicFile", currentModeName);

        if (! publicFiles.containsKey(currentModeName)) {
            publicFiles.put(currentModeName,
                    new File(storageDir, currentModeName + PUBLIC_FILENAME));
        }
        return publicFiles.get(currentModeName);
    }

    private synchronized File getPrivateFile() {
        String currentModeName = statusManager.getCurrentModeName();
        Logger.v(TAG, "{} - Getting privateFile", currentModeName);

        if (! privateFiles.containsKey(currentModeName)) {
            privateFiles.put(currentModeName,
                    new File(storageDir, currentModeName + PRIVATE_FILENAME));
        }
        return privateFiles.get(currentModeName);
    }

    public synchronized void onReady(CryptoStorageCallback callback) {
        if (!hasStoredKeyPairAndMaiId()) {
            Logger.i(TAG, "{} - CryptoStorage not ready -> " +
                    "registering on the server", statusManager.getCurrentModeName());
            register(callback);
        } else {
            Logger.i(TAG, "{} - CryptoStorage is ready -> calling back callback " +
                    "straight away", statusManager.getCurrentModeName());
            callback.onCryptoStorageReady(true);
        }
    }

    public synchronized void register(CryptoStorageCallback callback) {

        Logger.d(TAG, "{} - Generating a keypair for registration",
                statusManager.getCurrentModeName());
        final String registrationStartAppMode = statusManager.getCurrentModeName();
        final KeyPair kp = crypto.generateKeyPairNamedCurve(DEFAULT_CURVE_NAME);
        final CryptoStorageCallback parentCallback = callback;

        final HttpConversationCallback registrationCallback = new HttpConversationCallback() {

            private final String TAG =
                    "Registration HttpConversationCallback";

            @Override
            public void onHttpConversationFinished(boolean success, String serverAnswer) {
                Logger.d(TAG, "{} - Registration HttpConversation finished",
                        statusManager.getCurrentModeName());

                if (!statusManager.getCurrentModeName().equals(registrationStartAppMode)) {
                    Logger.i(TAG, "App mode has changed from {0} to {1} during "
                            + "registration, aborting crypto init.", registrationStartAppMode,
                            statusManager.getCurrentModeName());
                    parentCallback.onCryptoStorageReady(false);
                    return;
                }

                boolean storageSuccess = false;

                if (success) {
                    Logger.d(TAG, "{} - Registration HttpConversation " +
                            "successful", statusManager.getCurrentModeName());
                    ProfileWrapper registrationAnswer;
                    try {
                        registrationAnswer = json.fromJson(serverAnswer,
                                ProfileWrapper.class);
                    } catch (JSONException e) {
                        // Server answer wasn't what we expected. Try to parse it.
                        errorHandler.handleServerError(serverAnswer, e);
                        parentCallback.onCryptoStorageReady(false);
                        return;
                    }

                    String maiId = registrationAnswer.getProfile().getId();
                    storageSuccess = storeKeyPairAndMaiId(kp, maiId);

                    if (storageSuccess) {
                        Logger.i(TAG, "{} - Full registration successful",
                                statusManager.getCurrentModeName());
                    } else {
                        Logger.e(TAG, "{} - Registration successful on server " +
                                "but failed to store locally", statusManager.getCurrentModeName());
                    }
                } else {
                    Logger.w(TAG, "Error while registering on server");
                }

                parentCallback.onCryptoStorageReady(success && storageSuccess);
            }

        };

        Logger.i(TAG, "{} - Launching registration", statusManager.getCurrentModeName());
        serverTalker.register(kp, registrationCallback);
    }

    private synchronized boolean hasStoredMaiId() {
        return getMaiIdFile().exists();
    }

    private synchronized boolean hasStoredPrivateKey() {
        return getPrivateFile().exists();
    }

    private synchronized boolean hasStoredPublicKey() {
        return getPublicFile().exists();
    }

    public synchronized boolean hasStoredKeyPairAndMaiId() {
        if (hasStoredPrivateKey() && hasStoredPublicKey() && hasStoredMaiId()) {
            return true;
        } else {
            Logger.w(TAG, "{} - Incomplete store: missing a key or the maiId -> " +
                    "clearing the whole store", statusManager.getCurrentModeName());
            clearStore();
            return false;
        }
    }

    private synchronized boolean storeKeyPairAndMaiId(KeyPair kp, String maiId) {
        try {
            clearStore();

            if (getMaiIdFile().createNewFile()) {
                Logger.d(TAG, "{} - Created new file for maiId",
                        statusManager.getCurrentModeName());
            } else {
                Logger.w(TAG, "{} - Overwriting existing file for maiId",
                        statusManager.getCurrentModeName());
            }
            BufferedWriter maiIdBuf = new BufferedWriter(new FileWriter(getMaiIdFile()));
            maiIdBuf.write(maiId);
            maiIdBuf.close();
            Logger.d(TAG, "{} - Written maiId to file", statusManager.getCurrentModeName());

            if (getPublicFile().createNewFile()) {
                Logger.d(TAG, "{} - Created new file for public key",
                        statusManager.getCurrentModeName());
            } else {
                Logger.w(TAG, "{} - Overwriting existing file for public key",
                        statusManager.getCurrentModeName());
            }
            BufferedWriter publicBuf = new BufferedWriter(new FileWriter(getPublicFile()));
            publicBuf.write(Crypto.base64Encode(kp.getPublic().getEncoded()));
            publicBuf.close();
            Logger.d(TAG, "{} - Written public key to file", statusManager.getCurrentModeName());

            if (getPrivateFile().createNewFile()) {
                Logger.d(TAG, "{} - Created new file for private key",
                        statusManager.getCurrentModeName());
            } else {
                Logger.w(TAG, "{} - Overwriting existing file for private key",
                        statusManager.getCurrentModeName());
            }
            BufferedWriter privateBuf = new BufferedWriter(new FileWriter(getPrivateFile()));
            privateBuf.write(Crypto.base64Encode(kp.getPrivate().getEncoded()));
            privateBuf.close();
            Logger.d(TAG, "{} - Written private key to file", statusManager.getCurrentModeName());

            return true;
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error creating files for crypto storage",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        }
    }

    public synchronized String getMaiId() {
        Logger.d(TAG, "{} - Reading maiId from file", statusManager.getCurrentModeName());

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(getMaiIdFile()));
            String maiId = buf.readLine();
            buf.close();
            return maiId;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "{} - maiId file not found", statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error reading maiId file", statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        }
    }

    public synchronized PublicKey getPublicKey() {
        Logger.d(TAG, "{} - Reading public key from file", statusManager.getCurrentModeName());

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(getPublicFile()));
            String keyStr = buf.readLine();
            buf.close();
            return crypto.readPublicKey(keyStr);
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "{} - Public key file not found", statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error reading public key file",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Logger.e(TAG, "{} - Public key read from file but badly formatted",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        }
    }

    public synchronized PrivateKey getPrivateKey() {
        Logger.d(TAG, "{} - Reading private key from file", statusManager.getCurrentModeName());

        try {
            BufferedReader buf;
            buf = new BufferedReader(new FileReader(getPrivateFile()));
            String keyStr = buf.readLine();
            buf.close();
            return crypto.readPrivateKey(keyStr);
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "{} - Private key file not found", statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.e(TAG, "{} - IO error reading private key file",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Logger.e(TAG, "{} - Private key read from file but badly formatted",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized KeyPair getKeyPair() {
        return new KeyPair(getPublicKey(), getPrivateKey());
    }

    private synchronized boolean clearMaiId() {
        Logger.v(TAG, "{} - Clearing maiId file", statusManager.getCurrentModeName());
        return getMaiIdFile().delete();
    }

    private synchronized boolean clearPrivateKey() {
        Logger.v(TAG, "{} - Clearing private key file", statusManager.getCurrentModeName());
        return getPrivateFile().delete();
    }

    private synchronized boolean clearPublicKey() {
        Logger.v(TAG, "{} - Clearing public key file", statusManager.getCurrentModeName());
        return getPublicFile().delete();
    }

    public synchronized void clearStore() {
        Logger.v(TAG, "{} - Clearing whole store", statusManager.getCurrentModeName());
        clearPrivateKey();
        clearPublicKey();
        clearMaiId();
    }

    public synchronized String createArmoredPublicKey(PublicKey publicKey) {
        return Crypto.armorPublicKey(publicKey);
    }

    private synchronized byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Logger.d(TAG, "{} - Signing data", statusManager.getCurrentModeName());
            return crypto.sign(privateKey, data);
        } catch (InvalidKeyException e) {
            Logger.e(TAG, "{} - Asked to sign data but our key was invalid",
                    statusManager.getCurrentModeName());
            throw new RuntimeException(e);
        }
    }

    public synchronized String signJose(String data) {
        return signJose(data, getPrivateKey());
    }

    public synchronized String signJose(String data, PrivateKey privateKey) {
        Logger.i(TAG, "{} - Creating JOSE for data", statusManager.getCurrentModeName());

        String b64Header = Crypto.base64urlEncode(JWS_HEADER.getBytes());
        String b64Payload = Crypto.base64urlEncode(data.getBytes());

        String b64Input = b64Header + "." + b64Payload;
        String b64Sig = Crypto.base64urlEncode(
                sign(b64Input.getBytes(), privateKey));

        JWSSignature jwsSignature = new JWSSignature(b64Header, b64Sig);
        ArrayList<JWSSignature> jwsSignatures = new ArrayList<JWSSignature>();
        jwsSignatures.add(jwsSignature);
        JWS jws = new JWS(b64Payload, jwsSignatures);

        return json.toJsonPublic(jws);
    }

    public synchronized void createJwsAuthToken(final AuthTokenCallback authTokenCallback) {
        Logger.i(TAG, "{} - Creating JWS auth token", statusManager.getCurrentModeName());

        SntpClientCallback sntpCallback = new SntpClientCallback() {
            private String TAG = "createJwsAuthToken sntpCallback";
            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    Logger.d(TAG, "Sntp request returned successfully");

                    // Sntp answers with milliseconds. We want seconds.
                    int now = (int)(sntpClient.getNow() / 1000);

                    String b64Header = Crypto.base64urlEncode(JWS_HEADER.getBytes());
                    String payload = json.toJsonPublic(new AuthContent(getMaiId(), now));
                    String b64Payload = Crypto.base64urlEncode(payload.getBytes());

                    String b64Input = b64Header + "." + b64Payload;
                    String b64Sig = Crypto.base64urlEncode(
                            sign(b64Input.getBytes(), getPrivateKey()));

                    authTokenCallback.onAuthTokenReady(b64Header + "." + b64Payload + "." + b64Sig);
                } else {
                    Logger.v(TAG, "Sntp request failed");
                    authTokenCallback.onAuthTokenReady(null);
                }
            }
        };

        sntpClient.asyncRequestTime(sntpCallback);
    }

    public static interface AuthTokenCallback {
        public void onAuthTokenReady(String authToken);
    }
}
