package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;


public class LocationService extends Service {

	private LocationManager locationManager;
	private LocationListener locationListener;
	private StatusManager status;
	private boolean stopOnUnbind = false;
	private final IBinder mBinder = new LocationServiceBinder();
	private LocationCallback _callback = null;

	public class LocationServiceBinder extends Binder {
		LocationService getService() {
			// Return this instance of LocationService so clients can call public methods
			return LocationService.this;
		}
	}

	public void setLocationCallback(LocationCallback callback) {
		Toast.makeText(this, "Setting Location callback", Toast.LENGTH_SHORT).show();
		_callback = callback;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initVars();
		if (!status.isDataAndLocationEnabled()) {
			stopSelf();
			return;
		}
		startLocationListener();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		stopLocationListener();
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

		if (stopOnUnbind) {
			stopSelf();
		}

		return true;
	}

	public void setStopOnUnbind() {
		stopOnUnbind = true;
	}

	private void initVars() {
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		status = StatusManager.getInstance(this);
	}

	private void startLocationListener() {
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				Toast.makeText(LocationService.this,
						"New location received (prec: " + location.getAccuracy() + ")",
						Toast.LENGTH_SHORT).show();
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

	private void stopLocationListener() {
		if (locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
	}
}
