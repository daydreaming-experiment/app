package com.brainydroid.daydreaming.network;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

public class HttpPostData {

    private static String TAG = "HttpPostData";

    private final String postUrl;
    private String postString = null;
    private String contentType = null;
    private final HttpConversationCallback httpConversationCallback;

    public HttpPostData(String postUrl, HttpConversationCallback httpConversationCallback) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] HttpPostData (argset 1: small)");
        }

        this.postUrl = postUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public String getPostUrl() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getPostUrl");
        }

        return postUrl;
    }

    public String getPostString() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getPostString");
        }

        return postString;
    }

    public void setPostString(String postString) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addPostString");
        }

        this.postString = postString;
    }

    public String getContentType() {

        // Verbose
        if (Config.LOGV){
            Log.v(TAG, "[fn] getContentType");
        }

        return contentType;
    }

    public void setContentType(String contentType) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setContentType");
        }

        this.contentType = contentType;
    }

    public HttpConversationCallback getHttpConversationCallback() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getHttpConversationCallback");
        }

        return httpConversationCallback;
    }

}
