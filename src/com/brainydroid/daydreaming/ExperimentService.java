package com.brainydroid.daydreaming;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class ExperimentService extends Service {

	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	private boolean stopSelfOnUnbind = false;
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		ExperimentService getService() {
			// Return this instance of ExperimentService so clients can call public methods
			return ExperimentService.this;
		}
	}

	@Override
	public void onCreate() {
		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();

		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), true);
		eDPrefs.commit();

		Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), false);
		eDPrefs.commit();

		Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(getApplicationContext(), "Service bound", Toast.LENGTH_SHORT).show();
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Toast.makeText(getApplicationContext(), "Service rebound", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);

		Toast.makeText(getApplicationContext(), "Service unbound", Toast.LENGTH_SHORT).show();
		if (stopSelfOnUnbind) {
			stopSelf();
		}

		return true;
	}

	public void stopServiceOnUnbind() {
		stopSelfOnUnbind = true;
	}
}
