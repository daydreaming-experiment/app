package com.brainydroid.daydreaming.network;

public class HttpPostData {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "HttpPostData";

    private final String postUrl;
    private String postString = null;
    private String contentType = null;
    private final HttpConversationCallback httpConversationCallback;

    public HttpPostData(String postUrl,
                        HttpConversationCallback httpConversationCallback) {
        this.postUrl = postUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public synchronized String getPostUrl() {
        return postUrl;
    }

    public synchronized String getPostString() {
        return postString;
    }

    public synchronized void setPostString(String postString) {
        this.postString = postString;
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
