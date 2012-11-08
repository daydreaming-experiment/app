package com.brainydroid.daydreaming.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.brainydroid.daydreaming.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static String TAG = "SettingsActivity";

	TimePreference timepreference_max;
	TimePreference timepreference_min;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate(savedInstanceState);

		// Load the XML preferences file
		addPreferencesFromResource(R.layout.preference);

		// Get a reference to the preferences
		timepreference_min = (TimePreference)getPreferenceScreen().findPreference("time_window_lb_key");
		timepreference_max = (TimePreference)getPreferenceScreen().findPreference("time_window_ub_key");

		timepreference_min.setSummary("Current value is " + timepreference_min.getTimeString());
		timepreference_max.setSummary("Current value is " + timepreference_max.getTimeString());


	}

	@Override
	protected void onResume() {

		// Debug
		Log.d(TAG, "[fn] onResume");

		super.onResume();

		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		// Debug
		Log.d(TAG, "[fn] onSharedPreferenceChanged");

		// Let's do something a preference value changes
		if (key.equals("time_window_ub_key")) {
			timepreference_max.setSummary("Current value is " +  timepreference_max.getTimeString());
		}
		else if (key.equals("time_window_lb_key")) {
			timepreference_min.setSummary("Current value is " +  timepreference_min.getTimeString());
		}
	}
}
