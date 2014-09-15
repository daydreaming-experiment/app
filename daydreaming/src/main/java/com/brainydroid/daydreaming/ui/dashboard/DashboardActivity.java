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
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
import com.brainydroid.daydreaming.ui.AlphaButton;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.brainydroid.daydreaming.ui.sequences.PageActivity;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_dashboard)
public class DashboardActivity extends RoboFragmentActivity implements View.OnClickListener {

    private static String TAG = "DashboardActivity";

    @Inject ParametersStorage parametersStorage;
    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;
    @Inject SequenceBuilder sequenceBuilder;
    @Inject Sequence probe;

    @InjectView(R.id.dashboard_main_layout) RelativeLayout dashboardMainLayout;
    @InjectView(R.id.dashboard_ExperimentTimeElapsed2)
    TextView timeElapsedTextView;
    @InjectView(R.id.dashboard_ExperimentResultsIn2) TextView timeToGoTextView;
    @InjectView(R.id.button_test_sync) Button testSyncButton;
    @InjectView(R.id.button_test_poll) Button testProbeButton;
    @InjectView(R.id.button_reload_parameters) Button testReloadButton;
    @InjectView(R.id.dashboard_glossary_layout) RelativeLayout glossaryLayout;
    @InjectView(R.id.dashboard_textExperimentStatus) TextView expStatus;
    @InjectView(R.id.dashboard_no_params_text) TextView textNetworkConnection;
    @InjectView(R.id.dashboard_ExperimentTimeElapsed2days) TextView elapsedTextDays;
    @InjectView(R.id.dashboard_ExperimentResultsIn2days) TextView toGoTextDays;
    @InjectView(R.id.dashboard_ExperimentResultsButton) AlphaButton resultsButton;
    @InjectView(R.id.dashboard_debug_information) TextView debugInfoText;

    @InjectResource(R.string.dashboard_text_days) String textDays;
    @InjectResource(R.string.dashboard_text_day) String textDay;
    @InjectResource(R.integer.dashboard_swipe_velocity_threshold) int SWIPE_VELOCITY_THRESHOLD;

    private boolean testModeThemeActivated = false;
    private int daysToGo = -1;
    private long lastParametersUpdateAttempt = -1;
    private Timer updateTimer = null;

    List<Integer> showcasesId;
    List<String[]> showcasesTexts;
    int showcaseViewIndex = 0;
    boolean UNIQUE = true;

    IntentFilter parametersUpdateIntentFilter = new IntentFilter(StatusManager.ACTION_PARAMETERS_STATUS_CHANGE);
    IntentFilter networkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    public GestureDetector gestureDetector;
    public View.OnTouchListener gestureListener;
    public GestureDetector.SimpleOnGestureListener simpleOnGestureListener;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(StatusManager.ACTION_PARAMETERS_STATUS_CHANGE) ||
                    action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Logger.d(TAG, "receiver started for ACTION_PARAMETERS_STATUS_CHANGE or CONNECTIVITY_ACTION");
                updateExperimentStatus();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        checkTestMode();
        super.onCreate(savedInstanceState);
        checkFirstLaunch();
        setSideSwipeListener();
        setRobotoFont(this);
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");

        checkExperimentModeActivatedDirty();
        updateRunningTime();
        updateChromeMode();
        updateExperimentStatus();
        super.onStart();

