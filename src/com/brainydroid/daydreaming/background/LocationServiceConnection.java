package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;

public class LocationServiceConnection implements ServiceConnection {

	private static String TAG = "LocationServiceConnection";

	private boolean stopOnBound = false;
	private boolean updateLocationCallbackOnBound = false;
	private LocationCallback locationCallback = null;
	private LocationService locationService;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		// Debug
		Log.d(TAG, "[fn] onServiceConnected");

		LocationServiceBinder binder = (LocationServiceBinder)service;
		locationService = binder.getService();
		onBound();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		// Debug
		Log.d(TAG, "[fn] onServiceDisconnected");

	}

	public LocationService getService() {

		// Verbose
		Log.v(TAG, "[fn] getService");

		return locationService;
	}

	public void setStopOnBound(boolean set) {

		// Debug
		Log.d(TAG, "[fn] setStopOnBound");

		stopOnBound = set;
	}

	private void onBound() {

		// Debug
		Log.d(TAG, "[fn] onBound");

		if (stopOnBound) {
			locationService.setStopOnUnbind();
		}

		if (updateLocationCallbackOnBound) {
			locationService.setLocationCallback(locationCallback);
			updateLocationCallbackOnBound = false;
			locationCallback = null;
		}
	}

	public void setSetLocationCallbackOnBound(LocationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] setSetLocationCallbackOnBound");

		updateLocationCallbackOnBound = true;
		locationCallback = callback;
	}
}
