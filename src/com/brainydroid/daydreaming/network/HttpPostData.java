package com.brainydroid.daydreaming.network;

import java.util.HashMap;

import org.apache.http.entity.mime.content.FileBody;

public class HttpPostData {

	private final String _postUrl;
	private final HashMap<String, FileBody> _postFiles;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpPostData(String postUrl, HttpConversationCallback httpConversationCallback) {
		_postUrl = postUrl;
		_postFiles = new HashMap<String, FileBody>();
		_httpConversationCallback = httpConversationCallback;
	}

	public HttpPostData(String postUrl, HashMap<String, FileBody> postFiles,
			HttpConversationCallback httpConversationCallback) {
		_postUrl = postUrl;
		_postFiles = postFiles;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getPostUrl() {
		return _postUrl;
	}

	public HashMap<String, FileBody> getPostFiles() {
		return _postFiles;
	}

	public void addPostFile(String key, FileBody fileBody) {
		_postFiles.put(key, fileBody);
	}

	public HttpConversationCallback getHttpConversationCallback() {
		return _httpConversationCallback;
	}

}
