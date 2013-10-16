package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.*;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.ui.Dashboard.DashboardActivity;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
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

    /**
     * Kills activity if first launch already fully completed.
     */
    public void checkFirstLaunch() {
        if ( (statusManager.isFirstLaunchCompleted()) ) {
            Logger.i(TAG, "First launch completed -> finishing");
            finish();
        } else {
            Logger.v(TAG, "First launch not completed");
        }
    }

    /**
     * Launching activity in Sequence
     */
    protected void launchNextActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("nextClass", activity);
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

}
