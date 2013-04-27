package com.brainydroid.daydreaming.network;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.PublicKey;

@Singleton
public class ServerTalker {

	private static String TAG = "ServerTalker";

	@Inject CryptoStorage cryptoStorage;

    private String getPostResultUrl(String expId) {
        return ServerConfig.SERVER_NAME + ServerConfig.YE_URL_DEVICES + cryptoStorage.getMaiId() +
                ServerConfig.YE_EXPS + expId + ServerConfig.YE_RESULTS;
    }

	public void register(PublicKey publicKey, HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] register");
		}

		String jsonKey = cryptoStorage.createArmoredPublicKeyJson(publicKey);
		String postUrl = ServerConfig.SERVER_NAME + ServerConfig.YE_URL_DEVICES;

		HttpPostData postData = new HttpPostData(postUrl, callback);
		postData.setPostString(jsonKey);
		postData.setContentType("application/json");

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

	public void signAndUploadData(String expId, String data,
			HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] signAndUploadData");
		}

		String signedData = cryptoStorage.signJws(data);

		HttpPostData postData = new HttpPostData(getPostResultUrl(expId), callback);
		postData.setPostString(signedData);
		postData.setContentType("application/jws");

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

}
