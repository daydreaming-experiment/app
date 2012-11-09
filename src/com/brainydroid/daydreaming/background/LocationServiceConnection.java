package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.brainydroid.daydreaming.background.LocationService.LocationServiceBinder;

public class LocationServiceConnection implements ServiceConnection {

	private static String TAG = "LocationServiceConnection";

	private boolean stopOnUnbindToSet = false;
	private LocationCallback callbackToSet = null;
	private LocationService locationService;
	private boolean sBound = false;
	private final Context _context;

	public LocationServiceConnection(Context context) {
		super();

		// Debug
		Log.d(TAG, "[fn] LocationServiceConnection");

		_context = context;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		// Debug
		Log.d(TAG, "[fn] onServiceConnected");

		LocationServiceBinder binder = (LocationServiceBinder)service;
		locationService = binder.getService();
		onConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		// Debug
		Log.d(TAG, "[fn] onServiceDisconnected");

		locationService = null;
	}

	public void startLocationService() {

		// Debug
		Log.d(TAG, "[fn] startLocationService");

		Intent locationServiceIntent = new Intent(_context, LocationService.class);
		_context.startService(locationServiceIntent);
	}

	public void bindLocationService() {

		// Debug
		Log.d(TAG, "[fn] bindLocationService");

		Intent locationServiceIntent = new Intent(_context, LocationService.class);
		_context.bindService(locationServiceIntent, this, 0);
		sBound = true;
	}

	public void unbindLocationService() {

		// Debug
		Log.d(TAG, "[fn] unbindLocationService");

		if (sBound) {
			_context.unbindService(this);
			sBound = false;
		}
	}

	public LocationService getService() {

		// Verbose
		Log.v(TAG, "[fn] getService");

		return locationService;
	}

	private void onConnected() {

		// Debug
		Log.d(TAG, "[fn] onConnected");

		if (callbackToSet != null) {
			locationService.setLocationCallback(callbackToSet);
			callbackToSet = null;
		}

		if (stopOnUnbindToSet) {
			locationService.setStopOnUnbind();
			stopOnUnbindToSet = false;
		}
	}

	public void setLocationCallback(LocationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] setLocationCallback");

		if (sBound && locationService != null) {
			locationService.setLocationCallback(callback);
			callbackToSet = null;
		} else {
			callbackToSet = callback;
		}
	}

	public void setStopOnUnbind() {

		// Debug
		Log.d(TAG, "[fn] setStopOnUnbind");

		if (sBound && locationService != null) {
			locationService.setStopOnUnbind();
			stopOnUnbindToSet = false;
		} else {
			stopOnUnbindToSet = true;
		}
	}
}
