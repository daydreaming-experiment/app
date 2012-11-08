package com.brainydroid.daydreaming.network;

import java.io.File;

import org.apache.http.entity.mime.content.FileBody;

import android.util.Log;

public class ServerTalker {

	private static String TAG = "ServerTalker";

	private static String BS_URL_UPLOAD = "upload/";
	private static String BS_URL_UPLOAD_MAI_PUBKEY = "mai_pubkey/";
	private static String BS_URL_UPLOAD_EA_DATA = "ea_data/";
	private static String BS_URL_UPLOAD_REQUEST_MAI_ID = "request_mai_id";

	private static String BS_FORM_UPLOAD_PUBKEYFILE = "pubkeyfile";
	private static String BS_FORM_UPLOAD_DATAFILE = "datafile";
	private static String BS_FORM_UPLOAD_SIGFILE = "sigfile";

	private static String servername;

	private static CryptoStorage cryptoStorage;
	private static ServerTalker stInstance;

	public static synchronized ServerTalker getInstance(String s, CryptoStorage cs) {

		// Debug
		Log.d(TAG, "[fn] getInstance");

		if (stInstance == null) {
			stInstance = new ServerTalker(s, cs);
		}

		return stInstance;
	}

	private ServerTalker(String s, CryptoStorage cs) {

		// Debug
		Log.d(TAG, "[fn] ServerTalker");

		servername = s;
		cryptoStorage = cs;
	}

	public void setServerName(String s) {

		// Verbose
		Log.v(TAG, "[fn] setServerName");

		servername = s;
	}

	public void uploadPublicKey(HttpConversationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] uploadPublicKey");

		final File keyFile = cryptoStorage.createArmoredPublicKeyFile();
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback > fullCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				Log.d(TAG, "[fn] onHttpConversationFinished");

				initialCallback.onHttpConversationFinished(success, serverAnswer);
				keyFile.delete();
			}

		};

		String postUrl = servername + BS_URL_UPLOAD + BS_URL_UPLOAD_MAI_PUBKEY + cryptoStorage.getMaiId();
		HttpPostData postData = new HttpPostData(postUrl, fullCallback);
		postData.addPostFile(BS_FORM_UPLOAD_PUBKEYFILE, new FileBody(keyFile));

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

	public void signAndUploadData(String ea_id, String data,
			HttpConversationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] signAndUploadData");

		final SignedDataFiles sdf = cryptoStorage.createSignedDataFiles(data);
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback > fullCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				Log.d(TAG, "[fn] onHttpConversationFinished");

				initialCallback.onHttpConversationFinished(success, serverAnswer);
				sdf.deleteFiles();
			}

		};

		String postUrl = servername + BS_URL_UPLOAD + BS_URL_UPLOAD_EA_DATA + cryptoStorage.getMaiId() + "/" + ea_id;
		HttpPostData postData = new HttpPostData(postUrl, fullCallback);
		postData.addPostFile(BS_FORM_UPLOAD_SIGFILE, new FileBody(sdf.getSignatureFile()));
		postData.addPostFile(BS_FORM_UPLOAD_DATAFILE, new FileBody(sdf.getDataFile()));

		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(postData);
	}

	public void requestMaiId(HttpConversationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] requestMaiId");

		String getUrl = servername + BS_URL_UPLOAD + BS_URL_UPLOAD_REQUEST_MAI_ID;
		HttpGetData getData = new HttpGetData(getUrl, callback);
		HttpGetTask getTask = new HttpGetTask();
		getTask.execute(getData);
	}
}
