package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationItemService;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;

public class FirstLaunchMeasuresActivity extends SherlockActivity {

	private static String TAG = "FirstLaunchMeasuresActivity";

	private TextView textNetworkLocation;
	private TextView textSettings;
	private Button buttonSettings;
	private Button buttonNext;

	private StatusManager status;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_launch_measures);

		textNetworkLocation = (TextView)findViewById(R.id.firstLaunchMeasures_textNetworkLocation);
		textSettings = (TextView)findViewById(R.id.firstLaunchMeasures_textSettings);
		buttonSettings = (Button)findViewById(R.id.firstLaunchMeasures_buttonSettings);
		buttonNext = (Button)findViewById(R.id.firstLaunchMeasures_buttonNext);
		status = StatusManager.getInstance(this);

		if (status.isNetworkLocEnabled()) {
			launchDashboard();
		}
	}

	@Override
	public void onStart() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStart");
		}

		super.onStart();
		updateView();
		checkFirstLaunch();
	}

	@Override
	public void onResume() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onResume");
		}

		super.onResume();
        updateView();
	}

	@Override
	public void onBackPressed() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBackPressed");
		}

		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void checkFirstLaunch() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] checkFirstLaunch");
		}

		if (status.isFirstLaunchCompleted()) {
			finish();
		}
	}

	private void updateView() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] updateView");
		}

		textNetworkLocation.setCompoundDrawablesWithIntrinsicBounds(
				status.isNetworkLocEnabled() ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

		updateRequestAdjustSettings();
	}

	private void updateRequestAdjustSettings() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] updateRequestAdjustSettings");
		}

		if ((status.isNetworkLocEnabled())||(Build.FINGERPRINT.startsWith("generic"))) {
			setAdjustSettingsOff();
		} else {
			setAdjustSettingsNecessary();
		}
	}

	@TargetApi(11)
	private void setAdjustSettingsNecessary() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setAdjustSettingsNecessary");
		}

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

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setAdjustSettingsOff");
		}

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

	public void onClick_buttonSettings(@SuppressWarnings("UnusedParameters") View view) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onClick_buttonSettings");
		}

		launchSettings();
	}

	private void launchSettings() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] launchSettings");
		}

		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(settingsIntent);
	}

	public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onClick_buttonNext");
		}

		launchDashboard();
	}

	private void launchDashboard() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] launchDashboard");
		}

		finishFirstLaunch(); // when everything is ok, first launch is set to completed
		Intent dashboardIntent = new Intent(this, DashboardActivity.class);
		dashboardIntent.putExtra(DashboardActivity.EXTRA_COMES_FROM_FIRST_LAUNCH, true);
		startActivity(dashboardIntent);
		finish();
	}

	private void finishFirstLaunch() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] finishFirstLaunch");
		}

		status.setFirstLaunchCompleted();

        // TODO: clean this up and re-activate counterpart in DashboardActivity
        // saving actual date to string in sharedPreferences
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");//("MM/dd/yyyy");
//        String StartDateString = dateFormat.format(new Date());
//        SharedPreferences sharedPrefs = getSharedPreferences("startDatePrefs", 0);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putString("startDateString", StartDateString);
//        editor.commit();
        //-----------------------


		Intent schedulerServiceIntent = new Intent(this, SchedulerService.class);
		startService(schedulerServiceIntent);

        Intent locationItemServiceIntent = new Intent(this, LocationItemService.class);
        if (!(Build.FINGERPRINT.startsWith("generic"))){
        startService(locationItemServiceIntent);
        }
	}

}
