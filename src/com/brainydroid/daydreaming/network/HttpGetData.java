package com.brainydroid.daydreaming.network;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpGetData {

	private static String TAG = "HttpGetData";

	private final String _getUrl;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpGetData(String getUrl, HttpConversationCallback httpConversationCallback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] HttpGetData");
		}

		_getUrl = getUrl;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getGetUrl() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getGetUrl");
		}

		return _getUrl;
	}

	public HttpConversationCallback getHttpConversationCallback() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getHttpConversationCallback");
		}

		return _httpConversationCallback;
	}
}
