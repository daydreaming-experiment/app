package com.brainydroid.daydreaming.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpGetTask extends AsyncTask<HttpGetData, Void, Boolean> {

	private static String TAG = "HttpGetTask";

	private HttpClient client;
	private String serverAnswer;
	private HttpGetData getData;
	private HttpGet httpGet;
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
	protected Boolean doInBackground(HttpGetData... getDatas) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] doInBackground");
		}

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

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onPostExecute");
		}

		httpConversationCallback.onHttpConversationFinished(success, serverAnswer);
	}
}
