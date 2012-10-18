package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocationService extends Service {

	private boolean stopSelfOnUnbind = false;
	private final IBinder mBinder = new LocationServiceBinder();

	public class LocationServiceBinder extends Binder {
		LocationService getService() {
			// Return this instance of SyncService so clients can call public methods
			return LocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);

		if (stopSelfOnUnbind) {
			stopSelf();
		}

		return true;
	}

	public void setStopServiceOnUnbind() {
		stopSelfOnUnbind = true;
	}
}
