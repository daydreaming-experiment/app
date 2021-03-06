package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationPointService;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.dashboard.DashboardActivity;
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
        Logger.v(TAG, "Back pressed");
        super.onBackPressed();
        backHook();
    }

    public void backHook() {
        Logger.v(TAG, "Setting slide transition");
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
        if (statusManager.is(StatusManager.EXP_STATUS_FL_COMPLETED) || isExperimentModeActivatedDirty()) {
            Logger.i(TAG, "First launch completed or test mode theming discrepancy -> finishing");
            finish();
        } else {
            Logger.v(TAG, "First launch not completed, and no test mode theming discrepancy");
        }
    }

    /**
     * Launching activity in Sequence
     */
    protected void launchNextActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        statusManager.set(StatusManager.EXP_STATUS_FL_COMPLETED);

        // The SyncService (and following ProbeSchedulerService) and first parameter
        // update are launched by the dashboard. Here we only need to start the
        // LocationPointService for the first time.

        Intent locationPointServiceIntent = new Intent(this,
                LocationPointService.class);
        Logger.d(TAG, "Starting LocationPointService");
        startService(locationPointServiceIntent);
    }

    public void checkTestMode() {
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

}
