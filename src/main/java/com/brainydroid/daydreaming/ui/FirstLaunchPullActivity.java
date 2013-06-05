package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.QuestionsStorage;
import com.brainydroid.daydreaming.db.Util;
import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

// FIXME: all this activity should go in the background
@ContentView(R.layout.activity_first_launch_pull)
public class FirstLaunchPullActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunchPullActivity";

    @InjectView(R.id.firstLaunchPull_text_downloading) TextView textDownloading;
    @InjectView(R.id.firstLaunchPull_text_data_enabled) TextView dataEnabled;
    @InjectView(R.id.firstLaunchPull_text_change_settings) TextView textSettings;
    @InjectView(R.id.firstLaunchPull_buttonSettings) Button buttonSettings;
    @InjectView(R.id.firstLaunchPull_buttonNext) Button buttonNext;

    @Inject QuestionsStorage questionsStorage;

    private boolean areQuestionsDownloaded = false;

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    // FIXME : what does this do?
    private void updateView() {

        dataEnabled.setCompoundDrawablesWithIntrinsicBounds(
                (statusManager.isDataEnabled() | Build.FINGERPRINT.startsWith("generic")) ?
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
                if (statusManager.isDataEnabled()){
                    Toast.makeText(this, "Downloading questions", Toast.LENGTH_LONG).show();

                    loadQuestionsFromRes();
                    updateRequestAdjustSettings();
                }
            }

        }
    }

    private void updateRequestAdjustSettings() {

        if (Build.FINGERPRINT.startsWith("generic")){
                setAdjustSettingsOff();
        } else {
            if  (statusManager.isDataEnabled()) {
                setAdjustSettingsOff();
            } else {
                setAdjustSettingsNecessary();
            }
        }
    }

    @TargetApi(11)
    private void setAdjustSettingsNecessary() {

        textSettings.setText(R.string.firstLaunchPull_text_settings_necessary);
        textSettings.setVisibility(View.VISIBLE);
        buttonSettings.setVisibility(View.VISIBLE);
        buttonSettings.setClickable(true);
        forbidNextButton();
    }

    @TargetApi(11)
    private void forbidNextButton() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }

        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void allowNextButton() {
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

        textSettings.setVisibility(View.INVISIBLE);
        buttonSettings.setVisibility(View.INVISIBLE);
        buttonSettings.setClickable(false);
    }

    public void onClick_buttonSettings(@SuppressWarnings("UnusedParameters") View view) {
        launchSettings();
    }

    private void launchSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        launchNextActivity(FirstLaunchMeasuresActivity.class);
    }

    // FIXME: this is already in QuestionsStorage
    private void loadQuestionsFromRes() {
        textDownloading.setText("Downloading...");

        if (Build.FINGERPRINT.startsWith("generic") ) {
            Toast.makeText(this, "skipping download : emulator", Toast.LENGTH_LONG).show();

            areQuestionsDownloaded = true;
            textDownloading.setText("Questions loaded from resource");
            textDownloading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);

            allowNextButton();
        } else {
            try {
                // TODO: change this to download from the Internet
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
