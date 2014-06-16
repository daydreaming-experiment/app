package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.ui.AlphaImageButton;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
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
    @InjectView(R.id.firstLaunchMeasures2_explanation_network_connection)
            TextView explanationNetworkConnection;
    @InjectView(R.id.firstLaunchMeasures2_explanation_coarse_location)
            TextView explanationCoarseLocation;
    @InjectView(R.id.firstLaunchMeasures2_good_to_go) TextView goodToGoView;

    @InjectResource(R.string.firstLaunchMeasures2_explanation_network_connection_ok)
            String explanationNetworkConnectionOk;
    @InjectResource(R.string.firstLaunchMeasures2_explanation_network_connection_bad)
            String explanationNetworkConnectionBad;
    @InjectResource(R.string.firstLaunchMeasures2_explanation_coarse_location_ok)
            String explanationCoarseLocationOk;
    @InjectResource(R.string.firstLaunchMeasures2_explanation_coarse_location_bad)
            String explanationCoarseLocationBad;
    @InjectResource(R.string.firstLaunchMeasures2_good_to_go_ok) String goodToGoOk;
    String goodToGoBad = "";

    @InjectView(R.id.firstLaunchMeasures2_buttonNext)
            AlphaImageButton buttonNext;
    @InjectView(R.id.firstLaunchMeasures2_mainScrollView)
            ScrollView mainScrollView;

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        private String TAG = "FirstLaunch05MeasuresActivity networkReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Were we called because Internet just became available?
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Logger.d(TAG, "networkReceiver started for CONNECTIVITY_ACTION");
                asyncUpdateView();
            }
        }
    };

    private IntentFilter networkIntentFilter =
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();
        forbidNextButton();
        Logger.d(TAG, "Registering networkReceiver");
        registerReceiver(networkReceiver, networkIntentFilter);
        asyncUpdateView();
    }

    @Override
    public void onPause() {
        Logger.v(TAG, "Pausing");
        Logger.d(TAG, "Unregistering networkReceiver");
        unregisterReceiver(networkReceiver);
        super.onPause();
    }

    private void asyncUpdateView() {
        Logger.d(TAG, "Asynchronously updating view of settings");

        final boolean isCoarseLocEnabled = statusManager.isNetworkLocEnabled();
        final boolean isDataEnabled = statusManager.isDataEnabled();

        Runnable updateView = new Runnable() {

            private String TAG = "Runnable updateView";

            @Override
            public void run() {
                Logger.d(TAG, "Running task: update of view");

                textCoarseLocation.setCompoundDrawablesWithIntrinsicBounds(
                        isCoarseLocEnabled ? R.drawable.status_ok :
                                R.drawable.status_wrong, 0, 0, 0);
                explanationCoarseLocation.setText(isCoarseLocEnabled ?
                        Html.fromHtml(explanationCoarseLocationOk) :
                        Html.fromHtml(explanationCoarseLocationBad));
                textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                        isDataEnabled ? R.drawable.status_ok :
                                R.drawable.status_wrong, 0, 0, 0);
                explanationNetworkConnection.setText(isDataEnabled ?
                        Html.fromHtml(explanationNetworkConnectionOk) :
                        Html.fromHtml(explanationNetworkConnectionBad));

                if (isCoarseLocEnabled && isDataEnabled) {
                    goodToGoView.setText(goodToGoOk);
                    allowNextButton();
                } else {
                    goodToGoView.setText(goodToGoBad);
                    forbidNextButton();
                }
            }
        };

        mainScrollView.post(updateView);
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
        Logger.d(TAG, "Launching network settings dialog");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Enable Data");

        // set dialog message
        alertDialogBuilder
                .setMessage("Select the connectivity settings you wish to change")
                .setCancelable(true)
                .setPositiveButton("Network data",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Logger.d(TAG, "Launching data settings");
                        launchNetworkDataSettings();
                    }
                })
                .setNegativeButton("Wifi",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Logger.d(TAG, "Launching wifi settings");
                        launchNetworkWifiSettings();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void launchNetworkDataSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
            settingsIntent.setComponent(cName);
        }
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    private void launchNetworkWifiSettings() {
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

        String measures_warning = getResources().getString(R.string.firstLaunchMeasures2_text_warning);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Agreement");

        // set dialog message
        alertDialogBuilder
                .setMessage(measures_warning)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finishFirstLaunch();
                        launchDashBoardActivity();                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();        }



    @TargetApi(11)
    private void forbidNextButton() {
        Logger.d(TAG, "Forbidding buttonNext");
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaButton exists),
        // hence the @TargetApi(11) above.
        buttonNext.setAlpha(0.3f);
        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void allowNextButton() {
        Logger.d(TAG, "Allowing buttonNext");
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaButton exists),
        // hence the @TargetApi(11) above.
        buttonNext.setAlpha(1f);
        if (!buttonNext.isClickable()) {
            buttonNext.setClickable(true);
            // When hitting 'back' from the dashboard right after the first launch, the activity stack is emptied
            // and goes through this activity. Let's not show this alert.
            if (!statusManager.isFirstLaunchCompleted() && !isExperimentModeActivatedDirty()) {
                Toast.makeText(this, goodToGoOk, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
