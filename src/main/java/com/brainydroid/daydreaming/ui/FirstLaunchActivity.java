package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationPointService;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

public abstract class FirstLaunchActivity extends RoboSherlockFragmentActivity {

    private static String TAG = "FirstLaunchActivity";

    @Inject StatusManager statusManager;

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        checkFirstLaunch();
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
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

    protected void checkFirstLaunch() {
        if (statusManager.isFirstLaunchCompleted()) {
            Logger.i(TAG, "First launch completed -> finishing");
            finish();
        } else {
            Logger.v(TAG, "First launch not completed");
        }
    }

    protected void launchNextActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    protected void finishFirstLaunch() {
        Logger.i(TAG, "Setting first launch to finished");

        statusManager.setFirstLaunchCompleted();

//        // TODO: clean this up and re-activate counterpart in DashboardActivity
//        // saving actual date to string in sharedPreferences
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");//("MM/dd/yyyy");
//        String StartDateString = dateFormat.format(new Date());
//        SharedPreferences sharedPrefs = getSharedPreferences("startDatePrefs", 0);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putString("startDateString", StartDateString);
//        editor.commit();
//        //-----------------------

        Intent schedulerServiceIntent = new Intent(this, SchedulerService.class);
        startService(schedulerServiceIntent);

        // FIXME: fix all emulator-specific hacks
        Intent locationPointServiceIntent = new Intent(this, LocationPointService.class);
        if (!(Build.FINGERPRINT.startsWith("generic"))) {
            Logger.d(TAG, "Starting LocationPointService");
            startService(locationPointServiceIntent);
        }
    }

}
