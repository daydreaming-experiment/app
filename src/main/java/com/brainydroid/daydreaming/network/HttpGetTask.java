package com.brainydroid.daydreaming.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class HttpGetTask extends AsyncTask<HttpGetData, Void, Boolean> {

    private static String TAG = "HttpGetTask";

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
    protected Boolean doInBackground(HttpGetData... getDatas) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] doInBackground");
        }

        try {
            HttpGetData getData = getDatas[0];
            httpConversationCallback = getData.getHttpConversationCallback();
            HttpGet httpGet = new HttpGet(getData.getGetUrl());

            HttpResponse response = client.execute(httpGet);
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
