package com.brainydroid.daydreaming.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Util;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

@ContentView(R.layout.activity_first_launch_terms)
public class FirstLaunchTermsActivity  extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchTermsActivity";

    @InjectView(R.id.firstLaunchTerms_textConsent) TextView consent;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);

        populateConsent();
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

        launchNextActivity(FirstLaunchProfileActivity.class);
    }

    private void populateConsent() {
        try {
            InputStream termsInputStream = getResources().openRawResource(R.raw.terms);
            consent.setText(Util.convertStreamToString(termsInputStream));
            termsInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
