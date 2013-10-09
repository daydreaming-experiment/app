package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionsStorage;
import com.brainydroid.daydreaming.db.Util;
import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Activity at first launch
 * Checking if App Settings are correctly set (connectivity, location)
 *
 * In first launch sequence of apps
 *
 * Previous activity :  FirstLaunch04PersonalityQuestionnaireActivity
 * This activity     :  FirstLaunch05MeasuresActivity
 * Next activity     :  FirstLaunch06PullActivity
 *
 */
@ContentView(R.layout.activity_first_launch_measures)
public class FirstLaunch05MeasuresActivity extends FirstLaunchActivity {

    private static String TAG = "FirstLaunch05MeasuresActivity";

    @InjectView(R.id.firstLaunchMeasures2_textNetworkConnection) TextView textNetworkConnection;
    @InjectView(R.id.firstLaunchMeasures2_textCoarseLocation) TextView textCoarseLocation;
    @InjectView(R.id.firstLaunchMeasures2_buttonNetworkSettings) Button buttonNetworkSettings;
    @InjectView(R.id.firstLaunchMeasures2_buttonLocationSettings) Button buttonLocationSettings;
    @InjectView(R.id.firstLaunchMeasures2_buttonNext) ImageButton buttonNext;

    @InjectView(R.id.firstLaunchMeasures2_text_downloading) TextView textDownloading;
    // @InjectView(R.id.firstLaunchPull_text_data_enabled) TextView dataEnabled;
    // @InjectView(R.id.firstLaunchPull_text_change_settings) TextView textSettings;

    @Inject
    QuestionsStorage questionsStorage;

    private boolean areQuestionsDownloaded = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);



        //  if (statusManager.isNetworkLocEnabled()) {
      //      Logger.i(TAG, "Network location is enabled -> jumping to " +
      //              "dashboard");
      //      launchDashboard();
      //  } else {
      //      Logger.v(TAG, "Network location not enabled");
      //  }
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
        updateView();
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();
        updateView();
    }

    private void updateView() {
        Logger.d(TAG, "Updating view of settings");

        textCoarseLocation.setCompoundDrawablesWithIntrinsicBounds(
                statusManager.isNetworkLocEnabled() ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);
        textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                statusManager.isDataEnabled() ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);

        //updateRequestAdjustSettings();
        buttonNext.setClickable(true); // just to run without data

        textDownloading.setCompoundDrawablesWithIntrinsicBounds(areQuestionsDownloaded ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);

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
        if ((statusManager.isNetworkLocEnabled()) && (statusManager.isDataEnabled()) || (Build.FINGERPRINT.startsWith("generic"))) {
            Logger.i(TAG, "Settings are good");
            setAdjustSettingsOff();
        } else {
            Logger.i(TAG, "Settings are bad");
            setAdjustSettingsNecessary();
        }
    }

    @TargetApi(11)
    private void setAdjustSettingsNecessary() {
        Logger.i(TAG, "Disabling button to move on");
   //     textSettings.setText(R.string.firstLaunchMeasures_text_settings_necessary);
   //     textSettings.setVisibility(View.VISIBLE);
   //     buttonSettings.setVisibility(View.VISIBLE);
   //     buttonSettings.setClickable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }

        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void setAdjustSettingsOff() {
        Logger.i(TAG, "Allowing button to move on");
     //   textSettings.setVisibility(View.INVISIBLE);
     //   buttonSettings.setVisibility(View.INVISIBLE);
     //   buttonSettings.setClickable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(1f);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }

        buttonNext.setClickable(true);
    }

    public void onClick_buttonLocationSettings(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Settings button clicked");
        launchLocationSettings();
    }
    public void onClick_buttonNetworkSettings(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Settings button clicked");
        launchNetworkSettings();
    }

    private void launchNetworkSettings() {
        Logger.d(TAG, "Launching Network settings");
        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    private void launchLocationSettings() {
        Logger.d(TAG, "Launching Location settings");
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }


    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Next button clicked");
        finishFirstLaunch();
        launchDashBoardActivity();
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



    // FIXME: this is already in QuestionsStorage
    private void loadQuestionsFromRes() {
        textDownloading.setText("Downloading...");

        if (Build.FINGERPRINT.startsWith("generic") ) {
            Toast.makeText(this, "skipping download : emulator", Toast.LENGTH_LONG).show();

            areQuestionsDownloaded = true;
            textDownloading.setText("Questions loaded from resource");
            textDownloading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.status_ok, 0, 0, 0);

            allowNextButton();
        } else {
            try {
                // TODO: change this to download from the Internet
                InputStream questionsIS = getResources().openRawResource(R.raw.questions);
                questionsStorage.importQuestions(Util.convertStreamToString(questionsIS));
                questionsIS.close();

                areQuestionsDownloaded = true;
                textDownloading.setText("Questions loaded from internal resource");
                allowNextButton();

                textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                        areQuestionsDownloaded ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);
            } catch (IOException e) {
                // Error
                Log.e(TAG, "Error importing questions from local resource", e);
                e.printStackTrace();
                textDownloading.setText("Oops, there was an error loading questions. Can you talk to the developers?");
            }
        }
    }


}
