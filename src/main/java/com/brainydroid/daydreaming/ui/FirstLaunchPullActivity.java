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
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.QuestionsStorage;
import com.brainydroid.daydreaming.db.Util;

import java.io.IOException;
import java.io.InputStream;

public class FirstLaunchPullActivity extends SherlockActivity {

    private static String TAG = "FirstLaunchPullActivity";

    private TextView textDownloading;
    private TextView dataEnabled;
    private TextView textSettings;

    private Button buttonSettings;
    private Button buttonNext;
    private boolean areQuestionsDownloaded = false;

    private StatusManager status;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_pull);

        textDownloading = (TextView)findViewById(R.id.firstLaunchPull_text_downloading);
        textSettings = (TextView)findViewById(R.id.firstLaunchPull_text_change_settings);
        dataEnabled = (TextView)findViewById(R.id.firstLaunchPull_text_data_enabled);

        buttonSettings = (Button)findViewById(R.id.firstLaunchPull_buttonSettings);
        buttonNext = (Button)findViewById(R.id.firstLaunchPull_buttonNext);
        status = StatusManager.getInstance(this);
    }

    @Override
    public void onStart() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStart");
        }

        super.onStart();
        checkFirstLaunch();
        updateView();
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

    private void checkFirstLaunch() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] checkFirstLaunch");
        }

        if (status.isFirstLaunchCompleted()) {
            finish();
        }
    }

    private void updateView() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updateView");
        }

        dataEnabled.setCompoundDrawablesWithIntrinsicBounds(
                (status.isDataEnabled() | Build.FINGERPRINT.startsWith("generic")) ?
                        R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);
        textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                areQuestionsDownloaded ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

        if (Build.FINGERPRINT.startsWith("generic")) {
            if (!areQuestionsDownloaded) {
                loadQuestionsFromRes();
            }
        } else {
            if (areQuestionsDownloaded) {
                Toast.makeText(this, "Questions already downloaded", Toast.LENGTH_LONG).show();
            } else {
                if (status.isDataEnabled()){
                    Toast.makeText(this, "Downloading questions", Toast.LENGTH_LONG).show();

                    loadQuestionsFromRes();
                    updateRequestAdjustSettings();
                }
            }
        }
    }

    private void updateRequestAdjustSettings() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updateRequestAdjustSettings");
        }

        if (Build.FINGERPRINT.startsWith("generic")){
                setAdjustSettingsOff();
        } else {
            if  (status.isDataEnabled()) {
                setAdjustSettingsOff();
            } else {
                setAdjustSettingsNecessary();
            }
        }
    }

    @TargetApi(11)
    private void setAdjustSettingsNecessary() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setAdjustSettingsNecessary");
        }

        textSettings.setText(R.string.firstLaunchPull_text_settings_necessary);
        textSettings.setVisibility(View.VISIBLE);
        buttonSettings.setVisibility(View.VISIBLE);
        buttonSettings.setClickable(true);
        forbidNextButton();
    }

    @TargetApi(11)
    private void forbidNextButton() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] forbidNext");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }
        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void allowNextButton() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] allowNext");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(1f);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }
        buttonNext.setClickable(true);

        Toast.makeText(this, "Allowing next button", Toast.LENGTH_LONG).show();
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

        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_buttonNext");
        }

        launchMeasuresActivity();
    }

    private void launchMeasuresActivity() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchProfileActivity");
        }

        Intent intent = new Intent(this, FirstLaunchMeasuresActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void loadQuestionsFromRes() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] loadQuestionsFromRes");
        }

        textDownloading.setText("Downloading...");

        if (Build.FINGERPRINT.startsWith("generic") ) {
            Toast.makeText(this, "skipping download : emulator", Toast.LENGTH_LONG).show();

            areQuestionsDownloaded = true;
            textDownloading.setText("Questions loaded from resource");
            textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                    areQuestionsDownloaded ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

            allowNextButton();
        } else {
            try {
                // TODO: change this to download from the Internet
                QuestionsStorage questionsStorage = QuestionsStorage.getInstance(this);
                InputStream questionsIS = getResources().openRawResource(R.raw.questions);
                questionsStorage.importQuestions(Util.convertStreamToString(questionsIS));
                questionsIS.close();

                areQuestionsDownloaded = true;
                textDownloading.setText("Questions loaded from resource");
                allowNextButton();

                textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                        areQuestionsDownloaded ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);
            } catch (IOException e) {
                // Error
                Log.e(TAG, "Error importing questions from local resource", e);
                e.printStackTrace();
                textDownloading.setText("Oops, there was an error loading questions. Can you talk to the developers?");
            }
        }
    }

}
