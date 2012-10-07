package com.brainydroid.daydreaming;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brainydroid.daydreaming.ExperimentService.LocalBinder;

public class DashboardActivity extends Activity {

	private ToggleButton toggleExperimentRunning;

	private SharedPreferences mFLPrefs;
	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	private ExperimentService experimentService;
	private boolean mBound = false;

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

		//		checkFirstRun();
		//		checkServiceUpdateView();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to ExperimentService, cast the IBinder and get ExperimentService instance
			LocalBinder binder = (LocalBinder)service;
			experimentService = binder.getService();
			mBound = true;
			updateView();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

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

	private void startExperimentService() {
		//		Toast.makeText(this, "Will start service", Toast.LENGTH_SHORT).show();

		Intent experimentIntent = new Intent(this, ExperimentService.class);
		startService(experimentIntent);
		bindService(experimentIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void stopExperimentService() {
		//		Toast.makeText(this, "Will stop service", Toast.LENGTH_SHORT).show();
		if (mBound) {
			experimentService.stopServiceOnUnbind();
			unbindService(mConnection);
			mBound = false;
		} else {
			Toast.makeText(this, "Could not stop service: not bound", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isServiceRunning() {
		return mDPrefs.getBoolean(getString(R.pref.dashboardExpRunning), false);
	}

	private boolean isServiceShouldRun() {
		return mDPrefs.getBoolean(getString(R.pref.dashboardExpShouldRun), true);
	}

	private void checkService() {
		if (isServiceRunning()) {
			if (!mBound) {
				Intent experimentIntent = new Intent(this, ExperimentService.class);
				bindService(experimentIntent, mConnection, Context.BIND_AUTO_CREATE);
			}

			if (!isServiceShouldRun()) {
				stopExperimentService();
			}
		} else {
			if (isServiceShouldRun()) {
				startExperimentService();
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