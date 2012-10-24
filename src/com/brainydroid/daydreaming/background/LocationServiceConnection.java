package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;

public class LocationServiceConnection implements ServiceConnection {

	private boolean stopOnBound = false;
	private boolean updateLocationCallbackOnBound = false;
	private LocationCallback locationCallback = null;
	private LocationService locationService;
	private final Context _context;

	public LocationServiceConnection(Context context) {
		_context = context.getApplicationContext();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LocationServiceBinder binder = (LocationServiceBinder)service;
		locationService = binder.getService();
		onBound();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {}

	public LocationService getService() {
		return locationService;
	}

	public void setStopOnBound(boolean set) {
		stopOnBound = set;
	}

	private void onBound() {
		if (stopOnBound) {
			locationService.setStopOnUnbind();
			_context.unbindService(this);
		}

		if (updateLocationCallbackOnBound) {
			locationService.setLocationCallback(locationCallback);
			updateLocationCallbackOnBound = false;
			locationCallback = null;
		}
	}

	public void setLocationCallbackOnBound(LocationCallback callback) {
		updateLocationCallbackOnBound = true;
		locationCallback = callback;
	}
}
