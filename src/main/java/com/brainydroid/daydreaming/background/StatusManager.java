package com.brainydroid.daydreaming.background;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import com.brainydroid.daydreaming.db.LocationPointsStorage;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.google.inject.Inject;
import com.google.inject.Provider;
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

    /** Preference key storing the Tipi questionnaire completion status */
    private static String EXP_STATUS_TIPI_COMPLETED =
            "expStatusTipiCompleted";

    /** Preference key storing the status of initial questions update */
    private static String EXP_STATUS_QUESTIONS_UPDATED =
            "expStatusQuestionsUpdated";

    /** Preference key storing timestamp of the last sync operation */
    private static String LAST_SYNC_TIMESTAMP = "lastSyncTimestamp";

    /** Preference key storing number of slots per poll */
    private static String QUESTIONS_N_SLOTS_PER_POLL =
            "questionsNSlotsPerPoll";

    /** Preference key storing timestamp of beginning of experiment */
    @SuppressWarnings("FieldCanBeLocal")
    private static String EXP_START_TIMESTAMP = "expStartTimestamp";

    /** Preference key storing latest retrieved ntp timestamp */
    @SuppressWarnings("FieldCanBeLocal")
    private static String LATEST_NTP_TIMESTAMP = "latestNtpTimestamp";

    /** Preference key storing the current mode */
    private static String EXP_CURRENT_MODE = "expCurrentMode";

    public static int MODE_PROD = 0;
    public static int MODE_TEST = 1;
    public static int MODE_DEFAULT = MODE_PROD;

    /**
     * Interval below which we don't need to re-sync data to servers (in
     * milliseconds)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static int SYNC_INTERVAL = 15 * 1000;

    @Inject LocationManager locationManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject ActivityManager activityManager;
    @Inject ProfileStorage profileStorage;
    @Inject PollsStorage pollsStorage;
    @Inject LocationPointsStorage locationPointsStorage;
    // Use a provider here to prevent circular dependencies
    @Inject Provider<CryptoStorage> cryptoStorageProvider;

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
        if (sharedPreferences.getBoolean(getCurrentModeName() + EXP_STATUS_FL_COMPLETED, false)) {
            Logger.d(TAG, "{} - First launch is completed", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - First launch not completed yet", getCurrentModeName());
            return false;
        }
    }

    /**
     * Set the first launch flag to completed.
     */
    public synchronized void setFirstLaunchCompleted() {
        Logger.d(TAG, "{} - Setting first launch to completed", getCurrentModeName());

        if (!isTipiQuestionnaireCompleted()) {
            throw new RuntimeException("Setting first launch to completed can" +
                    " only be done if Tipi questionnaire is also completed");
        }

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_FL_COMPLETED, true);
        eSharedPreferences.commit();
    }

    private synchronized void clearFirstLaunchCompleted() {
        Logger.d(TAG, "{} - Clearing first launch completed", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_FL_COMPLETED);
        eSharedPreferences.commit();
    }

    public synchronized boolean isTipiQuestionnaireCompleted() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + EXP_STATUS_TIPI_COMPLETED, false)) {
            Logger.d(TAG, "{} - Tipi questionnaire is completed", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Tipi questionnaire not completed yet", getCurrentModeName());
            return false;
        }
    }

    public synchronized void setTipiQuestionnaireCompleted() {
        Logger.d(TAG, "{} - Setting Tipi questionnaire to completed", getCurrentModeName());

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_TIPI_COMPLETED, true);
        eSharedPreferences.commit();
    }

    private synchronized void clearTipiQuestionnaireCompleted() {
        Logger.d(TAG, "{} - Clearing Tipi questionnaire completed", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_TIPI_COMPLETED);
        eSharedPreferences.commit();
    }

    /**
     * Check if the questions have been updated.
     *
     * @return {@code true} if the questions have been updated,
     *         {@code false} otherwise
     */
    public synchronized boolean areQuestionsUpdated() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + EXP_STATUS_QUESTIONS_UPDATED,
                false)) {
            Logger.d(TAG, "{} - Questions are updated", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Questions not updated yet", getCurrentModeName());
            return false;
        }
    }

    /**
     * Set the updated questions flag to completed.
     */
    public synchronized void setQuestionsUpdated() {
        Logger.d(TAG, "{} - Setting questions to updated", getCurrentModeName());

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_QUESTIONS_UPDATED, true);
        eSharedPreferences.commit();
    }

    private synchronized void clearQuestionsUpdated() {
        Logger.d(TAG, "{} - Clearing questions updated", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_QUESTIONS_UPDATED);
        eSharedPreferences.commit();
    }

    /**
     * Get the number of slots per poll
     */
    public synchronized int getNSlotsPerPoll() {
        int nSlotsPerPoll = sharedPreferences.getInt(
                getCurrentModeName() + QUESTIONS_N_SLOTS_PER_POLL, -1);
        Logger.v(TAG, "{} - nSlotsPerPoll is {}", getCurrentModeName(), nSlotsPerPoll);
        return nSlotsPerPoll;
    }

    public synchronized void setNSlotsPerPoll(int nSlotsPerPoll) {
        Logger.d(TAG, "{} - Setting nSlotsPerPoll to {}", getCurrentModeName(), nSlotsPerPoll);
        eSharedPreferences.putInt(getCurrentModeName() + QUESTIONS_N_SLOTS_PER_POLL, nSlotsPerPoll);
        eSharedPreferences.commit();
    }

    private synchronized void clearNSlotsPerPoll() {
        Logger.d(TAG, "{} - Clearing nSlotsPerPoll", getCurrentModeName());
        eSharedPreferences.remove(getCurrentModeName() + QUESTIONS_N_SLOTS_PER_POLL);
        eSharedPreferences.commit();
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
        Logger.d(TAG, "{} - Setting last sync timestamp to now", getCurrentModeName());
        eSharedPreferences.putLong(getCurrentModeName() + LAST_SYNC_TIMESTAMP, now);
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
        long threshold = sharedPreferences.getLong(getCurrentModeName() + LAST_SYNC_TIMESTAMP,
                - SYNC_INTERVAL) + SYNC_INTERVAL;
        if (threshold < SystemClock.elapsedRealtime()) {
            Logger.d(TAG, "{} - Last sync was long ago", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Last sync was not long ago", getCurrentModeName());
            return false;
        }
    }

    public synchronized long getExperimentStartTimestamp() {
        return sharedPreferences.getLong(getCurrentModeName() + EXP_START_TIMESTAMP, -1);
    }

    public synchronized void setExperimentStartTimestamp(long timestamp) {
        Logger.d(TAG, "{} - Setting experiment start timestamp to {}", getCurrentModeName(), timestamp);
        eSharedPreferences.putLong(getCurrentModeName() + EXP_START_TIMESTAMP, timestamp);
        eSharedPreferences.commit();
    }

    private synchronized void clearExperimentStartTimestamp() {
        Logger.d(TAG, "{} - Clearing experiment start timestamp", getCurrentModeName());
        eSharedPreferences.remove(getCurrentModeName() + EXP_START_TIMESTAMP);
        eSharedPreferences.commit();
    }

    public synchronized void setLatestNtpTime(long timestamp) {
        Logger.d(TAG, "Setting latest ntp requested time to {}", timestamp);
        eSharedPreferences.putLong(LATEST_NTP_TIMESTAMP, timestamp);
        eSharedPreferences.commit();

        if (!sharedPreferences.contains(getCurrentModeName() + EXP_START_TIMESTAMP)) {
            Logger.w(TAG, "expStartTimestamp doesn't seem to have been set. " +
                    "Setting it to this latest (and probably first) NTP " +
                    "timestamp");
            setExperimentStartTimestamp(timestamp);
        }
    }

    public synchronized long getLatestNtpTime() {
        return sharedPreferences.getLong(LATEST_NTP_TIMESTAMP, -1);
    }

    /**
     * Get current mode (test or prod).
     */
    public synchronized int getCurrentMode() {
        int mode = sharedPreferences.getInt(EXP_CURRENT_MODE, MODE_DEFAULT);
        Logger.d(TAG, "Current mode is {}", mode);
        return mode;
    }

    public synchronized String getCurrentModeName() {
        String modeName;
        if (getCurrentMode() == MODE_PROD) {
            modeName = ProfileStorage.PROFILE_NAME_PROD;
        } else {
            modeName = ProfileStorage.PROFILE_NAME_TEST;
        }
        Logger.v(TAG, "Current mode name is {}", modeName);
        return modeName;
    }

    /**
     * Set current mode.
     */
    private synchronized void setCurrentMode(int mode) {
        Logger.d(TAG, "Setting current mode to {}", mode);
        eSharedPreferences.putInt(EXP_CURRENT_MODE, mode);
        eSharedPreferences.commit();
    }

    public synchronized void switchToTestMode() {
        Logger.d(TAG, "Doing full switch to test mode");

        // Clear pending uploads (before switch)
        pollsStorage.removeUploadablePolls();
        locationPointsStorage.removeUploadableLocationPoints();

        // Do the switch
        setCurrentMode(MODE_TEST);

        // Clear local flags (after switch)
        clearFirstLaunchCompleted();
        clearTipiQuestionnaireCompleted();
        clearQuestionsUpdated();
        clearNSlotsPerPoll();
        clearExperimentStartTimestamp();

        // Clear test profile and crypto storage (after switch)
        profileStorage.clearProfile();
        cryptoStorageProvider.get().clearStore();
    }

    public synchronized void switchToProdMode() {
        Logger.d(TAG, "Doing full switch to production mode");

        // Clear pending uploads (before switch)
        pollsStorage.removeUploadablePolls();
        locationPointsStorage.removeUploadableLocationPoints();

        // Do the switch
        setCurrentMode(MODE_PROD);

        // Don't clear local flags (after switch), so that experiment isn't restarted

        // And don't clear test profile and crypto storage (after switch)
    }

}
