package com.brainydroid.daydreaming.network;

import java.security.PublicKey;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class ServerTalker {

	private static String TAG = "ServerTalker";

	private static String YE_URL_API = "/api/v1";
	private static String YE_URL_DEVICES = YE_URL_API + "/devices/";

	private static String YE_POST_DATA = "jws_data";

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

		final String key = _cryptoStorage.createArmoredPublicKeyJson(publicKey);
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (fullCallback) onHttpConversationFinished");
				}

				initialCallback.onHttpConversationFinished(success, serverAnswer);
			}

		};

		String postUrl = _serverName + YE_URL_DEVICES;
		HttpPostData postData = new HttpPostData(postUrl, fullCallback);
		postData.setPostString(key);


		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

	public void signAndUploadData(String ea_id, String data,
			HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] signAndUploadData");
		}

		final SignedDataFiles sdf = _cryptoStorage.createSignedDataFiles(data);
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (fullCallback) onHttpConversationFinished");
				}

				initialCallback.onHttpConversationFinished(success, serverAnswer);
				sdf.deleteFiles();
			}

		};

		//		String postUrl = _serverName + BS_URL_UPLOAD + BS_URL_UPLOAD_EA_DATA + _cryptoStorage.getMaiId() + "/" + ea_id;
		//		HttpPostData postData = new HttpPostData(postUrl, fullCallback);
		//		postData.addPostFile(BS_FORM_UPLOAD_SIGFILE, new FileBody(sdf.getSignatureFile()));
		//		postData.addPostFile(BS_FORM_UPLOAD_DATAFILE, new FileBody(sdf.getDataFile()));

		HttpPostTask postTask = new HttpPostTask();
		//		postTask.execute(postData);
	}
}
