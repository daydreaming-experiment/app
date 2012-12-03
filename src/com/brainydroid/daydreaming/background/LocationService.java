package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class LocationService extends Service {

	private static String TAG = "LocationService";

	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean stopOnUnbind = false;
	private final IBinder mBinder = new LocationServiceBinder();
	private LocationCallback _callback = null;
	private Location lastLocation;

	public class LocationServiceBinder extends Binder {

		private final String TAG = "LocationServiceBinder";

		LocationService getService() {

			// Verbose
			if (Config.LOGV) {
				Log.v(TAG, "[fn] getService");
			}

			// Return this instance of LocationService so clients can call public methods
			return LocationService.this;
		}
	}

	public void setLocationCallback(LocationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setLocationCallback");
		}

		_callback = callback;

		if (lastLocation != null) {
			_callback.onLocationReceived(lastLocation);
		}
	}

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();
		initVars();
		startLocationListener();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStartCommand");
		}

		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onDestroy");
		}

		stopLocationListenerIfExists();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBind");
		}

		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onRebind");
		}

		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onUnbind");
		}

		super.onUnbind(intent);

		if (stopOnUnbind) {
			stopSelf();
		}

		return true;
	}

	public void setStopOnUnbind() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setStopOnUnbind");
		}

		stopOnUnbind = true;
	}

	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	}

	private void startLocationListener() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] updateLocationListener");
		}

		stopLocationListenerIfExists();

		locationListener = new LocationListener() {

			private final String TAG = "LocationListener";

			@Override
			public void onLocationChanged(Location location) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (locationListener) onLocationChanged");
				}

				lastLocation = location;

				if (_callback != null) {
					_callback.onLocationReceived(location);
				}
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onProviderDisabled(String provider) {}
		};

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0, 0, locationListener);
	}

	private void stopLocationListenerIfExists() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeLocationListenerIfExists");
		}

		if (locationListener != null) {
			locationManager.removeUpdates(locationListener);
			locationListener = null;
		}
	}
}
