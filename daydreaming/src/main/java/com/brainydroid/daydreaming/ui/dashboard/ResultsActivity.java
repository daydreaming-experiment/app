package com.brainydroid.daydreaming.ui.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.google.inject.Inject;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_results)
public class ResultsActivity extends RoboFragmentActivity {

    private static String TAG = "ResultsActivity";

    @Inject ParametersStorage parametersStorage;
    @InjectView(R.id.activity_results_webView) private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        super.onCreate(savedInstanceState);

        webView.getSettings().setJavaScriptEnabled(true);
        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {

            private ProgressDialog progressDialog;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog = ProgressDialog.show(ResultsActivity.this,
                        "Results", "Loading...");
                super.onPageStarted(view, url, favicon);
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                progressDialog.dismiss();
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(parametersStorage.getResultsPageUrl());

    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
