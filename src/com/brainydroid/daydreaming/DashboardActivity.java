package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DashboardActivity extends Activity {

	private ToggleButton toggleExperimentRunning;

	private SharedPreferences mFLPrefs;
	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFLPrefs = getSharedPreferences(getString(R.pref.firstLaunchPrefs), MODE_PRIVATE);
		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();
		checkFirstRun();

		setContentView(R.layout.activity_dashboard);
		toggleExperimentRunning = (ToggleButton)findViewById(R.id.dashboard_toggleExperimentRunning);
		toggleExperimentRunning.setOnCheckedChangeListener(new OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				eDPrefs.putBoolean(getString(R.pref.dashboardExpShouldRun), isChecked);
				eDPrefs.commit();
				checkService();
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

		checkFirstRun();
		checkServiceUpdateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_day_dreaming, menu);
		return true;
	}

	private void checkFirstRun() {
		if (!mFLPrefs.getBoolean(getString(R.pref.firstLaunchCompleted), false)) {
			eDPrefs.putBoolean(getString(R.pref.dashboardExpShouldRun), false);
			eDPrefs.commit();

			Intent intent;
			if (!mFLPrefs.getBoolean(getString(R.pref.firstLaunchStarted), false)) {
				intent = new Intent(this, FirstLaunchWelcomeActivity.class);
			} else {
				intent = new Intent(this, ReLaunchWelcomeActivity.class);
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			finish();
		}
	}

	private void startQuestionsService() {
		Toast.makeText(this, "Will start service", Toast.LENGTH_SHORT).show();
		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), true);
		eDPrefs.commit();
	}

	private void stopQuestionsService() {
		Toast.makeText(this, "Will stop service", Toast.LENGTH_SHORT).show();
		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), false);
		eDPrefs.commit();
	}

	private boolean isServiceRunning() {
		return mDPrefs.getBoolean(getString(R.pref.dashboardExpRunning), false);
	}

	private boolean isServiceShouldRun() {
		return mDPrefs.getBoolean(getString(R.pref.dashboardExpShouldRun), true);
	}

	private void checkService() {
		if (isServiceRunning() != isServiceShouldRun()) {
			if (isServiceShouldRun()) {
				startQuestionsService();
			} else {
				stopQuestionsService();
			}
		}
	}

	private void updateView() {
		toggleExperimentRunning.setChecked(isServiceRunning());
	}

	private void checkServiceUpdateView() {
		checkService();
		updateView();
	}
}