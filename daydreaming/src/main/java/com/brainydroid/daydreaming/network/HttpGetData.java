package com.brainydroid.daydreaming.network;

public class HttpGetData {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "HttpGetData";

    private final String getUrl;
    private final HttpConversationCallback httpConversationCallback;

    public HttpGetData(String getUrl, HttpConversationCallback httpConversationCallback) {
        this.getUrl = getUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public synchronized String getGetUrl() {
        return getUrl;
    }

    public synchronized HttpConversationCallback getHttpConversationCallback() {
        return httpConversationCallback;
    }

}
