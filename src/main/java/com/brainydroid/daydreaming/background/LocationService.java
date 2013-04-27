package com.brainydroid.daydreaming.background;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.service.RoboService;

public class LocationService extends RoboService {

	private static String TAG = "LocationService";

	private LocationListener locationListener;
	private final IBinder mBinder = new LocationServiceBinder();
	private LocationCallback questionLocationCallback = null;
    private LocationCallback locationItemCallback = null;
	private Location lastLocation;

    @Inject LocationManager locationManager;

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

	public void setLocationItemCallback(LocationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setLocationItemCallback");
		}

		locationItemCallback = callback;

		if (lastLocation != null && locationItemCallback != null) {
			locationItemCallback.onLocationReceived(lastLocation);
		}
	}

    public void setQuestionLocationCallback(LocationCallback callback) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setQuestionLocationCallback");
        }

        questionLocationCallback = callback;

        if (lastLocation != null && questionLocationCallback != null) {
            questionLocationCallback.onLocationReceived(lastLocation);
        }
    }

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();
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

        if (locationItemCallback == null && questionLocationCallback == null) {
			stopSelf();
		}

        // Make sur onUnbind is called again if some clients rebind and re-unbind
		return true;
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

				if (locationItemCallback != null) {
					locationItemCallback.onLocationReceived(location);
				}

                if (questionLocationCallback != null) {
                    questionLocationCallback.onLocationReceived(location);
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
