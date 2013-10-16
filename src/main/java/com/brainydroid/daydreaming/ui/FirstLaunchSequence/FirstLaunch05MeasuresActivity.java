package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.QuestionsUpdateCallback;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.ui.AlphaImageButton;
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
    @InjectView(R.id.firstLaunchMeasures2_buttonNext)
    AlphaImageButton buttonNext;

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

        explanations();

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
        Logger.d(TAG, "Launching data settings");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Enable Data");

        // set dialog message
        alertDialogBuilder
                .setMessage("Select the connectivity settings you wish to change")
                .setCancelable(true)
                .setPositiveButton("Network data",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        launchNetworkDATASettings();
                    }
                })
                .setNegativeButton("Wifi",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        launchNetworkWIFISettings();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }


    private void launchNetworkDATASettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
            settingsIntent.setComponent(cName);
        }
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }



    private void launchNetworkWIFISettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
            settingsIntent.setComponent(cName);
        }
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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

    private void forbidNextButton() {
        Logger.d(TAG, "Forbidding buttonNext");
        buttonNext.setAlpha(0.3f);
        buttonNext.setClickable(false);
    }

    private void allowNextButton() {
        Logger.d(TAG, "Allowing buttonNext");
        buttonNext.setAlpha(1f);
        buttonNext.setClickable(true);


       // Toast.makeText(this, "Content was successfully downloaded! Please click on the 'Next' button", Toast.LENGTH_SHORT).show();

    }

    private void loadQuestionsFromServer() {
        Logger.i(TAG, "Querying server for questions");
        textDownloading.setText("Downloading...");
        areQuestionsDownloading = true;
        statusManager.setQuestionsUpdateStatusCallback(questionsUpdateCallback);
        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

    public void explanations(){

        boolean areQuestionsDownloaded = statusManager.areQuestionsUpdated();

        if (!areQuestionsDownloaded) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Settings");

        // set dialog message
        alertDialogBuilder
                .setMessage("Please update your phone settings where required. We will need internet access to download some content before you can continue.")
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) { dialog.cancel(); }  });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        }

    }
}
