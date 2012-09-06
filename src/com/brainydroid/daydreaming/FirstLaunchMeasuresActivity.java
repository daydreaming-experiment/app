package com.brainydroid.daydreaming;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FirstLaunchMeasuresActivity extends Activity {

	private TextView textNetworkLocation;
	private TextView textGPSLocation;
	private TextView textSettings;
	private Button buttonSettings;
	private Button buttonNext;

	private SharedPreferences mPrefs;
	private LocationManager locationManager;

	private boolean networkLocEnabled;
	private boolean gpsEnabled;
	private boolean adjustSettingsOff;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_launch_measures);

		textNetworkLocation = (TextView) findViewById(R.id.firstLaunchMeasures_textNetworkLocation);
		textGPSLocation = (TextView) findViewById(R.id.firstLaunchMeasures_textGPSLocation);
		textSettings = (TextView) findViewById(R.id.firstLaunchMeasures_textSettings);
		buttonSettings = (Button) findViewById(R.id.firstLaunchMeasures_buttonSettings);
		buttonNext = (Button) findViewById(R.id.firstLaunchMeasures_buttonNext);

		mPrefs = getPreferences(MODE_PRIVATE);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onResume() {
		super.onResume();

		networkLocEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		updateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_first_launch_measures, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void updateView() {
		textNetworkLocation.setCompoundDrawablesWithIntrinsicBounds(
				networkLocEnabled ? R.drawable.check_36 : R.drawable.cross_36, 0, 0, 0);
		textGPSLocation.setCompoundDrawablesWithIntrinsicBounds(
				gpsEnabled ? R.drawable.check_36 : R.drawable.cross_36, 0, 0, 0);

		updateRequestAdjustSettings();
	}

	private void updateRequestAdjustSettings() {

		if (networkLocEnabled && gpsEnabled) {
			setAdjustSettingsOff();
		} else {
			if (!networkLocEnabled && !gpsEnabled) {
				setAdjustSettingsNecessary();
			} else {
				setAdjustSettingsOptional();
			}
		}
	}

	@TargetApi(11)
	private void setAdjustSettingsNecessary() {
		textSettings.setVisibility(View.VISIBLE);
		buttonSettings.setVisibility(View.VISIBLE);
		buttonSettings.setClickable(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			buttonNext.setAlpha(0.3f);
		} else {
			buttonNext.setVisibility(View.INVISIBLE);
		}
		buttonNext.setClickable(false);
		adjustSettingsOff = false;
	}

	@TargetApi(11)
	private void setAdjustSettingsOptional() {
		textSettings.setVisibility(View.VISIBLE);
		buttonSettings.setVisibility(View.VISIBLE);
		buttonSettings.setClickable(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			buttonNext.setAlpha(1f);
		} else {
			buttonNext.setVisibility(View.VISIBLE);
		}
		buttonNext.setClickable(true);
		adjustSettingsOff = false;
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
		adjustSettingsOff = true;
	}

	public void onClick_buttonSettings(View view) {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(View view) {
		if (adjustSettingsOff) {
			// Open next activity
			Toast.makeText(FirstLaunchMeasuresActivity.this, "Will open next activity",
					Toast.LENGTH_SHORT).show();
		} else {
			// Suggest to adjust the settings
			Toast.makeText(FirstLaunchMeasuresActivity.this, "Will suggest to adjust settings again",
					Toast.LENGTH_SHORT).show();
		}
	}
}