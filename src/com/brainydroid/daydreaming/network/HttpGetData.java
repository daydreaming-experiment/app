package com.brainydroid.daydreaming.network;

import android.util.Log;

public class HttpGetData {

	private static String TAG = "HttpGetData";

	private final String _getUrl;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpGetData(String getUrl, HttpConversationCallback httpConversationCallback) {

		// Debug
		Log.d(TAG, "[fn] HttpGetData");

		_getUrl = getUrl;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getGetUrl() {

		// Verbose
		Log.v(TAG, "[fn] getGetUrl");

		return _getUrl;
	}

	public HttpConversationCallback getHttpConversationCallback() {

		// Verbose
		Log.v(TAG, "[fn] getHttpConversationCallback");

		return _httpConversationCallback;
	}
}
