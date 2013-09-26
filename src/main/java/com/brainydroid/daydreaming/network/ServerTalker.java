package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Json;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.KeyPair;
import java.security.PublicKey;

@Singleton
public class ServerTalker {

    private static String TAG = "ServerTalker";

    @Inject CryptoStorage cryptoStorage;
    @Inject Json json;

    private synchronized String getPostResultUrl() {
        return ServerConfig.SERVER_NAME + ServerConfig.YE_URL_RESULTS;
    }

    public synchronized void register(KeyPair keyPair,
                                      HttpConversationCallback callback) {
        Logger.i(TAG, "Registering at the server");

        Logger.d(TAG, "Getting key to register");
        String vkPem = cryptoStorage.createArmoredPublicKey(keyPair.getPublic());
        Profile profile = new Profile(vkPem, ServerConfig.EXP_ID);
        ProfileRegistrationData profileRegistrationData =
                new ProfileRegistrationData(profile);
        String jsonPayload = json.toJsonExposed(profileRegistrationData);
        String signedJson = cryptoStorage.signJws(jsonPayload, keyPair.getPrivate());
        String postUrl = ServerConfig.SERVER_NAME +
                ServerConfig.YE_URL_PROFILES;

        HttpPostData postData = new HttpPostData(postUrl, callback);
        postData.setPostString(signedJson);
        postData.setContentType("application/json");

        HttpPostTask postTask = new HttpPostTask();
        Logger.d(TAG, "Executing POST task for registration");
        postTask.execute(postData);
    }

    public synchronized void signAndUploadData(String expId, String data,
            HttpConversationCallback callback) {
        Logger.i(TAG, "Signing and uploading data to server");

        Logger.d(TAG, "Signing data");
        String signedData = cryptoStorage.signJws(data);

        HttpPostData postData = new HttpPostData(getPostResultUrl(), callback);
        postData.setPostString(signedData);
        postData.setContentType("application/jws");

        HttpPostTask postTask = new HttpPostTask();
        Logger.d(TAG, "Executing POST task for data upload");
        postTask.execute(postData);
    }

}
