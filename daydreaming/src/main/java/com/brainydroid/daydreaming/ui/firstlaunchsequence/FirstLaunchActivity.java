package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.*;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.ui.dashboard.DashboardActivity;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;


/**
 * Class that activities FirstLaunchSequence extend.
 */
public abstract class FirstLaunchActivity extends RoboFragmentActivity {

    private static String TAG = "FirstLaunchActivity";

    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;

    private boolean testModeThemeActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        checkTestMode();
        super.onCreate(savedInstanceState);
        setRobotoFont(this);
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        checkFirstLaunch();
        super.onStart();
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        checkFirstLaunch();
        super.onResume();
    }

    @Override
    public void onStop() {
        Logger.v(TAG, "Stopping");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void setRobotoFont(Activity activity){
        ViewGroup godfatherView =
                (ViewGroup)activity.getWindow().getDecorView();
        FontUtils.setRobotoFont(activity, godfatherView);
    }

    protected boolean isExperimentModeActivatedDirty() {
        if ((statusManager.getCurrentMode() == StatusManager.MODE_TEST && !testModeThemeActivated)
            || (statusManager.getCurrentMode() == StatusManager.MODE_PROD && testModeThemeActivated)) {
            Logger.w(TAG, "Discrepancy between theme and test/production mode");
            return true;
        } else {
            Logger.v(TAG, "No test/production mode theming discrepancy");
            return false;
        }
    }

    /**
     * Kills activity if first launch already fully completed.
     */
    protected void checkFirstLaunch() {
        if (statusManager.isFirstLaunchCompleted() || isExperimentModeActivatedDirty()) {
            Logger.i(TAG, "First launch completed or test mode theming discrepancy -> finishing");
            finish();
        } else {
            Logger.v(TAG, "First launch not completed, and no test mode theming discrepancy");

            if(shouldFinishIfTipiQuestionnaireCompleted() &&
                    statusManager.isTipiQuestionnaireCompleted())
            {
                Logger.i(TAG, "Tipi questionnaire completed, " +
                        "and we should finish because of that -> finishing");
                finish();
            } else {
                Logger.v(TAG, "Tipi questionnaire either not relevant for " +
                        "finishing or not completed");
            }
        }
    }

    /**
     * To be overridden by classes who want a different behaviour in
     * checkFirstLaunch().
     */
    public boolean shouldFinishIfTipiQuestionnaireCompleted() {
        return false;
    }

    /**
     * Launching activity in Sequence
     */
    protected void launchNextActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * Launching Dashboard activity
     */
    protected void launchDashBoardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Saving completion of FirstLaunchSequence. Next launch will lead to
     * dashboard directly.
     */
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

        Logger.d(TAG, "Starting SchedulerService");
        Intent schedulerServiceIntent = new Intent(this,
                SchedulerService.class);
        startService(schedulerServiceIntent);

        Intent locationPointServiceIntent = new Intent(this,
                LocationPointService.class);
        Logger.d(TAG, "Starting LocationPointService");
        startService(locationPointServiceIntent);
    }

    public void checkTestMode() {
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

}
