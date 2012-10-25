package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;

public class FirstLaunchMeasuresActivity extends ActionBarActivity {

	private TextView textNetworkLocation;
	private TextView textSettings;
	private Button buttonSettings;
	private Button buttonNext;

	private StatusManager status;
	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_launch_measures);

		textNetworkLocation = (TextView)findViewById(R.id.firstLaunchMeasures_textNetworkLocation);
		textSettings = (TextView)findViewById(R.id.firstLaunchMeasures_textSettings);
		buttonSettings = (Button)findViewById(R.id.firstLaunchMeasures_buttonSettings);
		buttonNext = (Button)findViewById(R.id.firstLaunchMeasures_buttonNext);
		status = StatusManager.getInstance(this);
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		if (isNetworkLocEnabled()) {
			launchDashboard();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateView();
		checkFirstLaunch();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void checkFirstLaunch() {
		if (status.isFirstLaunchCompleted() || status.isClearing()) {
			finish();
		}
	}

	private void updateView() {
		textNetworkLocation.setCompoundDrawablesWithIntrinsicBounds(
				isNetworkLocEnabled() ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

		updateRequestAdjustSettings();
	}

	private void updateRequestAdjustSettings() {
		if (isNetworkLocEnabled()) {
			setAdjustSettingsOff();
		} else {
			setAdjustSettingsNecessary();
		}
	}

	private boolean isNetworkLocEnabled() {
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	@TargetApi(11)
	private void setAdjustSettingsNecessary() {
		textSettings.setText(R.string.firstLaunchMeasures_text_settings_necessary);
		textSettings.setVisibility(View.VISIBLE);
		buttonSettings.setVisibility(View.VISIBLE);
		buttonSettings.setClickable(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			buttonNext.setAlpha(0.3f);
		} else {
			buttonNext.setVisibility(View.INVISIBLE);
		}
		buttonNext.setClickable(false);
	}

	@TargetApi(11)
	private void setAdjustSettingsOff() {
		textSettings.setVisibility(View.INVISIBLE);
		buttonSettings.setVisibility(View.INVISIBLE);
		buttonSettings.setClickable(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			buttonNext.setAlpha(1f);
		} else {
			buttonNext.setVisibility(View.VISIBLE);
		}
		buttonNext.setClickable(true);
	}

	public void onClick_buttonSettings(View view) {
		launchSettings();
	}

	private void launchSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(View view) {
		launchDashboard();
	}

	private void launchDashboard() {
		setStatus(); // when everything is ok, first launch is set to completed
		Intent settingsIntent = new Intent(this, FirstLaunchSettingsActivity.class);
		settingsIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		settingsIntent.putExtra(DashboardActivity.EXTRA_COMES_FROM_FIRST_LAUNCH, true);
		startActivity(settingsIntent);
		finish();
	}

	private void setStatus() {
		status.setFirstLaunchCompleted();
	}
}