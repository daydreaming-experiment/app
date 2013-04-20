package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Util;

import java.io.IOException;
import java.io.InputStream;

public class FirstLaunchTermsActivity  extends SherlockActivity {

    private static String TAG = "FirstLaunchTermsActivity";

    TextView consent;
    private StatusManager status;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_terms);

        consent = (TextView)findViewById(R.id.firstLaunchTerms_textConsent);
        status = StatusManager.getInstance(this);
        populateConsent();
    }

    @Override
    public void onStart() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStart");
        }

        super.onStart();
        checkFirstLaunch();
    }

    @Override
    public void onResume() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onResume");
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onBackPressed");
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

        launchProfileActivity();
    }

    private void launchProfileActivity() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchProfileActivity");
        }

        Intent intent = new Intent(this, FirstLaunchProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void populateConsent() {
        try {
            InputStream termsIS = getResources().openRawResource(R.raw.terms);
            consent.setText(Util.convertStreamToString(termsIS));
            termsIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFirstLaunch() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] checkFirstLaunch");
        }

        if (status.isFirstLaunchCompleted()) {
            finish();
        }
    }

}
