package com.brainydroid.daydreaming.network;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpPostTask extends AsyncTask<HttpPostData, Void, Boolean> {

	private static String TAG = "HttpPostTask";

	private HttpClient client;
	private String serverAnswer;
	private HttpPostData postData;
	private HttpPost httpPost;
	private MultipartEntity reqEntity;
	private HttpResponse response;
	private HttpEntity resEntity;
	private HttpConversationCallback httpConversationCallback;

	@Override
	protected void onPreExecute() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onPreExecute");
		}

		client = new DefaultHttpClient();
	}

	@Override
	protected Boolean doInBackground(HttpPostData... postDatas) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] doInBackground");
		}

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

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onPostExecute");
		}

		httpConversationCallback.onHttpConversationFinished(success, serverAnswer);
	}
}
