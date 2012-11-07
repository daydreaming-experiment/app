package com.brainydroid.daydreaming.network;


public class HttpGetData {

	private final String _getUrl;
	private final HttpConversationCallback _httpConversationCallback;

	public HttpGetData(String getUrl, HttpConversationCallback httpConversationCallback) {
		_getUrl = getUrl;
		_httpConversationCallback = httpConversationCallback;
	}

	public String getGetUrl() {
		return _getUrl;
	}

	public HttpConversationCallback getHttpConversationCallback() {
		return _httpConversationCallback;
	}

}
