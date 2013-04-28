package com.brainydroid.daydreaming.network;

import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpGetData {

    private static String TAG = "HttpGetData";

    private final String getUrl;
    private final HttpConversationCallback httpConversationCallback;

    public HttpGetData(String getUrl, HttpConversationCallback httpConversationCallback) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] HttpGetData");
        }

        this.getUrl = getUrl;
        this.httpConversationCallback = httpConversationCallback;
    }

    public String getGetUrl() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getGetUrl");
        }

        return getUrl;
    }

    public HttpConversationCallback getHttpConversationCallback() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getHttpConversationCallback");
        }

        return httpConversationCallback;
    }

}
