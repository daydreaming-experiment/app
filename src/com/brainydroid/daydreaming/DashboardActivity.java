package com.brainydroid.daydreaming;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DashboardActivity extends ActionBarActivity {

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.dashboard, menu);

		// Calling super after populating the menu is necessary here to ensure that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onStart() {
		super.onStart();

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_settings:
			Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
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

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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