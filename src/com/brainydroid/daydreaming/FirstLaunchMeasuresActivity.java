package com.brainydroid.daydreaming;

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

		textNetworkLocation = (TextView) findViewById(R.id.firstLaunchMeasures_textNetworkLocation);
		textSettings = (TextView) findViewById(R.id.firstLaunchMeasures_textSettings);
		buttonSettings = (Button) findViewById(R.id.firstLaunchMeasures_buttonSettings);
		buttonNext = (Button) findViewById(R.id.firstLaunchMeasures_buttonNext);
		status = StatusManager.getInstance(this);
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		checkFirstRun();
		updateView();

		if (isNetworkLocEnabled()) {
			launchDashboard();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateView();
		checkFirstRun();
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

	private void checkFirstRun() {
		if (status.isFirstLaunchCompleted()) {
			finish();
		}
	}

	private void updateView() {
		textNetworkLocation.setCompoundDrawablesWithIntrinsicBounds(
				isNetworkLocEnabled() ? R.drawable.check : R.drawable.cross, 0, 0, 0);

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
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(View view) {
		launchDashboard();
	}

	private void launchDashboard() {
		setStatus();
		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		startActivity(dashboardIntent);
		finish();
	}

	private void setStatus() {
		status.setFirstLaunchCompleted();
		status.setExpServiceShouldRun(true);
	}
}