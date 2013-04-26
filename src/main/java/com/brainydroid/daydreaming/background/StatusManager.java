package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StatusManager {

	private static String TAG = "StatusManager";

	private static final String EXP_STATUS_FL_COMPLETED = "expStatusFlCompleted"; // first launch completed

    @Inject SharedPreferences sharedPreferences;
    @Inject LocationManager locationManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject ActivityManager activityManager;

    /*
     * Check if first launch is completed
     */
	public boolean isFirstLaunchCompleted() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isFirstLaunchCompleted");
		}

		return sharedPreferences.getBoolean(EXP_STATUS_FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setFirstLaunchCompleted");
		}

        SharedPreferences.Editor eSharedPreferences = sharedPreferences.edit();
		eSharedPreferences.putBoolean(EXP_STATUS_FL_COMPLETED, true);
		eSharedPreferences.commit();
	}

	public boolean isLocationServiceRunning() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isLocationServiceRunning");
		}

		for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
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

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

	public boolean isDataAndLocationEnabled() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isDataAndLocationEnabled");
		}

		return isNetworkLocEnabled() && isDataEnabled();
	}
}
