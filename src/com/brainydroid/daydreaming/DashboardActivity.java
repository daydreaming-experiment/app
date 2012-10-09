package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class DashboardActivity extends Activity {

	private StatusManager status;
	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	private ToggleButton toggleExperimentRunning;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		status = StatusManager.getInstance(this);

		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();
		checkFirstRun();

		setContentView(R.layout.activity_dashboard);
		toggleExperimentRunning = (ToggleButton)findViewById(R.id.dashboard_toggleExperimentRunning);
		toggleExperimentRunning.setOnCheckedChangeListener(new OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				eDPrefs.putBoolean(getString(R.pref.dashboardStartServiceAtBoot), isChecked);
				eDPrefs.commit();
				status.setExpServiceShouldRun(isChecked);
				status.checkService();
			}
		});
		checkServiceUpdateView();
	}

	@Override
	public void onStart() {
		super.onStart();

		checkFirstRun();
		checkServiceUpdateView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void checkFirstRun() {
		if (!status.isFirstLaunchCompleted()) {
			status.setExpServiceShouldRun(false);

			Intent intent;
			if (!status.isFirstLaunchStarted()) {
				intent = new Intent(this, FirstLaunchWelcomeActivity.class);
			} else {
				intent = new Intent(this, ReLaunchWelcomeActivity.class);
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			finish();
		}
	}

	private void updateView() {
		toggleExperimentRunning.setChecked(status.isExpServiceRunning());
	}

	private void checkServiceUpdateView() {
		status.checkService();
		updateView();
	}
}