package com.brainydroid.daydreaming.network;

import java.util.HashMap;

import org.apache.http.entity.mime.content.FileBody;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpPostData {

	private static String TAG = "HttpPostData";

	private final String _postUrl;
	private final HashMap<String, FileBody> _postFiles;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpPostData(String postUrl, HttpConversationCallback httpConversationCallback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] HttpPostData (argset 1: small)");
		}

		_postUrl = postUrl;
		_postFiles = new HashMap<String, FileBody>();
		_httpConversationCallback = httpConversationCallback;
	}

	public HttpPostData(String postUrl, HashMap<String, FileBody> postFiles,
			HttpConversationCallback httpConversationCallback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] HttpPostData (argset 2: full)");
		}

		_postUrl = postUrl;
		_postFiles = postFiles;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getPostUrl() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getPostUrl");
		}

		return _postUrl;
	}

	public HashMap<String, FileBody> getPostFiles() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getPostFiles");
		}

		return _postFiles;
	}

	public void addPostFile(String key, FileBody fileBody) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addPostFile");
		}

		_postFiles.put(key, fileBody);
	}

	public HttpConversationCallback getHttpConversationCallback() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getHttpConversationCallback");
		}

		return _httpConversationCallback;
	}
}
