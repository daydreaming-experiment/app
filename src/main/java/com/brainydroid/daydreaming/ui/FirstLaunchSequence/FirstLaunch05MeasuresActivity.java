package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.QuestionsUpdateCallback;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

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

    @InjectView(R.id.firstLaunchMeasures2_textNetworkConnection) TextView
            textNetworkConnection;
    @InjectView(R.id.firstLaunchMeasures2_textCoarseLocation) TextView
            textCoarseLocation;
    @InjectView(R.id.firstLaunchMeasures2_buttonNext) ImageButton buttonNext;

    @InjectView(R.id.firstLaunchMeasures2_text_downloading) TextView
            textDownloading;

    private boolean areQuestionsDownloading = false;

    private QuestionsUpdateCallback questionsUpdateCallback =
            new QuestionsUpdateCallback() {

                private String TAG = "QuestionsUpdateCallback";

                @Override
                public void onQuestionsUpdateStatusChange(String status) {
                    Logger.i(TAG, "Processing questionsUpdate status change " +
                            "in callback");
                    areQuestionsDownloading = false;
                    statusManager.clearQuestionsUpdateCallback();

                    if (status.equals(StatusManager.QUESTIONS_UPDATE_FAILED)) {
                        textDownloading.setText("Download failed! Are you " +
                                "connected to the internet?");
                        updateView();
                        return;
                    }

                    if (status.equals(
                            StatusManager.QUESTIONS_UPDATE_MALFORMED)) {
                        textDownloading.setText("Malformed questions " +
                                "downloaded... can you contact the " +
                                "developers?");
                        updateView();
                        return;
                    }

                    if (status.equals(
                            StatusManager.QUESTIONS_UPDATE_SUCCEEDED)) {
                        textDownloading.setText("Questions downloaded");
                        updateView();
                        return;
                    }

                    // If we can't recognised the status
                    Logger.e(TAG, "Couldn't recognise the provided " +
                            "questionsUpdate status");
                    throw new RuntimeException("Unknown questionsUpdate " +
                            "status received");
                }

            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
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

        boolean areQuestionsDownloaded = statusManager.areQuestionsUpdated();

        textCoarseLocation.setCompoundDrawablesWithIntrinsicBounds(
                statusManager.isNetworkLocEnabled() ? R.drawable.status_ok :
                        R.drawable.status_wrong, 0, 0, 0);
        textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                statusManager.isDataEnabled() ? R.drawable.status_ok :
                        R.drawable.status_wrong, 0, 0, 0);
        textDownloading.setCompoundDrawablesWithIntrinsicBounds
                (areQuestionsDownloaded ? R.drawable.status_ok :
                        R.drawable.status_wrong, 0, 0, 0);

        if (areQuestionsDownloaded) {
            allowNextButton();
        } else {
            forbidNextButton();
        }

        if (!areQuestionsDownloaded && !areQuestionsDownloading) {
            if (statusManager.isDataEnabled() &&
                    statusManager.isLastSyncLongAgo()) {
                loadQuestionsFromServer();
            }
        }
    }

    public void onClick_buttonLocationSettings(
            @SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Location settings button clicked");
        launchLocationSettings();
    }

    public void onClick_buttonNetworkSettings(
            @SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Network settings button clicked");
        launchNetworkSettings();
    }

    private void launchNetworkSettings() {
        Logger.d(TAG, "Launching network settings");
        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    private void launchLocationSettings() {
        Logger.d(TAG, "Launching location settings");
        Intent settingsIntent = new Intent(Settings
                .ACTION_LOCATION_SOURCE_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    public void onClick_buttonNext(
            @SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Next button clicked");
        finishFirstLaunch();
        launchDashBoardActivity();
    }

    @TargetApi(11)
    private void forbidNextButton() {
        Logger.d(TAG, "Forbidding buttonNext");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }

        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void allowNextButton() {
        Logger.d(TAG, "Allowing buttonNext");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(1f);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }

        buttonNext.setClickable(true);
    }

    private void loadQuestionsFromServer() {
        Logger.i(TAG, "Querying server for questions");
        textDownloading.setText("Downloading...");
        areQuestionsDownloading = true;
        statusManager.setQuestionsUpdateStatusCallback(questionsUpdateCallback);
        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }
}
