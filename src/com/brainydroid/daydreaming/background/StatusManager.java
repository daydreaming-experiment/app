package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



// Class to create, maintain and update data (SharedPreferences) associated to the app
public class StatusManager {

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
		_context = context.getApplicationContext();
		expStatus = _context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
		locationServiceConnection = new LocationServiceConnection(_context);
		locationManager = (LocationManager)_context.getSystemService(Context.LOCATION_SERVICE);
		connManager = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connManager.getActiveNetworkInfo();
	}

	/*
	 * Check if activity was already launched
	 */
	public boolean isFirstLaunchStarted() {
		return expStatus.getBoolean(FL_STARTED, false);
	}


	public void setFirstLaunchStarted() {
		eExpStatus.putBoolean(FL_STARTED, true);
		eExpStatus.commit();
	}

	/*
	 * Check if first launch is completed
	 */
	public boolean isFirstLaunchCompleted() {
		return expStatus.getBoolean(FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {
		eExpStatus.putBoolean(FL_COMPLETED, true);
		eExpStatus.commit();
	}

	public boolean isLocationServiceRunning() {
		ActivityManager manager = (ActivityManager)_context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (LocationService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isClearing() {
		return expStatus.getBoolean(IS_CLEARING, false);
	}

	// Clearing expstatus
	public void startClear() {
		eExpStatus.clear();
		eExpStatus.putBoolean(IS_CLEARING, true);
		eExpStatus.commit();
	}

	public void finishClear() {
		eExpStatus.clear();
		eExpStatus.commit();
		smInstance = null;
	}

	public void startLocationService() {
		if (!isLocationServiceRunning()) {
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			_context.startService(locationServiceIntent);
		}
	}

	public void stopLocationService() {
		if (isLocationServiceRunning()) {
			locationServiceConnection.setStopOnBound(true);
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			_context.bindService(locationServiceIntent, locationServiceConnection, 0);
			_context.unbindService(locationServiceConnection);
		}
	}

	public void setLocationCallback(LocationCallback callback) {
		if (isLocationServiceRunning()) {
			Intent locationServiceIntent = new Intent(_context, LocationService.class);
			locationServiceConnection.setLocationCallbackOnBound(callback);
			_context.bindService(locationServiceIntent, locationServiceConnection, 0);
			_context.unbindService(locationServiceConnection);
		}
	}

	public boolean isNetworkLocEnabled() {
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public boolean isDataEnabled() {
		networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public boolean isDataAndLocationEnabled() {
		return isNetworkLocEnabled() && isDataEnabled();
	}
}