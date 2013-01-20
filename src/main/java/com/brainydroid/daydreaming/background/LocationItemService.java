package com.brainydroid.daydreaming.background;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.brainydroid.daydreaming.db.LocationItem;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.ui.Config;

import java.lang.Override;
import java.lang.String;

public class LocationItemService extends Service {

	private static String TAG = "LocationItemService";

    private static String STOP_LOCATION_LISTENING = "stopLocationListening";
    private static long SAMPLE_INTERVAL = 18 * 60 * 1000; // 18 minutes (in milliseconds)
    private static long LISTENING_TIME = 2 * 60 * 1000; // 2 minutes (in milliseconds)

    private boolean sntpRequestDone = false;
    private boolean serviceConnectionDone = false;
    private LocationItem locationItem = null;

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

		// The service stops itself through callbacks set in stopLocationListening or startLocationListening
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

        // No need for an NTP request
        setSntpRequestDone();

        ServiceConnectionCallback serviceConnectionCallback = new ServiceConnectionCallback() {

            private final String TAG = "ServiceConnectionCallback";

            @Override
            public void onServiceConnected() {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] onServiceConnected");
                }

                LocationItemService.this.setServiceConnectionDone();
            }

        };

        locationServiceConnection.setOnServiceConnectedCallback(serviceConnectionCallback);

        if (status.isLocationServiceRunning()) {
            locationServiceConnection.bindLocationService();
            // The serviceConnectionCallback stops this service, which calls onDestroy.
            // unBind happens in onDestroy, and the LocationService finishes if nobody else has listeners registered
            locationServiceConnection.clearLocationItemCallback();
        }
    }

    private void startLocationListening() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startLocationListening");
        }

        ServiceConnectionCallback serviceConnectionCallback = new ServiceConnectionCallback() {

            private final String TAG = "ServiceConnectionCallback";

            @Override
            public void onServiceConnected() {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] onServiceConnected");
                }

                LocationItemService.this.setServiceConnectionDone();
            }

        };

        locationServiceConnection.setOnServiceConnectedCallback(serviceConnectionCallback);

        locationItem = new LocationItem(this);

        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] (locationCallback) onLocationReceived");
                }

                locationItem.setLocation(location);
                // save() is called from saveAndStopSelfIfAllDone
            }

        };

        locationServiceConnection.setLocationItemCallback(locationCallback);

        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] (sntpCallback) onTimeReceived");
                }

                if (sntpClient != null) {
                    locationItem.setTimestamp(sntpClient.getNow());
                    // save() is called from saveAndStopSelfIfAllDone
                }

                LocationItemService.this.setSntpRequestDone();
            }

        };

        SntpClient sntpClient = new SntpClient();
        sntpClient.asyncRequestTime(sntpCallback);

        if (!status.isLocationServiceRunning()) {
            locationServiceConnection.bindLocationService();
            // The serviceConnectionCallback stops this service, which calls onDestroy.
            // unBind happens in onDestroy, and the LocationService finishes if nobody else has listeners registered
            locationServiceConnection.startLocationService();
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

    private void setSntpRequestDone() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setSntpRequestDone");
        }

        sntpRequestDone = true;
        saveAndStopSelfIfAllDone();
    }

    private void setServiceConnectionDone() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setServiceConnectionDone");
        }

        serviceConnectionDone = true;
        saveAndStopSelfIfAllDone();
    }

    private void saveAndStopSelfIfAllDone() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveAndStopSelfIfAllDone");
        }

        if (locationItem != null) {
            locationItem.save();
        }

        if (sntpRequestDone && serviceConnectionDone) {
            stopSelf();
        }
    }
}