        populateShowcaseViews();
        if (statusManager.areParametersUpdated()){
             launchShowCaseViewSequence(UNIQUE);
        }

    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        statusManager.checkLatestSchedulerWasAgesAgo();
        checkExperimentModeActivatedDirty();
        if (!statusManager.areParametersUpdated()) {
            Logger.v(TAG, "Parameters not yet updated, registering broadcast receiver");
            if (statusManager.isDataEnabled()) {
                Logger.v(TAG, "Internet enabled, so also launching parameters update");
                lastParametersUpdateAttempt = -1;
                launchParametersUpdate();
            }
            registerReceiver(receiver, parametersUpdateIntentFilter);
            registerReceiver(receiver, networkIntentFilter);
        }
        updateExperimentStatus();
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.v(TAG, "Pausing");
        Logger.d(TAG, "Unregistering receiver for all intent filters");
        try {
            unregisterReceiver(receiver);
        } catch(IllegalArgumentException e) {
            Logger.v(TAG, "Receiver is not registered, so not unregistering");
        }
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
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
     * Launching beginning questionnaires activity
     */
    public void openBeginQuestionnaires(){
        Intent intent = new Intent(this, BeginQuestionnairesActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
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

    public void  onClick_OpenGlossaryActivity(
            @SuppressWarnings("UnusedParameters") View view){
        if (statusManager.areParametersUpdated()) {
            Intent intent = new Intent(this, GlossaryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
        }
    }

    public void onClick_OpenAboutActivity(
            @SuppressWarnings("UnusedParameters") View view){
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    public void  onClick_SeeResults(
            @SuppressWarnings("UnusedParameters") View view){
        Logger.v(TAG, "Results button clicked");

        if (!statusManager.isExpRunning()) {
            Logger.v(TAG, "Experiment not yet running => aborting");
            Toast.makeText(this, "Experiment hasn't started yet!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (daysToGo > 0) {
            if (statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
                Logger.i(TAG, "Test mode activated => allowing results");
                Toast.makeText(this, "Test mode => allowing results", Toast.LENGTH_SHORT).show();
            } else {
                Logger.v(TAG, "Still {} day(s) to go and test mode not activated => aborting",
                        daysToGo);
                String msg = "Still " + daysToGo + " day";
                if (daysToGo > 1) {
                    msg += "s";
                }
                msg += " to wait before the results";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (statusManager.isDataEnabled()) {
            Logger.d(TAG, "Data enabled, transitioning to results");
            Intent resultsIntent = new Intent(this, ResultsActivity.class);
            startActivity(resultsIntent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else {
            Logger.v(TAG, "No data connection => aborting");
            Toast.makeText(this, "You're not connected to the internet!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void  onClick_OpenNetworkSettings(
            @SuppressWarnings("UnusedParameters") View view){
        launchNetworkSettings();
    }

    private void updateRunningTime() {
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

    // TODO: user should be notified once the results are available
    private void updateRunningTimeFromTimestamp(long timestampNow) {
        if (!statusManager.areParametersUpdated()) {
            Logger.v(TAG, "Parameters not updated, not setting running time views");
            return;
        }

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

        daysToGo = parametersStorage.getExpDuration() - daysElapsed;
        Logger.i(TAG, "Days to go: {}", daysToGo);

        Runnable timeElapsedUpdater = new Runnable() {

            private String TAG = "timeElapsedUpdater Runnable";

            @Override
            public void run() {
                Logger.d(TAG, "Updating time elapsed view");
                timeElapsedTextView.setText(String.valueOf(daysElapsed));
                elapsedTextDays.setText(daysElapsed != 1 ? textDays : textDay);
            }

        };

        Runnable timeToGoUpdater = new Runnable() {

            private String TAG = "timeToGoUpdater Runnable";

            @Override
            public void run() {
                Logger.d(TAG, "Updating time to go view");
                timeToGoTextView.setText(String.valueOf(daysToGo));
                toGoTextDays.setText(daysToGo != 1 ? textDays : textDay);
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

    @TargetApi(11)
    protected synchronized void updateExperimentStatus() {
        View dashboard_TimeBox_layout = findViewById(R.id.dashboard_TimeBox_layout);
        View dashboard_TimeBox_no_param = findViewById(R.id.dashboard_TimeBox_layout_no_params);
        View dashboardNetworkSettingsButton = findViewById(R.id.dashboard_network_settings_button);

        if (statusManager.isExpRunning()) {
            Logger.v(TAG, "Experiment is running, setting views accordingly");
            expStatus.setText(R.string.dashboard_text_exp_running);
            dashboard_TimeBox_layout.setVisibility(View.VISIBLE);
            dashboard_TimeBox_no_param.setVisibility(View.INVISIBLE);

            // Lint erroneously catches this as a call that requires API >= 11
            // (which is exactly why AlphaLinearLayout exists),
            // hence the @TargetApi(11) above.
            glossaryLayout.setAlpha(1f);
            glossaryLayout.setClickable(true);

            updateRunningTime();

            if (updateTimer != null) {
                updateTimer.cancel();
                updateTimer = null;
            }
        } else {
            Logger.v(TAG, "Experiment is NOT running, setting views accordingly");
            expStatus.setText(R.string.dashboard_text_exp_stopped);
            dashboard_TimeBox_layout.setVisibility(View.INVISIBLE);
            dashboard_TimeBox_no_param.setVisibility(View.VISIBLE);

            // Lint erroneously catches this as a call that requires API >= 11
            // (which is exactly why AlphaLinearLayout exists),
            // hence the @TargetApi(11) above.
            glossaryLayout.setAlpha(0.3f);
            glossaryLayout.setClickable(false);

            if (statusManager.isDataEnabled()) {
                long now = Calendar.getInstance().getTimeInMillis();
                if (now - lastParametersUpdateAttempt < 2 * 60 * 1000) {
                    // Last update short time ago: "will retry in X seconds" + reset timer
                    long secondsLeft = (2 * 60 * 1000 - (now - lastParametersUpdateAttempt)) / 1000;
                    textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.status_wrong, 0, 0, 0);
                    textNetworkConnection.setText(
                            getString(R.string.dashboard_text_error_will_retry1)
                                    + " " + secondsLeft + " "
                                    + getString(R.string.dashboard_text_error_will_retry2));
                    dashboardNetworkSettingsButton.setVisibility(View.INVISIBLE);

                    if (updateTimer == null) {
                        updateTimer = new Timer("updateTimer");
                        updateTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                updateExperimentStatusViewsForceUIThread();
                            }
                        }, 1000, 1000);
                    }
                } else {
                    // Last update long ago: relaunch, and say "updating"
                    textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.status_loading, 0, 0, 0);
                    textNetworkConnection.setText(
                            getString(R.string.dashboard_text_parameters_updating));
                    dashboardNetworkSettingsButton.setVisibility(View.INVISIBLE);

                    lastParametersUpdateAttempt = now;
                    launchParametersUpdate();

                    if (updateTimer != null) {
                        updateTimer.cancel();
                        updateTimer = null;
                    }
                }
            } else {
                textNetworkConnection.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.status_wrong, 0, 0, 0);
                textNetworkConnection.setText(getString(R.string.dashboard_text_enable_internet));
                dashboardNetworkSettingsButton.setVisibility(View.VISIBLE);

                if (updateTimer != null) {
                    updateTimer.cancel();
                    updateTimer = null;
                }
            }
        }

        if (statusManager.areResultsAvailable()) {
            resultsButton.setAlpha(1f);
            resultsButton.setClickable(true);
        } else {
            resultsButton.setAlpha(0.3f);
            resultsButton.setClickable(false);
        }

        debugInfoText.setText(statusManager.getDebugInfoString());
        updateBeginQuestionnairesButton();
    }

    public void updateExperimentStatusViewsForceUIThread() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateExperimentStatus();
            }
        });
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

    private void launchParametersUpdate() {
        Logger.d(TAG, "Launching full sync service to update parameters");

        Logger.d(TAG, "Starting SyncService");
        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

    /**
     * Launching poll from dashboard (debug)
     */
    public void runPollNow(@SuppressWarnings("UnusedParameters") View view) {
        if (statusManager.isExpRunning()) {
            Logger.d(TAG, "Launching a debug poll");

            Intent pollIntent = new Intent(this, SchedulerService.class);
            pollIntent.putExtra(SchedulerService.SCHEDULER_DEBUGGING, true);
            startService(pollIntent);

            Toast.makeText(this, "Now wait for 5 secs", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Parameters aren't loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void runSyncNow(View view) {
        if (statusManager.isExpRunning()) {
            Logger.d(TAG, "Launching debug sync now");

            if (!statusManager.isDataEnabled()) {
                Toast.makeText(this, "You're not connected to the internet!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent syncIntent = new Intent(this, SyncService.class);
            syncIntent.putExtra(SyncService.DEBUG_SYNC, true);
            startService(syncIntent);
        } else {
            Toast.makeText(this, "Parameters aren't loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void reloadParametersKeepProfileAnswers(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Resetting parameters and profile_id, but keeping profile answers");

        if (!statusManager.isDataEnabled()) {
            Toast.makeText(this, "You're not connected to the internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register receiver to get the update
        registerReceiver(receiver, parametersUpdateIntentFilter);
        statusManager.resetParametersKeepProfileAnswers();

        lastParametersUpdateAttempt = -1;
        updateExperimentStatus();
        // SyncService is started from inside updateExperimentStatus
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
            testSyncButton.setVisibility(View.INVISIBLE);
            testSyncButton.setClickable(false);
            debugInfoText.setVisibility(View.INVISIBLE);
        } else {
            Logger.d(TAG, "Setting test chrome");
            testProbeButton.setVisibility(View.VISIBLE);
            testProbeButton.setClickable(true);
            testReloadButton.setVisibility(View.VISIBLE);
            testReloadButton.setClickable(true);
            testSyncButton.setVisibility(View.VISIBLE);
            testSyncButton.setClickable(true);
            debugInfoText.setVisibility(View.VISIBLE);
            debugInfoText.setText(statusManager.getDebugInfoString());
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

    @TargetApi(11)
    private synchronized void updateBeginQuestionnairesButton() {

        if (statusManager.areParametersUpdated()) {
            final AlphaButton btn = (AlphaButton)findViewById(R.id.dashboard_begin_questionnaires_button);
            if (!statusManager.areBeginQuestionnairesCompleted()) {
                final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                animation.setDuration(1000); // duration - half a second
                animation.setInterpolator(new AccelerateDecelerateInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                btn.startAnimation(animation);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        openBeginQuestionnaires();
                    }
                });
                btn.setClickable(true);
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaButton exists),
                // hence the @TargetApi(11) above.
                btn.setAlpha(1f);
            } else {
                btn.setClickable(false);
                btn.clearAnimation();
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaButton exists),
                // hence the @TargetApi(11) above.
                btn.setAlpha(0.3f);
            }
        }
    }

    public synchronized void setSideSwipeListener() {
        Logger.d(TAG, "Setting Swipe Listener");

        simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(TAG, "Swipe Event detected");
                if (statusManager.areParametersUpdated()){
                    Logger.d(TAG, "Swipe Event used");
                    if ( Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
                        // set title
                        alertDialogBuilder.setTitle("Self Report");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Do you want to report a daydreaming experience?")
                                .setCancelable(false)
                                .setPositiveButton("Yes!",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        createProbe();
                                        launchProbeIntent();
                                    }
                                })
                                .setNegativeButton("Nope",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }

            }
        };

        gestureDetector = new GestureDetector(DashboardActivity.this, simpleOnGestureListener);
        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.d(TAG, "Dashboard Touch event");
                return gestureDetector.onTouchEvent(event);
            }
        };

        dashboardMainLayout.setOnClickListener(DashboardActivity.this);
        dashboardMainLayout.setOnTouchListener(gestureListener);
    }

    public Sequence createProbe() {
        probe = sequenceBuilder.buildSave(Sequence.TYPE_PROBE);
        probe.setSelfInitiated(true);
        return probe;
    }

    private void launchProbeIntent() {
        Logger.d(TAG, "Creating probe Intent");
        Intent probeIntent = new Intent(this, PageActivity.class);
        // Set the id of the probe to start
        probeIntent.putExtra(PageActivity.EXTRA_SEQUENCE_ID, probe.getId());
        // Create a new task. The rest is defined in the App manifest.
        probeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(probeIntent);
    }

    @Override
    public void onClick(View v) {}

    public void populateShowcaseViews() {
        Logger.d(TAG, "Populating ShowCaseViews");
        // populate Showcase List
        showcasesId = new ArrayList<Integer>();
        showcasesTexts = new ArrayList<String[]>();
        addShowCaseItem(R.id.dashboard_begin_questionnaires_button,"Questions", "When blinking, there are questions for you!");
        addShowCaseItem(R.id.dashboard_openAppSettings, "Settings", "Set the app parameters");
        addShowCaseItem(R.id.dashboard_glossary_button, "Glossary", "A list of useful definitions");
        addShowCaseItem(R.id.dashboard_ExperimentResultsButton, "Results", "When available, a detailed report for you here!");
        addShowCaseItem(R.id.dashboard_TimeBox_layout, "Self Report", "Swipe right for a self report");
        addShowCaseItem(R.id.dashboard_ExperimentTimeElapsed2, "Time Elapsed", "The duration you have been running this app");
        addShowCaseItem(R.id.dashboard_ExperimentResultsIn2, "Time Left", "The duration left before you get results");

    }

    public void launchShowCaseViewSequence(boolean unique) {
        Logger.d(TAG, "Launching Sequence of ShowCaseViews");
        showcaseViewIndex = 0;
        inflateFromRunningIndex(unique);
    }

    public void onClick_launchInstructions(View v) {
        launchShowCaseViewSequence(!UNIQUE);
    }

    public void addShowCaseItem(int id, String title, String text){
        Logger.d(TAG, "Preparing ShowCaseView - id:{0}, title:{1}, text:{2}",
                Integer.toString(id),title,text);
        showcasesId.add(id);
        showcasesTexts.add(new String[]{title, text});
    }

    public void inflateFromRunningIndex(final boolean unique) {
        Logger.d(TAG, "Showing ShowCaseView - index: {}",Integer.toString(showcaseViewIndex));
        if (showcaseViewIndex < showcasesId.size()) {
            int id = showcasesId.get(showcaseViewIndex);
            String[] texts = showcasesTexts.get(showcaseViewIndex);
            ShowcaseView.Builder svBuilder = new ShowcaseView.Builder(DashboardActivity.this)
                    .setTarget(new ViewTarget(id, DashboardActivity.this))
                    .setContentTitle(texts[0])
                    .setContentText(texts[1]);
            if (unique) { svBuilder.singleShot(id); }
            final ShowcaseView sv = svBuilder.build();
            if (showcaseViewIndex < showcasesId.size()) {
                sv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sv.hide();
                        inflateFromRunningIndex(unique);
                    }
                });
            }
            sv.setShouldCentreText(true);
            sv.setStyle(R.style.CustomShowcaseTheme);
            showcaseViewIndex += 1;
        }
    }


}
