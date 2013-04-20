package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

// Class to create, maintain and update data (SharedPreferences) associated to the app
public class StatusManager {

	private static String TAG = "StatusManager";

	private static StatusManager smInstance = null;

	private static final String EXP_STATUS = "expStatus"; // status of experiment
	private static final String EXP_STATUS_FL_COMPLETED = "expStatusFlCompleted"; // first launch completed

	private final SharedPreferences expStatus; // file containing status of exp
	private final SharedPreferences.Editor eExpStatus; // editor of expStatus

	private final LocationManager locationManager;
	private final ConnectivityManager connManager;
	private NetworkInfo networkInfo;
	private final Context context; // application environment

	public static synchronized StatusManager getInstance(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

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
		if (Config.LOGD) {
			Log.d(TAG, "[fn] StatusManager");
		}

		this.context = context.getApplicationContext();
		expStatus = this.context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
		locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
		connManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connManager.getActiveNetworkInfo();
	}

	/*
	 * Check if first launch is completed
	 */
	public boolean isFirstLaunchCompleted() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isFirstLaunchCompleted");
		}

		return expStatus.getBoolean(EXP_STATUS_FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setFirstLaunchCompleted");
		}

		eExpStatus.putBoolean(EXP_STATUS_FL_COMPLETED, true);
		eExpStatus.commit();
	}

	public boolean isLocationServiceRunning() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isLocationServiceRunning");
		}

		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (LocationService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isNetworkLocEnabled() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isNetworkEnabled");
		}

		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public boolean isDataEnabled() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isDataEnabled");
		}

		networkInfo = connManager.getActiveNetworkInfo();
        return ( ( networkInfo != null && networkInfo.isConnectedOrConnecting() )||( Build.FINGERPRINT.startsWith("generic") ) );	}

	public boolean isDataAndLocationEnabled() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isDataAndLocationEnabled");
		}

		return isNetworkLocEnabled() && isDataEnabled();
	}
}
