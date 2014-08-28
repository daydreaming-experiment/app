package com.brainydroid.daydreaming.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.inject.Inject;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by vincent on 28/08/14.
 */
@ContentView(R.layout.activity_credits)

public class CreditsActivity extends RoboFragmentActivity {


    private static String TAG = "CreditsActivity";


    private boolean testModeThemeActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        ViewGroup godfatherView = (ViewGroup) this.getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Logger.v(TAG, "Stopping");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backToDashboard(@SuppressWarnings("UnusedParameters") View v) {
        Logger.v(TAG, "Back to dashboard button clicked");
        onBackPressed();
    }

}