package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;

/*
 * Activity at first launch
 *
 * In first launch sequence of apps
 *
 * Previous activity :  none
 * This activity     :  FirstLaunchWelcomeActivity
 * Next activity     :  FirstLaunchDescriptionActivity
 *
 */
public class FirstLaunchWelcomeActivity extends SherlockActivity {

	private static String TAG = "FirstLaunchWelcomeActivity";

	private StatusManager status;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);

        status = StatusManager.getInstance(this);

		setContentView(R.layout.activity_first_launch_welcome);
	}

	@Override
	public void onStart() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStart");
		}

		super.onStart();
		checkFirstLaunch();
	}

	@Override
	public void onResume() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onResume");
		}

		super.onResume();
	}

	public void onClick_start(@SuppressWarnings("UnusedParameters") View view) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onClick_start");
		}

		Intent intent = new Intent(this, FirstLaunchDescriptionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void checkFirstLaunch() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] checkFirstLaunch");
		}

		if (status.isFirstLaunchCompleted()) {
			finish();
		}
	}
}
