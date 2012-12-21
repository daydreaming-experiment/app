package com.brainydroid.daydreaming.network;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpPostData {

	private static String TAG = "HttpPostData";

	private final String _postUrl;
	private String _postString;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpPostData(String postUrl, HttpConversationCallback httpConversationCallback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] HttpPostData (argset 1: small)");
		}

		_postUrl = postUrl;
		_postString = null;
		_httpConversationCallback = httpConversationCallback;
	}

	public HttpPostData(String postUrl, String postString,
			HttpConversationCallback httpConversationCallback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] HttpPostData (argset 2: full)");
		}

		_postUrl = postUrl;
		_postString = postString;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getPostUrl() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getPostUrl");
		}

		return _postUrl;
	}

	public String getPostString() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getPostString");
		}

		return _postString;
	}

	public void setPostString(String postString) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addPostString");
		}

		_postString = postString;
	}

	public HttpConversationCallback getHttpConversationCallback() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getHttpConversationCallback");
		}

		return _httpConversationCallback;
	}
}
