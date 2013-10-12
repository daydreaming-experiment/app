package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
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

    /** Preference key storing timestamp of the last sync operation */
    private static String LAST_SYNC_TIMESTAMP = "lastSyncTimestamp";

    /** Transient variable and possible values storing questionsUpdate status */
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private String questionsUpdateStatus = null;
    public static String QUESTIONS_UPDATE_FAILED = "questionsUpdateFailed";
    public static String QUESTIONS_UPDATE_SUCCEEDED =
            "questionsUpdateSucceeded";
    public static String QUESTIONS_UPDATE_MALFORMED =
            "questionsUpdateMalformed";

    /** Callback called when questionsUpdateStatus changes */
    private QuestionsUpdateCallback questionsUpdateCallback = null;

    /**
     * Interval below which we don't need to re-sync data to servers (in
     * milliseconds)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static int SYNC_INTERVAL = 15 * 1000;

    @Inject LocationManager locationManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject ActivityManager activityManager;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    /**
     * Initialize the {@link SharedPreferences} editor.
     */
    @Inject
    public StatusManager(SharedPreferences sharedPreferences) {
        Logger.d(TAG, "StatusManager created");
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    /**
     * Check if first launch is completed.
     *
     * @return {@code true} if first launch has completed,
     *         {@code false} otherwise
     */
    public synchronized boolean isFirstLaunchCompleted() {
        if (sharedPreferences.getBoolean(EXP_STATUS_FL_COMPLETED, false)) {
            Logger.d(TAG, "First launch is completed");
            return true;
        } else {
            Logger.d(TAG, "First launch not completed yet");
            return false;
        }
    }

    /**
     * Set the first launch flag to completed.
     */
    public synchronized void setFirstLaunchCompleted() {
        Logger.d(TAG, "Setting first launch to completed");

        eSharedPreferences.putBoolean(EXP_STATUS_FL_COMPLETED, true);
        eSharedPreferences.commit();
    }

    /**
     * Check if the questions have been updated.
     *
     * @return {@code true} if the questions have been updated,
     *         {@code false} otherwise
     */
    public synchronized boolean areQuestionsUpdated() {
        if (sharedPreferences.getBoolean(EXP_STATUS_QUESTIONS_UPDATED,
                false)) {
            Logger.d(TAG, "Questions are updated");
            return true;
        } else {
            Logger.d(TAG, "Questions not updated yet");
            return false;
        }
    }

    /**
     * Set the updated questions flag to completed.
     */
    public synchronized void setQuestionsUpdated() {
        Logger.d(TAG, "Setting questions to updated");

        eSharedPreferences.putBoolean(EXP_STATUS_QUESTIONS_UPDATED, true);
        eSharedPreferences.commit();
    }

    public synchronized void setQuestionsUpdateStatusCallback
            (QuestionsUpdateCallback callback) {
        Logger.d(TAG, "Setting questionsUpdateCallback");
        questionsUpdateCallback = callback;
    }

    public synchronized void clearQuestionsUpdateCallback() {
        Logger.d(TAG, "Clearing questionsUpdateCallback");
        questionsUpdateCallback = null;
    }

    public synchronized void setQuestionsUpdateStatus(String status) {
        Logger.d(TAG, "Setting the questionsUpdateStatus to {}", status);
        questionsUpdateStatus = status;
        if (questionsUpdateCallback != null) {
            questionsUpdateCallback.onQuestionsUpdateStatusChange(status);
        }
    }

    /**
     * Check if {@link LocationService} is running.
     *
     * @return {@code true} if {@link LocationService} is running,
     *         {@code false} otherwise
     */
    public synchronized boolean isLocationServiceRunning() {
        // This hack was found on StackOverflow
        for (RunningServiceInfo service :
                activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(
                    service.service.getClassName())) {
                Logger.d(TAG, "LocationService is running");
                return true;
            }
        }

        Logger.d(TAG, "LocationService is not running");
        return false;
    }

    /**
     * Check if the network location provider is enabled.
     *
     * @return {@code true} if the network location provider is enabled,
     *         {@code false} otherwise
     */
    public synchronized boolean isNetworkLocEnabled() {
        if (locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            Logger.d(TAG, "Network location is enabled");
            return true;
        } else {
            Logger.d(TAG, "Network location is disabled");
            return false;
        }
    }

    /**
     * Check if data connection is enabled (or connecting).
     *
     * @return {@code true} if data connection is enabled or connecting,
     *         {@code false} otherwise
     */
    public synchronized boolean isDataEnabled() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Logger.d(TAG, "Data is enabled");
            return true;
        } else {
            Logger.d(TAG, "Data is disabled");
            return false;
        }
    }

    /**
     * Check if both data connection and network location provider are
     * enabled (data connection can also be still connecting only).
     *
     * @return {@code true} if the network location provider is enabled and
     *         data connection is enabled or connecting,
     *         {@code false} otherwise
     */
    public synchronized boolean isDataAndLocationEnabled() {
        if (isNetworkLocEnabled() && isDataEnabled()) {
            Logger.d(TAG, "Data and network location are enabled");
            return true;
        } else {
            Logger.d(TAG, "Either data or network location is disabled");
            return false;
        }
    }

    /**
     * Set the timestamp of the last sync operation to now.
     */
    public synchronized void setLastSyncToNow() {
        long now = SystemClock.elapsedRealtime();
        Logger.d(TAG, "Setting last sync timestamp to now");
        eSharedPreferences.putLong(LAST_SYNC_TIMESTAMP, now);
        eSharedPreferences.commit();
    }

    /**
     * Check if a sync operation was made not long ago.
     * <p/>
     * If a sync operation was made less than {@link #SYNC_INTERVAL}
     * milliseconds ago, this method will return false. Otherwise it will
     * return true. Use {@link #setLastSyncToNow} to set the timestamp of the
     * last sync operation when syncing.
     *
     * @return {@code boolean} indicating if the last sync operation was long
     *         ago or not
     */
    public synchronized boolean isLastSyncLongAgo() {
        // If last sync timestamp is present, make sure now is after the
        // threshold to force a sync.
        long threshold = sharedPreferences.getLong(LAST_SYNC_TIMESTAMP,
                - SYNC_INTERVAL) + SYNC_INTERVAL;
        if (threshold < SystemClock.elapsedRealtime()) {
            Logger.d(TAG, "Last sync was long ago");
            return true;
        } else {
            Logger.d(TAG, "Last sync was not long ago");
            return false;
        }
    }

}
