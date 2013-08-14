package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationPointService;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

public abstract class ReOpenActivity extends RoboSherlockFragmentActivity {

    private static String TAG = "FirstLaunchActivity";

    @Inject StatusManager statusManager;

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);

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







}
