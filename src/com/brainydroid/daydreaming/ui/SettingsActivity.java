package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.SchedulerService;

public class SettingsActivity extends PreferenceActivity
implements OnSharedPreferenceChangeListener {

	private static String TAG = "SettingsActivity";

	private PreferenceScreen prefScreen;
	private SharedPreferences sharedPrefs;
	private TimePreference timepreference_max;
	private TimePreference timepreference_min;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate(savedInstanceState);

		initVars();
	}

	@Override
	protected void onResume() {

		// Debug
		Log.d(TAG, "[fn] onResume");

		super.onResume();

		// Set up a listener whenever a key changes
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	private void initVars() {

		// Debug
		Log.d(TAG, "[fn] initVars");

		// Load the XML preferences file
		addPreferencesFromResource(R.layout.preferences);

		prefScreen = getPreferenceScreen();
		sharedPrefs = prefScreen.getSharedPreferences();

		// Get a reference to the preferences
		timepreference_min = (TimePreference)prefScreen.findPreference("time_window_lb_key");
		timepreference_max = (TimePreference)prefScreen.findPreference("time_window_ub_key");

		timepreference_min.setSummary("Current value is " +
				timepreference_min.getTimeString());
		timepreference_max.setSummary("Current value is " +
				timepreference_max.getTimeString());

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		// Debug
		Log.d(TAG, "[fn] onSharedPreferenceChanged");

		// TODO: check that the final time is after the starting time
		// TODO: check that the allowed time window is longer than a certain minimum (e.g. 3 hours)

		// Let's do something a preference value changes
		if (key.equals("time_window_ub_key")) {
			timepreference_max.setSummary(timepreference_max.getTimeString());
			startSchedulerService();
		} else if (key.equals("time_window_lb_key")) {
			timepreference_min.setSummary(timepreference_min.getTimeString());
			startSchedulerService();
		}
	}

	//	private boolean compareTimes(String timeFirst, String timeLast) {
	//		String[] firstPieces = timeFirst.split(":");
	//		int firstHour = Integer.parseInt(firstPieces[0]);
	//		int firstMinute = Integer.parseInt(firstPieces[1]);
	//
	//		String[] lastPieces = timeLast.split(":");
	//		int lastHour = Integer.parseInt(lastPieces[0]);
	//		int lastMinute = Integer.parseInt(lastPieces[1]);
	//
	//		if (firstHour != lastHour) {
	//			return firstHour < lastHour;
	//		} else {
	//			return firstMinute < lastMinute;
	//		}
	//	}

	private void startSchedulerService() {

		// Debug
		Log.d(TAG, "[fn] startSchedulerService");

		Intent schedulerIntent = new Intent(this, SchedulerService.class);
		startService(schedulerIntent);
	}
}
