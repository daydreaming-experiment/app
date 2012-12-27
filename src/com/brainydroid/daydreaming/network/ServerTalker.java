package com.brainydroid.daydreaming.network;

import java.security.PublicKey;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class ServerTalker {

	private static String TAG = "ServerTalker";

	private static String YE_URL_API = "/api/v1";
	private static String YE_URL_DEVICES = YE_URL_API + "/devices/";
	private static String YE_EXPS = "/exps/";
	private static String YE_RESULTS = "/results/";

	private static ServerTalker stInstance;

	private final CryptoStorage _cryptoStorage;
	private String _serverName;

	public static synchronized ServerTalker getInstance(String serverName,
			CryptoStorage cryptoStorage) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

		if (stInstance == null) {
			stInstance = new ServerTalker(serverName, cryptoStorage);
		}

		return stInstance;
	}

	private ServerTalker(String serverName, CryptoStorage cryptoStorage) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] ServerTalker");
		}

		_serverName = serverName;
		_cryptoStorage = cryptoStorage;
	}

	public void setServerName(String s) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setServerName");
		}

		_serverName = s;
	}

	public void register(PublicKey publicKey, HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] register");
		}

		final String jsonKey = _cryptoStorage.createArmoredPublicKeyJson(publicKey);
		String postUrl = _serverName + YE_URL_DEVICES;
		HttpPostData postData = new HttpPostData(postUrl, callback);
		postData.setPostString(jsonKey);
		postData.setContentType("application/json");

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

	private String getPostResultUrl(String exp_id) {
		return _serverName + YE_URL_DEVICES + _cryptoStorage.getMaiId() +
				YE_EXPS + exp_id + YE_RESULTS;
	}

	public <T> void signAndUploadData(String exp_id, String data,
			HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] signAndUploadData");
		}

		String signedData = _cryptoStorage.signJws(data);
		HttpPostData postData = new HttpPostData(getPostResultUrl(exp_id), callback);
		postData.setPostString(signedData);
		postData.setContentType("application/jws");

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}
}
