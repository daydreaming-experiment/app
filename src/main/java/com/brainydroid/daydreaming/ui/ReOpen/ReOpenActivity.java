package com.brainydroid.daydreaming.ui.ReOpen;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationPointService;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.ui.FirstLaunchSequence.DashboardActivity;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;


/**
 * Class that activities FirstLaunchSequence extend.
 */
public abstract class ReOpenActivity extends RoboSherlockFragmentActivity {

    private static String TAG = "FirstLaunchActivity";

    @Inject  StatusManager statusManager;

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        setRobotoFont(this);

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
        overridePendingTransition(R.anim.push_bottom_in,
                R.anim.push_bottom_out);
    }

    public void setRobotoFont(Activity activity) {
        ViewGroup godfatherView =
                (ViewGroup)activity.getWindow().getDecorView();
        FontUtils.setRobotoFont(activity, godfatherView);
    }

}
