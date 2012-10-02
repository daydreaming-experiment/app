package com.brainydroid.daydreaming;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class ExperimentService extends Service {

	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	@Override
	public void onCreate() {
		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();
		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 0;
	}

	@Override
	public void onDestroy() {
		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Don't allow binding
		return null;
	}

}
