package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brainydroid.daydreaming.R;

public class FirstLaunchSettingsActivity extends ActionBarActivity {

	private static String TAG = "FirstLaunchSettingsActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch_settings);
	}

	// TODO
	// - save and restore preference state (when screen rotates)
	// - save value for time window dialog
	// - status checks (good insertion in the firstlaunch sequence)
	// DONE
	// - listening for preference change (if time window is set to 7pm while a notification is scheduled for 8)
	// - saving preference : automatic
	// - setting default values in xml

	public void launchDashboard(View v) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] launchDashboard");
		}

		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		dashboardIntent.putExtra(DashboardActivity.EXTRA_COMES_FROM_FIRST_LAUNCH, true);
		startActivity(dashboardIntent);
	}

	public void runSettings(View v) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] runSettings");
		}

		Intent dashboardIntent = new Intent(this, SettingsActivity.class);
		dashboardIntent.putExtra(DashboardActivity.EXTRA_COMES_FROM_FIRST_LAUNCH, true);
		startActivity(dashboardIntent);
	}
}
