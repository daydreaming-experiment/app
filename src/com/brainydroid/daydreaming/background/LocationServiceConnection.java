package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;

public class LocationServiceConnection implements ServiceConnection {

	private boolean stopServiceOnBound = false;
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

	public void setStopServiceOnBound(boolean stop) {
		stopServiceOnBound = stop;
	}

	private void onBound() {
		if (stopServiceOnBound) {
			locationService.setStopServiceOnUnbind();
			_context.unbindService(this);
		}
	}
}
