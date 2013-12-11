package com.brainydroid.daydreaming.ui.Dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.ServerConfig;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.ui.AlphaLinearLayout;
import com.brainydroid.daydreaming.ui.FirstLaunchSequence.FirstLaunch00WelcomeActivity;
import com.brainydroid.daydreaming.ui.FontUtils;

import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_dashboard)
public class DashboardActivity extends RoboFragmentActivity {

    private static String TAG = "DashboardActivity";

    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;

    @InjectView(R.id.dashboard_ExperimentTimeElapsed2)
    TextView timeElapsedTextView;
    @InjectView(R.id.dashboard_ExperimentResultsIn2) TextView timeToGoTextView;
    @InjectView(R.id.dashboard_titleTesting)  TextView testText;
    @InjectView(R.id.button_test_poll) Button testButton;
    @InjectView(R.id.dashboard_about_layout) AlphaLinearLayout aboutLayout;

    private boolean testModeThemeActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        checkTestMode();
        super.onCreate(savedInstanceState);
        checkFirstLaunch();
        setRobotoFont(this);
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");

        aboutLayout.setAlpha(0.3f);
        aboutLayout.setClickable(false);

        checkExperimentModeActivatedDirty();
        updateRunningTime();
        updateChromeMode();
        super.onStart();
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        checkExperimentModeActivatedDirty();
        super.onResume();
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
//        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        startActivity(intent);
//        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
    }

    //TODO: Decide what should happen when results button is clicked.
    public void  onClick_SeeResults(
            @SuppressWarnings("UnusedParameters") View view){
        Toast.makeText(this, "Not yet!", Toast.LENGTH_SHORT).show();
    }

    private void updateRunningTime() {
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
        final int daysToGo = ServerConfig.EXP_DURATION_DAYS - daysElapsed;
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

    protected void checkFirstLaunch() {
        if (!statusManager.isFirstLaunchCompleted()) {
            Logger.i(TAG, "First launch not completed -> starting first " +
                    "launch sequence and finishing this activity");
            Intent intent = new Intent(this, FirstLaunch00WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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

    public void setRobotoFont(Activity activity){
        ViewGroup godfatherView =
                (ViewGroup)activity.getWindow().getDecorView();
        FontUtils.setRobotoFont(activity, godfatherView);
    }

    private void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.MyCustomTheme);
            testModeThemeActivated = false;
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.MyCustomTheme_test);
            testModeThemeActivated = true;
        }
    }

    private void updateChromeMode() {
        Logger.d(TAG, "Updating chrome depending on test mode");
        if (statusManager.getCurrentMode() == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production chrome");
            testButton.setVisibility(View.INVISIBLE);
            testButton.setClickable(false);
            testText.setVisibility(View.INVISIBLE);
        } else {
            Logger.d(TAG, "Setting test chrome");
            testButton.setVisibility(View.VISIBLE);
            testButton.setClickable(true);
            testText.setVisibility(View.VISIBLE);
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

}
