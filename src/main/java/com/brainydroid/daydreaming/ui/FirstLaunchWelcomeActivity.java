package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;

public class FirstLaunchWelcomeActivity extends ActionBarActivity {

	private static String TAG = "FirstLaunchWelcomeActivity";

	private StatusManager status;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch_welcome);

		status = StatusManager.getInstance(this);
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

	public void onClick_start(View view) {

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

		if (status.isFirstLaunchCompleted() || status.isClearing()) {
			if (status.isClearing()) {
				status.finishClear();
			}
			finish();
		}
	}
}
