package com.brainydroid.daydreaming.background;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.brainydroid.daydreaming.db.LocationItem;
import com.brainydroid.daydreaming.ui.Config;

public class LocationItemService extends Service {

	private static String TAG = "LocationItemService";

    private static String STOP_LOCATION_LISTENING = "stopLocationListening";
    private static long SAMPLE_INTERVAL = 20 * 60 * 1000; // 20 minutes (in milliseconds)
    private static long LISTENING_TIME = 3 * 60 * 1000; // 3 minutes (in milliseconds)

	private AlarmManager alarmManager;
    private StatusManager status;
    private LocationServiceConnection locationServiceConnection;

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStartCommand");
		}

		super.onStartCommand(intent, flags, startId);

		initVars();

        if (intent.getBooleanExtra(STOP_LOCATION_LISTENING, false)) {
            stopLocationListening();
            scheduleNextService();
        } else {
            if (status.isDataAndLocationEnabled()) {
                startLocationListening();
                scheduleStopLocationListening();
            }
        }

		stopSelf();
		return START_REDELIVER_INTENT;
	}

    @Override
	public void onDestroy() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onDestroy");
		}

        locationServiceConnection.unbindLocationService();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBind");
		}

		// Don't allow binding
		return null;
	}

	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        status = StatusManager.getInstance(this);
        locationServiceConnection = new LocationServiceConnection(this);
	}

    private void stopLocationListening() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] stopLocationListening");
        }

        if (status.isLocationServiceRunning()) {
            locationServiceConnection.bindLocationService();
            // unBind happens in onDestroy, and the LocationService finishes if nobody else has listeners registered
            locationServiceConnection.clearLocationItemCallback();
        }
    }

    private void startLocationListening() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startLocationListening");
        }

        final LocationItem locationItem = new LocationItem(this);

        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] (locationCallback) onLocationReceived");
                }

                locationItem.setLocation(location);
                locationItem.save();
            }

        };

        locationServiceConnection.setLocationItemCallback(locationCallback);

        if (!status.isLocationServiceRunning()) {
            locationServiceConnection.startLocationService();
            locationServiceConnection.bindLocationService();
        } else {
            locationServiceConnection.bindLocationService();
        }
    }

    private void scheduleNextService() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] scheduleNextService");
        }

        long scheduledTime = SystemClock.elapsedRealtime() + SAMPLE_INTERVAL;
        Intent intent = new Intent(this, LocationItemService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }

    private void scheduleStopLocationListening() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] scheduleStopLocationListening");
        }

        long scheduledTime = SystemClock.elapsedRealtime() + LISTENING_TIME;
        Intent intent = new Intent(this, LocationItemService.class);
        intent.putExtra(STOP_LOCATION_LISTENING, true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }
}
