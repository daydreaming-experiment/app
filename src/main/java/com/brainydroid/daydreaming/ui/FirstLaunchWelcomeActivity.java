package com.brainydroid.daydreaming.ui;

import android.util.Log;
import android.view.View;
import com.brainydroid.daydreaming.R;
import roboguice.inject.ContentView;

/**
 * Activity at first launch
 *
 * In first launch sequence of apps
 *
 * Previous activity :  none
 * This activity     :  FirstLaunchWelcomeActivity
 * Next activity     :  FirstLaunchDescriptionActivity
 *
 */
@ContentView(R.layout.activity_first_launch_welcome)
public class FirstLaunchWelcomeActivity extends FirstLaunchActivity {

	@SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunchWelcomeActivity";

	public void onClick_start(@SuppressWarnings("UnusedParameters") View view) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onClick_start");
		}

        launchNextActivity(FirstLaunchDescriptionActivity.class);
	}

}
