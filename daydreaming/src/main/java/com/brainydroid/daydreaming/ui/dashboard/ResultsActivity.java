package com.brainydroid.daydreaming.ui.dashboard;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.network.HttpConversationCallback;
import com.brainydroid.daydreaming.network.ServerTalker;
import com.google.inject.Inject;

import java.util.HashMap;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_results)
public class ResultsActivity extends RoboFragmentActivity {

    private static String TAG = "ResultsActivity";

    @Inject ParametersStorage parametersStorage;
    @Inject StatusManager statusManager;
    @Inject ServerTalker serverTalker;
    @Inject ProfileStorage profileStorage;
    @InjectView(R.id.activity_results_webView) private WebView webView;

    private boolean resultsLoadFinished = false;
    private boolean pageLoadFinished = false;
    private ProgressDialog progressDialog;

    public static class JSResults {

        private int versionCode;
        private String resultsWrap;

        public JSResults(int versionCode, String resultsWrap) {
            this.versionCode = versionCode;
            this.resultsWrap = resultsWrap;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public String getResultsWrap() {
            return resultsWrap;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        super.onCreate(savedInstanceState);

        // No need to notify results again, the user opened them
        statusManager.setResultsNotified();
        statusManager.setResultsNotifiedDashboard();

        webView.getSettings().setJavaScriptEnabled(true);
    }

    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
        loadResultsAndWebView();
    }

    private void setPageLoadFinished() {
        pageLoadFinished = true;
        dismissDialogIfLoadingFinished();
    }

    private void setResultsLoadFinished() {
        resultsLoadFinished = true;
        dismissDialogIfLoadingFinished();
    }

    private void dismissDialogIfLoadingFinished() {
        if (pageLoadFinished && resultsLoadFinished) {
            progressDialog.dismiss();
        }
    }


    public void loadResultsAndWebView() {
        Logger.d(TAG, "Starting results retrieval from server");

        progressDialog = ProgressDialog.show(ResultsActivity.this,
                "Results", "Loading your results...");

        HttpConversationCallback resultsCallback = new HttpConversationCallback() {
            private String TAG = "getResults HttpConversationCallback";
            @Override
            public void onHttpConversationFinished(boolean success, String serverAnswer) {
                if (success) {
                    Logger.i(TAG, "Results successfully retrieved");

                    setResultsLoadFinished();

                    // Now load the results in the webView and start it
                    webView.clearCache(true);
                    webView.addJavascriptInterface(
                            new JSResults(profileStorage.getAppVersionCode(), serverAnswer),
                            "injectedResults");
                    webView.setWebViewClient(new WebViewClient() {

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                        }

                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            setPageLoadFinished();
                        }

                        public void onReceivedError(WebView view, int errorCode,
                                                    String description, String failingUrl) {
                            progressDialog.dismiss();
                            Toast.makeText(ResultsActivity.this,
                                    "Oh no! " + description, Toast.LENGTH_SHORT).show();
                        }
                    });
                    webView.loadUrl(parametersStorage.getResultsPageUrl());
                } else {
                    Logger.i(TAG, "Failed to get results");
                    Toast.makeText(ResultsActivity.this,
                            "Oh no! There was an error loading the results",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }
        };

        HashMap<String, String> args = new HashMap<String, String>();
        args.put("access", "private");

        serverTalker.authenticatedGet(serverTalker.getResultsUrl(), args, resultsCallback);
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
