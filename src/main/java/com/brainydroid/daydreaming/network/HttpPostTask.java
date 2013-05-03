package com.brainydroid.daydreaming.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpPostTask extends AsyncTask<HttpPostData, Void, Boolean> {

    private static String TAG = "HttpPostTask";

    private HttpClient client;
    private String serverAnswer;
    private HttpConversationCallback httpConversationCallback;

    @Override
    protected void onPreExecute() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onPreExecute");
        }

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,
                ServerConfig.HTTP_TIMEOUT);
        client = new DefaultHttpClient(httpParams);
    }

    @Override
    protected Boolean doInBackground(HttpPostData... postDatas) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] doInBackground");
        }

        try {
            HttpPostData postData = postDatas[0];
            httpConversationCallback = postData.getHttpConversationCallback();
            HttpPost httpPost = new HttpPost(postData.getPostUrl());
            StringEntity stringEntity = new StringEntity(postData.getPostString());

            httpPost.setHeader("Content-Type", postData.getContentType());
            httpPost.setEntity(stringEntity);

            HttpResponse response = client.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                serverAnswer = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            // FIXME: properly inform about errors that happen
            serverAnswer = null;
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onPostExecute");
        }

        if (httpConversationCallback != null) {
            httpConversationCallback.onHttpConversationFinished(success, serverAnswer);
        }
    }

}
