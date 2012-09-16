package com.brainydroid.daydreaming;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstLaunchMeasuresActivity extends FragmentActivity {

	private TextView textNetworkLocation;
	private TextView textGPSLocation;
	private TextView textSettings;
	private Button buttonSettings;
	private Button buttonNext;

	private SharedPreferences mPrefs;
	private SharedPreferences.Editor ePrefs;
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

		mPrefs = getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);
		ePrefs = mPrefs.edit();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		ePrefs.putBoolean(PreferenceKeys.FIRST_LAUNCH_STARTED, true);
		ePrefs.commit();
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
				networkLocEnabled ? R.drawable.check : R.drawable.cross, 0, 0, 0);
		textGPSLocation.setCompoundDrawablesWithIntrinsicBounds(
				gpsEnabled ? R.drawable.check : R.drawable.cross, 0, 0, 0);

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
		adjustSettingsOff = false;
	}

	@TargetApi(11)
	private void setAdjustSettingsOptional() {
		textSettings.setText(R.string.firstLaunchMeasures_text_settings_optional);
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
		launchSettings();
	}

	public void launchSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(View view) {
		if (adjustSettingsOff) {
			launchDashboard();
		} else {
			DialogFragment settingsAlert = SettingsAlertDialogFragment.newInstance(
					R.string.firstLaunchMeasures_alert_title,
					R.string.firstLaunchMeasures_alert_text,
					R.string.firstLaunchMeasures_alert_button_ignore,
					R.string.firstLaunchMeasures_alert_button_gotosettings);
			settingsAlert.show(getSupportFragmentManager(), "settingsAlert");
		}
	}

	public void launchDashboard() {
		ePrefs.putBoolean(PreferenceKeys.FIRST_LAUNCH_COMPLETED, true);
		ePrefs.commit();
		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		startActivity(dashboardIntent);
	}

	public static class SettingsAlertDialogFragment extends DialogFragment {

		public static SettingsAlertDialogFragment newInstance(int title, int text,
				int negText, int posText) {
			SettingsAlertDialogFragment frag = new SettingsAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			args.putInt("text", text);
			args.putInt("negText", negText);
			args.putInt("posText", posText);
			frag.setArguments(args);
			return frag;
		}

		@TargetApi(11)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");
			int text = getArguments().getInt("text");
			int negText = getArguments().getInt("negText");
			int posText = getArguments().getInt("posText");

			AlertDialog.Builder alertSettings = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setMessage(text)
			.setNegativeButton(negText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					((FirstLaunchMeasuresActivity)getActivity()).launchDashboard();
				}
			})
			.setPositiveButton(posText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					((FirstLaunchMeasuresActivity)getActivity()).launchSettings();
				}
			});

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				alertSettings.setIconAttribute(android.R.attr.alertDialogIcon);
			} else {
				alertSettings.setIcon(android.R.drawable.ic_dialog_alert);
			}

			return alertSettings.create();
		}
	}
}