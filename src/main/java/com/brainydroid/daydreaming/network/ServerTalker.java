package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.PublicKey;

@Singleton
public class ServerTalker {

    private static String TAG = "ServerTalker";

    @Inject CryptoStorage cryptoStorage;

    private synchronized String getPostResultUrl(String expId) {
        return ServerConfig.SERVER_NAME + ServerConfig.YE_URL_DEVICES + cryptoStorage.getMaiId() +
                ServerConfig.YE_EXPS + expId + ServerConfig.YE_RESULTS;
    }

    public synchronized void register(PublicKey publicKey, HttpConversationCallback callback) {
        Logger.i(TAG, "Registering at the server");

        Logger.d(TAG, "Getting key to register");
        String jsonKey = cryptoStorage.createArmoredPublicKeyJson(publicKey);
        String postUrl = ServerConfig.SERVER_NAME + ServerConfig.YE_URL_DEVICES;

        HttpPostData postData = new HttpPostData(postUrl, callback);
        postData.setPostString(jsonKey);
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

        HttpPostData postData = new HttpPostData(getPostResultUrl(expId), callback);
        postData.setPostString(signedData);
        postData.setContentType("application/jws");

        HttpPostTask postTask = new HttpPostTask();
        Logger.d(TAG, "Executing POST task for data upload");
        postTask.execute(postData);
    }

}
