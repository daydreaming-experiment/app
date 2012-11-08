package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// Class to create, maintain and update data (SharedPreferences) associated to the app
public class StatusManager {

	private static String TAG = "StatusManager";

	private static StatusManager smInstance = null;

	private static final String EXP_STATUS = "expStatus"; // status of experiment
	private static final String FL_STARTED = "flStarted"; // first launch started
	private static final String FL_COMPLETED = "flCompleted"; // first launch completed
	private static final String IS_CLEARING = "isClearing";

	private final SharedPreferences expStatus; // file containing status of exp
	private final SharedPreferences.Editor eExpStatus; // editor of expStatus

	private final LocationServiceConnection locationServiceConnection;
	private final LocationManager locationManager;
	private final ConnectivityManager connManager;
	private NetworkInfo networkInfo;
	private final Context _context; // application environment

	public static synchronized StatusManager getInstance(Context context) {

		// Debug
		Log.d(TAG, "[fn] getInstance");

		if (smInstance == null) {
			smInstance = new StatusManager(context);
		}
		return smInstance;
	}

	/*
	 * Constructor.
	 * loads context, assign initial preferences
	 */
	private StatusManager(Context context) {

		// Debug
		Log.d(TAG, "[fn] StatusManager");

		_context = context.getApplicationContext();
		expStatus = _context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
		locationServiceConnection = new LocationServiceConnection();
		locationManager = (LocationManager)_context.getSystemService(Context.LOCATION_SERVICE);
		connManager = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connManager.getActiveNetworkInfo();
	}

	/*
	 * Check if activity was already launched
	 */
	public boolean isFirstLaunchStarted() {

		// Debug
		Log.d(TAG, "[fn] isFirstLaunchStarted");

		return expStatus.getBoolean(FL_STARTED, false);
	}


	public void setFirstLaunchStarted() {

		// Debug
		Log.d(TAG, "[fn] setFirstLaunchStarted");

		eExpStatus.putBoolean(FL_STARTED, true);
		eExpStatus.commit();
	}

	/*
	 * Check if first launch is completed
	 */
	public boolean isFirstLaunchCompleted() {

		// Debug
		Log.d(TAG, "[fn] isFirstLaunchCompleted");

		return expStatus.getBoolean(FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {

		// Debug
		Log.d(TAG, "[fn] setFirstLaunchCompleted");

		eExpStatus.putBoolean(FL_COMPLETED, true);
		eExpStatus.commit();
	}

	public boolean isLocationServiceRunning() {

		// Debug
		Log.d(TAG, "[fn] isLocationServiceRunning");

		ActivityManager manager = (ActivityManager)_context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (LocationService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isClearing() {

		// Debug
		Log.d(TAG, "[fn] isClearing");

		return expStatus.getBoolean(IS_CLEARING, false);
	}

	// Clearing expstatus
	public void startClear() {

		// Debug
		Log.d(TAG, "[fn] startClear");

		eExpStatus.clear();
		eExpStatus.putBoolean(IS_CLEARING, true);
		eExpStatus.commit();
	}

	public void finishClear() {

		// Debug
		Log.d(TAG, "[fn] finishClear");

		eExpStatus.clear();
		eExpStatus.commit();
		smInstance = null;
	}

	public void startLocationService() {

		// Debug
		Log.d(TAG, "[fn] startLocationService");

		if (!isLocationServiceRunning()) {
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			_context.startService(locationServiceIntent);
		}
	}

	public void stopLocationService() {

		// Debug
		Log.d(TAG, "[fn] stopLocationService");

		if (isLocationServiceRunning()) {
			locationServiceConnection.setStopOnBound(true);
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			_context.bindService(locationServiceIntent, locationServiceConnection, 0);
			_context.unbindService(locationServiceConnection);
		}
	}

	public void setLocationCallback(LocationCallback callback) {

		// Debug
		Log.d(TAG, "[fn] setLocationCallback");

		if (isLocationServiceRunning()) {
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			locationServiceConnection.setSetLocationCallbackOnBound(callback);
			_context.bindService(locationServiceIntent, locationServiceConnection, 0);
			_context.unbindService(locationServiceConnection);
		}
	}

	public boolean isNetworkLocEnabled() {

		// Debug
		Log.d(TAG, "[fn] isNetworkEnabled");

		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public boolean isDataEnabled() {

		// Debug
		Log.d(TAG, "[fn] isDataEnabled");

		networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public boolean isDataAndLocationEnabled() {

		// Debug
		Log.d(TAG, "[fn] isDataAndLocationEnabled");

		return isNetworkLocEnabled() && isDataEnabled();
	}
}
