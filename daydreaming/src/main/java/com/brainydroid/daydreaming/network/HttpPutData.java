package com.brainydroid.daydreaming.network;

public class HttpPutData {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "HttpPutData";

    private final String putUrl;
    private String putString = null;
    private String contentType = null;
    private final HttpConversationCallback httpConversationCallback;

    public HttpPutData(String putUrl,
                       HttpConversationCallback httpConversationCallback) {
        this.putUrl = putUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public synchronized String getPutUrl() {
        return putUrl;
    }

    public synchronized String getPutString() {
        return putString;
    }

    public synchronized void setPutString(String putString) {
        this.putString = putString;
    }

    public synchronized String getContentType() {
        return contentType;
    }

    public synchronized void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public synchronized HttpConversationCallback
    getHttpConversationCallback() {
        return httpConversationCallback;
    }

}
