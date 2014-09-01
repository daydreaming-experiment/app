package com.brainydroid.daydreaming.ui.dashboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationPointService;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.ui.AlphaLinearLayout;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.google.inject.Inject;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_dashboard)
public class DashboardActivity extends RoboFragmentActivity {

    private static String TAG = "DashboardActivity";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    @Inject ParametersStorage parametersStorage;
    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;

    @InjectView(R.id.dashboard_ExperimentTimeElapsed2)
    TextView timeElapsedTextView;
    @InjectView(R.id.dashboard_ExperimentResultsIn2) TextView timeToGoTextView;
    @InjectView(R.id.button_test_poll) Button testProbeButton;
    @InjectView(R.id.button_reload_parameters) Button testReloadButton;
    @InjectView(R.id.dashboard_about_layout) AlphaLinearLayout aboutLayout;

    @InjectView(R.id.dashboard_textExperimentStatus) TextView expStatus;

    @InjectView(R.id.dashboard_no_params_text) TextView
            textNetworkConnection;
    @InjectView(R.id.dashboard_main_layout)
    LinearLayout dashboard_main_layout;
    private boolean testModeThemeActivated = false;

    IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_STRING_ACTIVITY)) {
                Logger.d(TAG, "networkReceiver started for CONNECTIVITY_ACTION");
                setExperimentStatusText();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        checkTestMode();
        super.onCreate(savedInstanceState);
        checkFirstLaunch();

        if (activityReceiver != null) {
            registerReceiver(activityReceiver, intentFilter);
        }

        setRobotoFont(this);
    }

    @TargetApi(11)
    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");

        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaLinearLayout exists),
        // hence the @TargetApi(11) above.
        aboutLayout.setAlpha(0.3f);
        aboutLayout.setClickable(false);

        checkExperimentModeActivatedDirty();
        updateRunningTime();
        updateChromeMode();
        setExperimentStatusText();
        super.onStart();
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        checkExperimentModeActivatedDirty();
        if (!statusManager.areParametersUpdated()) {
            registerReceiver(activityReceiver, intentFilter);
            asyncUpdateView();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        Logger.v(TAG, "Pausing");
        Logger.d(TAG, "Unregistering dashboardReceiver");
        try {
            unregisterReceiver(activityReceiver);
        } catch(IllegalArgumentException e) {}
        super.onPause();
    }

    /**
     * Launching app settings activity
     */
    public void  onClick_openAppSettings(
            @SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    /**
     * Launching term activity without enabling buttons
     * Only exit is pressing back button
     */
    public void onClick_ReOpenTerms(
            @SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    /**
     * Launching a description activity without possibility to click next
     * Only exit is pressing back button.
     */
    public void onClick_ReOpenDescription(
            @SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, DescriptionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    //TODO: Layout of an about activity
    public void  onClick_OpenAboutActivity(
            @SuppressWarnings("UnusedParameters") View view){
//        Intent intent = new Intent(this, AboutActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    /**
     * Launching a credit activity
     */
    public void onClick_OpenCredits(
            @SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    //TODO: Decide what should happen when results button is clicked.
    public void  onClick_SeeResults(
            @SuppressWarnings("UnusedParameters") View view){
        Toast.makeText(this, "Not yet!", Toast.LENGTH_SHORT).show();
    }

    public void  onClick_OpenNetworkSettings(
            @SuppressWarnings("UnusedParameters") View view){
        launchNetworkSettings();
    }

    private void updateRunningTime() {
        // TODO: this should update automatically once parameters are updated (through an observer)
        if (!statusManager.areParametersUpdated()) {
            Logger.i(TAG, "Parameters not downloaded yet, not updating timestamp");
            return;
        }
        // Set the running time from last trustworthy timestamp
        long lastNtpTimestamp = statusManager.getLatestNtpTime();
        updateRunningTimeFromTimestamp(lastNtpTimestamp);

        // And try to update the timestamp
        SntpClientCallback callback = new SntpClientCallback() {

            private String TAG = "Dashboard SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                Logger.d(TAG, "NTP request completed");

                if (sntpClient != null) {
                    Logger.i(TAG, "NTP request successful, " +
                            "updating running time views in dashboard");
                    updateRunningTimeFromTimestamp(sntpClient.getNow());
                } else {
                    Logger.i(TAG, "NTP request failed, sntpClient is null");
                }
            }
        };

        sntpClient.asyncRequestTime(callback);
    }

    // TODO: disable "see results" button before the results are available
    // TODO: something should happen once the results are available
    private void updateRunningTimeFromTimestamp(long timestampNow) {
        Logger.d(TAG, "Updating running time with timestamp {}", timestampNow);
        long expStartTimestamp = statusManager.getExperimentStartTimestamp();

        // In case our very first NTP request didn't succeed,
        // or hasn't yet finished
        if (expStartTimestamp <= 0) {
            expStartTimestamp = timestampNow - 1;
        }

        final int daysElapsed = (int)((timestampNow - expStartTimestamp) /
                (24 * 60 * 60 * 1000));
        Logger.i(TAG, "Days elapsed: {}", daysElapsed);

        final int daysToGo = parametersStorage.getExpDuration() - daysElapsed;
        Logger.i(TAG, "Days to go: {}", daysToGo);

        Runnable timeElapsedUpdater = new Runnable() {

            private String TAG = "timeElapsedUpdater Runnable";

            @Override
            public void run() {
                Logger.d(TAG, "Updating time elapsed view");
                timeElapsedTextView.setText(String.valueOf(daysElapsed));
            }

        };

        Runnable timeToGoUpdater = new Runnable() {

            private String TAG = "timeToGoUpdater Runnable";

            @Override
            public void run() {
                Logger.d(TAG, "Updating time to go view");
                timeToGoTextView.setText(String.valueOf(daysToGo));
            }

        };

        timeElapsedTextView.post(timeElapsedUpdater);
        timeToGoTextView.post(timeToGoUpdater);
    }

    /**
     * Dashboard is main launcher. At start up of app, the whole firstLaunch
     * sequence is run. If they were already ran, user directly end up on
     * dashboard layout.
     */

    //TODO[Vincent] Think of a way to deal with experiment being paused (status : running paused stopped)
    protected void setExperimentStatusText() {
        View dashboard_TimeBox_layout = findViewById(R.id.dashboard_TimeBox_layout);
        View dashboard_TimeBox_no_param = findViewById(R.id.dashboard_TimeBox_layout_no_params);
        if (statusManager.expIsRunning()){
            expStatus.setText(R.string.dashboard_text_exp_running);
            dashboard_TimeBox_layout.setVisibility(View.VISIBLE);
            dashboard_TimeBox_no_param.setVisibility(View.INVISIBLE);
            updateRunningTime();
        } else {
            expStatus.setText(R.string.dashboard_text_exp_stopped);
            dashboard_TimeBox_layout.setVisibility(View.INVISIBLE);
            dashboard_TimeBox_no_param.setVisibility(View.VISIBLE);
        }
    }

    protected void checkFirstLaunch() {
        if (!statusManager.isFirstLaunchCompleted()) {
            Logger.i(TAG, "First launch not completed -> starting first " +
                    "launch sequence and finishing this activity");
            Intent intent = new Intent(this, FirstLaunch00WelcomeActivity.class);
            // No need for Intent.FLAG_ACTIVITY_CLEAR_TOP here since FirstLaunch00WelcomeActivity
            // is "noHistory" and as such never exists in the back stack.
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            Logger.v(TAG, "First launch completed");
        }
    }

    /**
     * Launching poll from dashboard (debug)
     */
    public void runPollNow(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Launching a debug poll");

        Intent pollIntent = new Intent(this, SchedulerService.class);
        pollIntent.putExtra(SchedulerService.SCHEDULER_DEBUGGING, true);
        startService(pollIntent);

        Toast.makeText(this, "Now wait for 5 secs", Toast.LENGTH_SHORT).show();
    }

    public void reloadParametersKeepProfileAnswers(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Resetting parameters and profile_id, but keeping profile answers");

        if (!statusManager.isDataEnabled()) {
            Toast.makeText(this, "You're not connected to the internet!", Toast.LENGTH_SHORT).show();
            return;
        }
        statusManager.resetParametersKeepProfileAnswers();
        Intent syncIntent = new Intent(this, SyncService.class);
        syncIntent.putExtra(SyncService.DEBUG_SYNC, true);
        startService(syncIntent);
    }

    public void setRobotoFont(Activity activity){
        ViewGroup godfatherView =
                (ViewGroup)activity.getWindow().getDecorView();
        FontUtils.setRobotoFont(activity, godfatherView);
    }

    private void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.daydreamingTheme);
            testModeThemeActivated = false;
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.daydreamingTestTheme);
            testModeThemeActivated = true;
        }
    }

    private void updateChromeMode() {
        Logger.d(TAG, "Updating chrome depending on test mode");
        if (statusManager.getCurrentMode() == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production chrome");
            testProbeButton.setVisibility(View.INVISIBLE);
            testProbeButton.setClickable(false);
            testReloadButton.setVisibility(View.INVISIBLE);
            testReloadButton.setClickable(false);
        } else {
            Logger.d(TAG, "Setting test chrome");
            testProbeButton.setVisibility(View.VISIBLE);
            testProbeButton.setClickable(true);
            testReloadButton.setVisibility(View.VISIBLE);
            testReloadButton.setClickable(true);
        }
    }

    private void checkExperimentModeActivatedDirty() {
        if ((statusManager.getCurrentMode() == StatusManager.MODE_TEST && !testModeThemeActivated)
            || (statusManager.getCurrentMode() == StatusManager.MODE_PROD && testModeThemeActivated)) {
            Logger.w(TAG, "Test/production mode theme discrepancy, " +
                    "meaning a vicious activity path didn't let us update");
            finish();
        } else {
            Logger.v(TAG, "No test mode theming discrepancy");
        }
    }


    private void asyncUpdateView() {
        Logger.d(TAG, "Asynchronously updating view of settings");

        final boolean isDataEnabled = statusManager.isDataEnabled();

        Runnable updateView = new Runnable() {

            private String TAG = "Runnable updateView";

            @Override
            public void run() {
                Logger.d(TAG, "Running task: update of view");

                textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                        isDataEnabled ? R.drawable.status_ok :
                                R.drawable.status_wrong, 0, 0, 0
                );
                finishFirstLaunch();
                setExperimentStatusText();

            }
        };
        dashboard_main_layout.post(updateView);
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
                .setPositiveButton("Network data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Logger.d(TAG, "Launching data settings");
                        launchNetworkDataSettings();
                    }
                })
                .setNegativeButton("Wifi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(settingsIntent);
    }

    private void launchNetworkWifiSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
            settingsIntent.setComponent(cName);
        }
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(settingsIntent);
    }

    protected void finishFirstLaunch() {
        Logger.i(TAG, "Setting first launch to finished");

        statusManager.setFirstLaunchCompleted();

        SntpClientCallback callback = new SntpClientCallback() {

            private String TAG = "FirstLaunch SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                Logger.d(TAG, "NTP request completed");

                if (sntpClient != null) {
                    Logger.i(TAG, "NTP request successful, " +
                            "setting timestamp for start of experiment");
                    statusManager.setExperimentStartTimestamp(
                            sntpClient.getNow());
                } else {
                    Logger.i(TAG, "NTP request failed, sntpClient is null");
                }
            }

        };

        sntpClient.asyncRequestTime(callback);

        Intent syncServiceIntent = new Intent(this, SyncService.class);
        Logger.d(TAG, "Starting SyncService");
        startService(syncServiceIntent);

        // SchedulerService will be started when the SyncService successfully updates parameters
        Intent locationPointServiceIntent = new Intent(this,
                LocationPointService.class);
        Logger.d(TAG, "Starting LocationPointService");
        startService(locationPointServiceIntent);
    }


}
