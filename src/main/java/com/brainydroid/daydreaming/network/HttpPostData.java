package com.brainydroid.daydreaming.network;

public class HttpPostData {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "HttpPostData";

    private final String postUrl;
    private String postString = null;
    private String contentType = null;
    private final HttpConversationCallback httpConversationCallback;

    public HttpPostData(String postUrl, HttpConversationCallback httpConversationCallback) {
        this.postUrl = postUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getPostString() {
        return postString;
    }

    public void setPostString(String postString) {
        this.postString = postString;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public HttpConversationCallback getHttpConversationCallback() {
        return httpConversationCallback;
    }

}
