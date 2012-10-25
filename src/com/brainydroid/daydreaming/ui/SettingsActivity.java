package com.brainydroid.daydreaming.ui;



import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.TimePreference;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	TimePreference timepreference_max;
	TimePreference timepreference_min;

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
		super.onResume();
		
		
		// Set up a listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Let's do something a preference value changes
		if (key.equals("time_window_ub_key")) {
			timepreference_max.setSummary("Current value is " +  timepreference_max.getTimeString()); 
		}
		else if (key.equals("time_window_lb_key")) {
			timepreference_min.setSummary("Current value is " +  timepreference_min.getTimeString()); 
		}

	}






}
