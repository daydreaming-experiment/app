package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.db.Util;

public class SettingsActivity extends PreferenceActivity
implements OnSharedPreferenceChangeListener {

	private static String TAG = "SettingsActivity";

	private static int MIN_WINDOW_HOURS = 5; // 5 hours (in hours)

	private PreferenceScreen prefScreen;
	private SharedPreferences sharedPrefs;
	private TimePreference timepreference_max;
	private TimePreference timepreference_min;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);

		initVars();
	}

	@Override
	protected void onResume() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onResume");
		}

		super.onResume();

		// Set up a listener whenever a key changes
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStop");
		}

		super.onStop();
		sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		// Load the XML preferences file
		addPreferencesFromResource(R.layout.preferences);

		prefScreen = getPreferenceScreen();
		sharedPrefs = prefScreen.getSharedPreferences();

		// Get a reference to the preferences
		timepreference_min = (TimePreference)prefScreen.findPreference("time_window_lb_key");
		timepreference_max = (TimePreference)prefScreen.findPreference("time_window_ub_key");

		timepreference_min.setSummary(timepreference_min.getTimeString());
		timepreference_max.setSummary(timepreference_max.getTimeString());

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onSharedPreferenceChanged");
		}

		// TODO: check that the final time is after the starting time
		// TODO: check that the allowed time window is longer than a certain minimum (e.g. 3 hours)

		// Let's do something a preference value changes
		if (key.equals("time_window_ub_key") || key.equals("time_window_lb_key")) {
			correctTimeWindow();
			timepreference_max.setSummary(timepreference_max.getTimeString());
			timepreference_min.setSummary(timepreference_min.getTimeString());
			startSchedulerService();
		}
	}

	private void correctTimeWindow() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] correctTimeWindow");
		}

		if (!checkTimeWindow()) {
			timepreference_min.setTime(getString(R.pref.settings_time_window_lb_default));
			timepreference_max.setTime(getString(R.pref.settings_time_window_ub_default));
			Toast.makeText(this, getString(R.string.settings_time_corrected_1) + " " +
					MIN_WINDOW_HOURS + " " + getString(R.string.settings_time_corrected_2),
					Toast.LENGTH_LONG).show();
		}
	}

	private boolean checkTimeWindow() {

		// Debug
		if (Config.LOGD){
			Log.d(TAG, "[fn] checkTimeWindow");
		}

		String timeFirst = timepreference_min.getTimeString();
		int firstHour = Util.getHour(timeFirst);
		int firstMinute = Util.getMinute(timeFirst);
		int first = firstHour * 60 + firstMinute;

		String timeLast = timepreference_max.getTimeString();
		int lastHour = Util.getHour(timeLast);
		int lastMinute = Util.getMinute(timeLast);
		int last = lastHour * 60 + lastMinute;

		if (last < first) {
			// The time window goes through midnight
			last += 24 * 60;
		}

		return (first + MIN_WINDOW_HOURS * 60) <= last;
	}

	private void startSchedulerService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] startSchedulerService");
		}

		Intent schedulerIntent = new Intent(this, SchedulerService.class);
		startService(schedulerIntent);
	}
}
