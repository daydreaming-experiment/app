package com.brainydroid.daydreaming.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
	private StringEntity stringEntity;
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
			stringEntity = new StringEntity(postData.getPostString());

			httpPost.setHeader("Content-Type", postData.getContentType());
			httpPost.setEntity(stringEntity);

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
