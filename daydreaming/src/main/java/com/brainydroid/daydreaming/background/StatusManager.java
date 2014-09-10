package com.brainydroid.daydreaming.background;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.brainydroid.daydreaming.db.LocationPointsStorage;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.Calendar;

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
    private static String EXP_STATUS_PARAMETERS_UPDATED =
            "expStatusQuestionsUpdated";
    /** Preference key storing the status of initial questions update */
    private static String EXP_STATUS_PARAMETERS_FLUSHED =
            "expStatusQuestionsFlushed";

    /** Preference key storing timestamp of the last sync operation */
    private static String LAST_SYNC_TIMESTAMP = "lastSyncTimestamp";

    /** Preference key storing timestamp of beginning of experiment */
    @SuppressWarnings("FieldCanBeLocal")
    private static String EXP_START_TIMESTAMP = "expStartTimestamp";

    /** Preference key storing latest retrieved ntp timestamp */
    @SuppressWarnings("FieldCanBeLocal")
    private static String LATEST_NTP_TIMESTAMP = "latestNtpTimestamp";

    private static String LATEST_SCHEDULER_SERVICE_SYSTEM_TIMESTAMP =
            "latestSchedulerServiceSystemTimestamp";
    private static String LATEST_LOCATION_POINT_SERVICE_SYSTEM_TIMESTAMP =
            "latestLocationPointServiceSystemTimestamp";

    /** Preference key storing the current mode */
    private static String EXP_CURRENT_MODE = "expCurrentMode";

    public static final String ACTION_PARAMETERS_UPDATED = "actionParametersUpdated";

    public static int MODE_PROD = 0;
    public static int MODE_TEST = 1;
    public static int MODE_DEFAULT = MODE_PROD;
    public static String MODE_NAME_TEST = "test";
    public static String MODE_NAME_PROD = "production";
    public static String[] AVAILABLE_MODE_NAMES = {MODE_NAME_PROD, MODE_NAME_TEST};

    /** Delay after which SchedulerService must be restarted if it hasn't run. 2 days. */
    public static long RESTART_SCHEDULER_SERVICE_DELAY = 2 * 24 * 60 * 60 * 1000;

    /** Delay after which LocationPointService must be restarted if it hasn't run. 1 hour. */
    public static long RESTART_LOCATION_POINT_SERVICE_DELAY = 1 * 60 * 60 * 1000;

    private int cachedCurrentMode = MODE_DEFAULT;

    /**
     * Delay below which we don't need to re-sync data to servers (in
     * milliseconds), for production and test modes
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static int SYNC_DELAY_PROD = 5 * 60 * 1000;  // 5 minutes
    @SuppressWarnings("FieldCanBeLocal")
    private static int SYNC_DELAY_TEST = 10 * 1000;      // 10 seconds

    @Inject LocationManager locationManager;
    @Inject ConnectivityManager connectivityManager;
    @Inject ActivityManager activityManager;
    @Inject Context context;
    // Use providers here to prevent circular dependencies
    @Inject Provider<ProfileStorage> profileStorageProvider;
    @Inject Provider<SequencesStorage> sequencesStorageProvider;
    @Inject Provider<LocationPointsStorage> locationPointsStorageProvider;
    @Inject Provider<ParametersStorage> parametersStorageProvider;
    @Inject Provider<CryptoStorage> cryptoStorageProvider;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    private void sendParametersUpdatedBroadcast() {
        Logger.v(TAG, "Sending ACTION_PARAMETERS_UPDATED broadcast");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_PARAMETERS_UPDATED);
        context.sendBroadcast(broadcastIntent);
    }

    /**
     * Initialize the {@link SharedPreferences} editor.
     */
    @SuppressLint("CommitPrefEdits")
    @Inject
    public StatusManager(SharedPreferences sharedPreferences) {
        Logger.d(TAG, "StatusManager created");
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
        updateCachedCurrentMode();
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

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_FL_COMPLETED, true);
        eSharedPreferences.commit();
    }

    private synchronized void clearFirstLaunchCompleted() {
        Logger.d(TAG, "{} - Clearing first launch completed", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_FL_COMPLETED);
        eSharedPreferences.commit();
    }

    /**
     * Check if the parameters have been updated.
     *
     * @return {@code true} if the parameters have been updated,
     *         {@code false} otherwise
     */
    public synchronized boolean areParametersUpdated() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED,
                false)) {
            Logger.d(TAG, "{} - Parameters are updated", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Parameters not updated yet", getCurrentModeName());
            return false;
        }
    }

    /**
     * Set the updated parameters flag to completed.
     */
    public synchronized void setParametersUpdated() {
        Logger.d(TAG, "{} - Setting parameters to updated", getCurrentModeName());

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED, true);
        eSharedPreferences.commit();

        // Broadcast the info so that the dashboard can update its view
        sendParametersUpdatedBroadcast();
    }

    public synchronized void clearParametersUpdated() {
        Logger.d(TAG, "{} - Clearing parameters updated", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED);
        eSharedPreferences.commit();
    }

    /**
     * Check if the parameters have been flushed.
     *
     * @return {@code true} if the parameters have been flushed,
     *         {@code false} otherwise
     */
    public synchronized boolean areParametersFlushed() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_FLUSHED,
                false)) {
            Logger.d(TAG, "{} - Parameters are flushed", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Parameters not flushed", getCurrentModeName());
            return false;
        }
    }

    /**
     * Set the flushed parameters flag to on.
     */
    public synchronized void setParametersFlushed() {
        Logger.d(TAG, "{} - Setting parameters to flushed", getCurrentModeName());

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_FLUSHED, true);
        eSharedPreferences.commit();
    }

    public synchronized void clearParametersFlushed() {
        Logger.d(TAG, "{} - Clearing parameters flushed", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_PARAMETERS_FLUSHED);
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
     * If a sync operation was made less than {@code syncDelay}
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
        int syncDelay = getSyncDelay();
        long threshold = sharedPreferences.getLong(getCurrentModeName() + LAST_SYNC_TIMESTAMP,
                - syncDelay) + syncDelay;
        if (threshold < SystemClock.elapsedRealtime()) {
            Logger.d(TAG, "{} - Last sync was long ago", getCurrentModeName());
            return true;
        } else {
            Logger.d(TAG, "{} - Last sync was not long ago", getCurrentModeName());
            return false;
        }
    }

    private synchronized int getSyncDelay() {
        if (getCurrentMode() == MODE_PROD) {
            Logger.v(TAG, "Using production sync delay");
            return SYNC_DELAY_PROD;
        } else {
            Logger.v(TAG, "Using test sync delay");
            return SYNC_DELAY_TEST;
        }
    }

    public synchronized long getExperimentStartTimestamp() {
        return sharedPreferences.getLong(getCurrentModeName() + EXP_START_TIMESTAMP, -1);
    }

    public synchronized void setExperimentStartTimestamp(long timestamp) {
        Logger.d(TAG, "{0} - Setting experiment start timestamp to {1}", getCurrentModeName(), timestamp);
        eSharedPreferences.putLong(getCurrentModeName() + EXP_START_TIMESTAMP, timestamp);
        eSharedPreferences.commit();
    }

    private synchronized void clearExperimentStartTimestamp() {
        Logger.d(TAG, "{} - Clearing experiment start timestamp", getCurrentModeName());
        eSharedPreferences.remove(getCurrentModeName() + EXP_START_TIMESTAMP);
        eSharedPreferences.commit();
    }

    public synchronized void setLatestSchedulerServiceSystemTimestampToNow() {
        Logger.d(TAG, "Setting last time SchedulerService ran to now (system timestamp)");
        eSharedPreferences.putLong(LATEST_SCHEDULER_SERVICE_SYSTEM_TIMESTAMP,
                Calendar.getInstance().getTimeInMillis());
        eSharedPreferences.commit();
    }

    public synchronized void checkLatestSchedulerWasAgesAgo() {
        long latest = sharedPreferences.getLong(LATEST_SCHEDULER_SERVICE_SYSTEM_TIMESTAMP, -1);
        if (latest == -1) {
            // SchedulerService never ran, which means first launch wasn't finished
            Logger.d(TAG, "SchedulerService never ran yet, no need to restart it");
        } else {
            if (Calendar.getInstance().getTimeInMillis() - latest > RESTART_SCHEDULER_SERVICE_DELAY) {
                Logger.d(TAG, "SchedulerService hasn't run since long ago -> restarting it");

                Intent schedulerIntent = new Intent(context, SchedulerService.class);
                context.startService(schedulerIntent);
            } else {
                Logger.d(TAG, "SchedulerService ran not long ago, leaving it be");
            }
        }
    }

    public synchronized void setLatestLocationPointServiceSystemTimestampToNow() {
        Logger.d(TAG, "Setting last time LocationPointService ran to now (system timestamp)");
        eSharedPreferences.putLong(LATEST_LOCATION_POINT_SERVICE_SYSTEM_TIMESTAMP,
                Calendar.getInstance().getTimeInMillis());
        eSharedPreferences.commit();
    }

    public synchronized void checkLatestLocationPointServiceWasAgesAgo() {
        long latest = sharedPreferences.getLong(LATEST_LOCATION_POINT_SERVICE_SYSTEM_TIMESTAMP, -1);
        if (latest == -1) {
            // LocationPointService never ran, which means first launch wasn't finished
            Logger.d(TAG, "LocationPointService never ran yet, no need to restart it");
        } else {
            if (Calendar.getInstance().getTimeInMillis() - latest >
                    RESTART_LOCATION_POINT_SERVICE_DELAY) {
                Logger.d(TAG, "LocationPointService hasn't run since long ago -> restarting it");

                // Possible race condition here: if the user changes the system time, and a
                // LocationPoint was collecting, but the time change is long enough to make
                // us think the LocationPointService hasn't run for a long time. In that case,
                // LocationPointService will start a new location collection, and the currently
                // collection LocationPoint will lose its callback in LocationService (replaced by
                // the new one). (The NTP callback could still fire, which is ok.) It'll then be
                // removed from DB, or uploaded (if it was complete), next time the
                // LocationPointService wants to stop collection. We'll get a warning in the logs
                // saying there were two LocationPoints collecting, that's all.
                Intent locationIntent = new Intent(context, LocationPointService.class);
                context.startService(locationIntent);
            } else {
                Logger.d(TAG, "LocationPointService ran not long ago, leaving it be");
            }
        }
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

    private synchronized void updateCachedCurrentMode() {
        int mode = sharedPreferences.getInt(EXP_CURRENT_MODE, MODE_DEFAULT);
        Logger.d(TAG, "Updating cached mode (is {})", mode);
        cachedCurrentMode = mode;
    }

    public synchronized int getCurrentMode() {
        return cachedCurrentMode;
    }

    public static synchronized int getCurrentModeStatic(Context context) {
        int mode = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext()).getInt(EXP_CURRENT_MODE, MODE_DEFAULT);
        Logger.d(TAG, "Current mode is {} (gotten statically)", mode);
        return mode;
    }

    public synchronized String getCurrentModeName() {
        String modeName;
        if (getCurrentMode() == MODE_PROD) {
            modeName = MODE_NAME_PROD;
        } else {
            modeName = MODE_NAME_TEST;
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
        updateCachedCurrentMode();
    }

    public synchronized void switchToTestMode() {
        Logger.d(TAG, "Doing full switch to test mode");

        // Clear pending uploads (before switch)
        sequencesStorageProvider.get().removeAllSequences(Sequence.TYPE_PROBE);
        locationPointsStorageProvider.get().removeUploadableLocationPoints();

        // Do the switch
        setCurrentMode(MODE_TEST);

        // Clear local flags (after switch)
        clearFirstLaunchCompleted();
        clearExperimentStartTimestamp();

        // Cancel any running location collection and pending notifications.
        // This is done after the switch to make sure no polls / location collection are
        // started between cancellation of previous ones and switch. It _can_ be done after
        // the switch, because these polls and location collections have no notion of
        // app mode.
        cancelNotifiedPollsAndCollectingLocations();

        // Clear test profile and crypto storage (after switch).
        // Clearing the profile also clears the parametersStorage.
        profileStorageProvider.get().clearProfile();
        cryptoStorageProvider.get().clearStore();
    }

    public synchronized void resetParametersKeepProfileAnswers() {
        Logger.d(TAG, "Resetting parameters and profile_id, keeping the profile answers");

        // Clear pending uploads (before clearing)
        sequencesStorageProvider.get().removeAllSequences(Sequence.TYPE_PROBE);
        locationPointsStorageProvider.get().removeUploadableLocationPoints();

        // Clear local experiment started flag
        clearExperimentStartTimestamp();

        // Clear parameters storage
        parametersStorageProvider.get().flush();

        // Cancel any running location collection and pending notifications
        cancelNotifiedPollsAndCollectingLocations();

        // Clear crypto storage to force a new handshake
        cryptoStorageProvider.get().clearStore();

        // Set the dirty flag on the profile, so that what information we have now
        // gets uploaded at next sync (which will also trigger the crypto handshake).
        profileStorageProvider.get().setIsDirtyAndCommit();
    }

    public synchronized void switchToProdMode() {
        Logger.d(TAG, "Doing full switch to production mode");

        // Clear pending uploads (before switch)
        sequencesStorageProvider.get().removeAllSequences(Sequence.TYPE_PROBE);
        locationPointsStorageProvider.get().removeUploadableLocationPoints();

        // Do the switch
        setCurrentMode(MODE_PROD);

        // Cancel any running location collection and pending notifications.
        // This is done after the switch to make sure no polls / location collection are
        // started between cancellation of previous ones and switch. It _can_ be done after
        // the switch, because these polls and location collections have no notion of
        // app mode.
        cancelNotifiedPollsAndCollectingLocations();

        // Don't clear local flags (after switch), so that experiment isn't restarted
        // And don't clear prod profile, or parameters storage, or crypto storage (after switch)
    }

    private synchronized void cancelNotifiedPollsAndCollectingLocations() {
        Logger.d(TAG, "Cancelling collecting locationPoints by calling LocationPointService");
        Intent locationPointServiceIntent = new Intent(context, LocationPointService.class);
        locationPointServiceIntent.putExtra(
                LocationPointService.STOP_LOCATION_LISTENING, true);
        locationPointServiceIntent.putExtra(
                LocationPointService.CANCEL_COLLECTING_LOCATION_POINTS, true);
        // The service will auto-reschedule itself for the next location listening
        context.startService(locationPointServiceIntent);

        Logger.d(TAG, "Cancelling notified polls");
        Intent pollServiceIntent = new Intent(context, ProbeService.class);
        pollServiceIntent.putExtra(ProbeService.CANCEL_PENDING_POLLS, true);
        context.startService(pollServiceIntent);
    }

    public synchronized Boolean isExpRunning() {
        return areParametersUpdated();
    }


    /**
     * Check if beginning questionnaires are completed
     *
     * @return {@code true} if beginning questionnaires are completed
     *         {@code false} otherwise
     */
    public synchronized boolean areBeginningQuestionnairesCompleted() {
        if (sequencesStorageProvider.get().getUploadableSequences(Sequence.TYPE_BEGIN_QUESTIONNAIRE).isEmpty())  {
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean areResultsAvailable(){
        return false;
    }

}
