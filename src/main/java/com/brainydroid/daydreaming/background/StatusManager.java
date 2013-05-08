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

/**
 * Manage global application status (like first launch) and collect
 * information on the device's status.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
@Singleton
public class StatusManager {

    private static String TAG = "StatusManager";

    /** Preference key storing the first launch status */
    private static String EXP_STATUS_FL_COMPLETED =
            "expStatusFlCompleted";

    /** Preference key storing the status of initial questions update */
    private static String EXP_STATUS_QUESTIONS_UPDATED =
            "expStatusQuestionsUpdated";

    @Inject LocationManager locationManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject ActivityManager activityManager;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    /**
     * Initialize the {@code SharedPreferences} editor.
     */
    @Inject
    public StatusManager(SharedPreferences sharedPreferences) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] StatusManager");
        }

        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    /**
     * Check if first launch is completed.
     *
     * @return {@code true} if first launch has completed,
     *         {@code false} otherwise
     */
    public boolean isFirstLaunchCompleted() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isFirstLaunchCompleted");
        }

        return sharedPreferences.getBoolean(EXP_STATUS_FL_COMPLETED, false);
    }

    /**
     * Set the first launch flag to completed.
     */
    public void setFirstLaunchCompleted() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setFirstLaunchCompleted");
        }

        eSharedPreferences.putBoolean(EXP_STATUS_FL_COMPLETED, true);
        eSharedPreferences.commit();
    }

    /**
     * Check if the questions have been updated.
     *
     * @return {@code true} if the questions have been updated,
     *         {@code false} otherwise
     */
    public boolean areQuestionsUpdated() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] areQuestionsUpdated");
        }

        return sharedPreferences.getBoolean(EXP_STATUS_QUESTIONS_UPDATED,
                false);
    }

    /**
     * Set the updated questions flag to completed.
     */
    public void setQuestionsUpdated() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setQuestionsUpdated");
        }

        eSharedPreferences.putBoolean(EXP_STATUS_QUESTIONS_UPDATED, true);
        eSharedPreferences.commit();
    }

    /**
     * Check if {@link LocationService} is running.
     *
     * @return {@code true} if {@code LocationService} is running,
     *         {@code false} otherwise
     */
    public boolean isLocationServiceRunning() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isLocationServiceRunning");
        }

        // This hack was found on StackOverflow
        for (RunningServiceInfo service :
                activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the network location provider is enabled.
     *
     * @return {@code true} if the network location provider is enabled,
     *         {@code false} otherwise
     */
    public boolean isNetworkLocEnabled() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isNetworkEnabled");
        }

        return locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Check if data connection is enabled (or connecting).
     *
     * @return {@code true} if data connection is enabled or connecting,
     *         {@code false} otherwise
     */
    public boolean isDataEnabled() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isDataEnabled");
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null &&
                networkInfo.isConnectedOrConnecting());
    }

    /**
     * Check if both data connection and network location provider are
     * enabled (data connection can also be still connecting only).
     *
     * @return {@code true} if the network location provider is enabled and
     *         data connection is enabled or connecting,
     *         {@code false} otherwise
     */
    public boolean isDataAndLocationEnabled() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isDataAndLocationEnabled");
        }

        return isNetworkLocEnabled() && isDataEnabled();
    }

}
