package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_first_launch_measures)
public class FirstLaunchMeasuresActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchMeasuresActivity";

    @InjectView(R.id.firstLaunchMeasures_textNetworkLocation) TextView textNetworkLocation;
    @InjectView(R.id.firstLaunchMeasures_textSettings) TextView textSettings;
    @InjectView(R.id.firstLaunchMeasures_buttonSettings) Button buttonSettings;
    @InjectView(R.id.firstLaunchMeasures_buttonNext) Button buttonNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);

        if (statusManager.isNetworkLocEnabled()) {
            launchDashboard();
        }
    }

    @Override
    public void onStart() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStart");
        }

        super.onStart();
        updateView();
    }

    @Override
    public void onResume() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onResume");
        }

        super.onResume();
        updateView();
    }

    private void updateView() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updateView");
        }

        textNetworkLocation.setCompoundDrawablesWithIntrinsicBounds(
                statusManager.isNetworkLocEnabled() ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

        updateRequestAdjustSettings();
    }

    private void updateRequestAdjustSettings() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updateRequestAdjustSettings");
        }

        if ((statusManager.isNetworkLocEnabled()) || (Build.FINGERPRINT.startsWith("generic"))) {
            setAdjustSettingsOff();
        } else {
            setAdjustSettingsNecessary();
        }
    }

    @TargetApi(11)
    private void setAdjustSettingsNecessary() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setAdjustSettingsNecessary");
        }

        textSettings.setText(R.string.firstLaunchMeasures_text_settings_necessary);
        textSettings.setVisibility(View.VISIBLE);
        buttonSettings.setVisibility(View.VISIBLE);
        buttonSettings.setClickable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }

        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void setAdjustSettingsOff() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setAdjustSettingsOff");
        }

        textSettings.setVisibility(View.INVISIBLE);
        buttonSettings.setVisibility(View.INVISIBLE);
        buttonSettings.setClickable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(1f);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }

        buttonNext.setClickable(true);
    }

    public void onClick_buttonSettings(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonSettings");
        }

        launchSettings();
    }

    private void launchSettings() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchSettings");
        }

        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

        launchDashboard();
    }

    private void launchDashboard() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchDashboard");
        }

        finishFirstLaunch(); // when everything is ok, first launch is set to completed
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

}
