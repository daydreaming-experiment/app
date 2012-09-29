package com.brainydroid.daydreaming;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstLaunchMeasuresActivity extends FragmentActivity {

	private TextView textNetworkLocation;
	private TextView textSettings;
	private Button buttonSettings;
	private Button buttonNext;

	private SharedPreferences mFLPrefs;
	private SharedPreferences.Editor eFLPrefs;
	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;
	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_launch_measures);

		textNetworkLocation = (TextView) findViewById(R.id.firstLaunchMeasures_textNetworkLocation);
		textSettings = (TextView) findViewById(R.id.firstLaunchMeasures_textSettings);
		buttonSettings = (Button) findViewById(R.id.firstLaunchMeasures_buttonSettings);
		buttonNext = (Button) findViewById(R.id.firstLaunchMeasures_buttonNext);

		mFLPrefs = getSharedPreferences(getString(R.pref.firstLaunchPrefs), MODE_PRIVATE);
		eFLPrefs = mFLPrefs.edit();
		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		eFLPrefs.putBoolean(getString(R.pref.firstLaunchStarted), true);
		eFLPrefs.commit();

		if (isNetworkLocEnabled()) {
			launchDashboard();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		checkFirstRun();

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

	private void checkFirstRun() {
		if (mFLPrefs.getBoolean(getString(R.pref.firstLaunchCompleted), false)) {
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

	public void launchSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(View view) {
		launchDashboard();
	}

	public void launchDashboard() {
		eFLPrefs.putBoolean(getString(R.pref.firstLaunchCompleted), true);
		eFLPrefs.commit();
		eDPrefs.putBoolean(getString(R.pref.dashboardExpShouldRun), true);
		eDPrefs.commit();
		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		startActivity(dashboardIntent);
		finish();
	}
}