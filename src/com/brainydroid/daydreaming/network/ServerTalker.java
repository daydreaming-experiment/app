package com.brainydroid.daydreaming.network;

import java.io.File;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class ServerTalker {

	private static String BS_URL_UPLOAD = "upload/";
	private static String BS_URL_UPLOAD_MAI_PUBKEY = "mai_pubkey/";
	private static String BS_URL_UPLOAD_EA_DATA = "ea_data/";
	private static String BS_URL_UPLOAD_REQUEST_MAI_ID = "request_mai_id";

	private static String BS_FORM_UPLOAD_PUBKEYFILE = "pubkeyfile";
	private static String BS_FORM_UPLOAD_DATAFILE = "datafile";
	private static String BS_FORM_UPLOAD_SIGFILE = "sigfile";

	private static HttpClient client;
	private static String servername;

	private static CryptoStorage cryptoStorage;
	private static ServerTalker stInstance;

	private class HttpGetTask extends AsyncTask<HttpGetData, Void, Boolean> {

		String serverAnswer;
		HttpGetData getData;
		HttpGet httpGet;
		HttpResponse response;
		HttpEntity resEntity;
		HttpConversationCallback httpConversationCallback;

		@Override
		protected Boolean doInBackground(HttpGetData... getDatas) {
			try {
				getData = getDatas[0];
				httpConversationCallback = getData.getHttpConversationCallback();
				httpGet = new HttpGet(getData.getGetUrl());

				response = client.execute(httpGet);
				resEntity = response.getEntity();

				if (resEntity != null) {
					serverAnswer = EntityUtils.toString(resEntity);
				}
			} catch (Exception e) {
				serverAnswer = null;
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			httpConversationCallback.onHttpConversationFinished(success, serverAnswer);
		}

	}

	private class HttpPostTask extends AsyncTask<HttpPostData, Void, Boolean> {

		String serverAnswer;
		HttpPostData postData;
		HttpPost httpPost;
		MultipartEntity reqEntity;
		HttpResponse response;
		HttpEntity resEntity;
		HttpConversationCallback httpConversationCallback;

		@Override
		protected Boolean doInBackground(HttpPostData... postDatas) {
			try {
				postData = postDatas[0];
				httpConversationCallback = postData.getHttpConversationCallback();
				httpPost = new HttpPost(postData.getPostUrl());
				reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				for (Map.Entry<String, FileBody> entry : postData.getPostFiles().entrySet()) {
					reqEntity.addPart(entry.getKey(), entry.getValue());
				}

				httpPost.setEntity(reqEntity);

				response = client.execute(httpPost);
				resEntity = response.getEntity();

				if (resEntity != null) {
					serverAnswer = EntityUtils.toString(resEntity);
				}
			} catch (Exception e) {
				serverAnswer = null;
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			httpConversationCallback.onHttpConversationFinished(success, serverAnswer);
		}

	}

	public static synchronized ServerTalker getInstance(String s, CryptoStorage cs) {
		if (stInstance == null) {
			stInstance = new ServerTalker(s, cs);
		}

		return stInstance;
	}

	private ServerTalker(String s, CryptoStorage cs) {
		client = new DefaultHttpClient();
		servername = s;
		cryptoStorage = cs;
	}

	public void setServerName(String s) {
		servername = s;
	}

	public void uploadPublicKey(HttpConversationCallback callback) {

		final File keyFile = cryptoStorage.createArmoredPublicKeyFile();
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {
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

		final SignedDataFiles sdf = cryptoStorage.createSignedDataFiles(data);
		final HttpConversationCallback initialCallback = callback;

		HttpConversationCallback fullCallback = new HttpConversationCallback() {

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {
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
		String getUrl = servername + BS_URL_UPLOAD + BS_URL_UPLOAD_REQUEST_MAI_ID;
		HttpGetData getData = new HttpGetData(getUrl, callback);
		HttpGetTask getTask = new HttpGetTask();
		getTask.execute(getData);
	}

}