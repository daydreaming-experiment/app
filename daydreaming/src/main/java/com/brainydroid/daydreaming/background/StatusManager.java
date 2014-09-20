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

import java.util.ArrayList;
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

    private static String LATEST_DAILY_SERVICE_SYSTEM_TIMESTAMP =
            "latestSchedulerServiceSystemTimestamp";
    private static String LATEST_LOCATION_POINT_SERVICE_SYSTEM_TIMESTAMP =
            "latestLocationPointServiceSystemTimestamp";

    /** Preference key storing the current mode */
    private static String EXP_CURRENT_MODE = "expCurrentMode";

    public static String ARE_RESULTS_NOTIFIED_DASHBOARD = "areResultsNotifiedDashboard";
    public static String ARE_RESULTS_NOTIFIED = "areResultsNotified";

    public static String CURRENT_BEG_END_QUESTIONNAIRE_TYPE = "currentBEQType";

    public static String RESULTS_DOWNLOADED = "resultsDownloaded";
    public static String NOTIFICATION_EXPIRY_EXPLAINED = "notificationExpiryExplained";

    public static final String ACTION_PARAMETERS_STATUS_CHANGE = "actionParametersStatusChange";

    public static int MODE_PROD = 0;
    public static int MODE_TEST = 1;
    public static int MODE_DEFAULT = MODE_PROD;
    public static String MODE_NAME_TEST = "test";
    public static String MODE_NAME_PROD = "production";

    /** Delay after which ProbeSchedulerService must be restarted if it hasn't run. 2 days. */
    public static long RESTART_SCHEDULER_SERVICE_DELAY = 2 * 24 * 60 * 60 * 1000;

    /** Delay after which LocationPointService must be restarted if it hasn't run. 1 hour. */
    public static long RESTART_LOCATION_POINT_SERVICE_DELAY = 1 * 60 * 60 * 1000;

    /** Delay in days after which probes should not be scheduled if begin questionnaires are unanswered*/
    public static int DELAY_TO_ANSWER_BEGQ = 3;

    private int cachedCurrentMode = MODE_DEFAULT;
    private boolean isDashboardRunning = false;
    private long isDashboardRunningTimestamp = -1;
    private boolean isParametersSyncRunning = false;
    private boolean isRegistrationRunning = false;
    private boolean isSequencesSyncRunning = false;
    private boolean isLocationPointsSyncRunning = false;
    private boolean isProfileSyncRunning = false;
    private long isSyncRunningTimestamp = -1;

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

    private void sendParametersStatusChangeBroadcast() {
        Logger.v(TAG, "Sending ACTION_PARAMETERS_STATUS_CHANGE broadcast");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_PARAMETERS_STATUS_CHANGE);
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

    public String getDebugInfoString() {
        return "app version: " + profileStorageProvider.get().getAppVersionName()
                + "\nparameters version: " + profileStorageProvider.get().getParametersVersion();
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

    public synchronized void setResultsNotifiedDashboard() {
        Logger.d(TAG, "{} - Setting resultsNotifiedDashboard to true", getCurrentModeName(), true);

        eSharedPreferences.putBoolean(getCurrentModeName() + ARE_RESULTS_NOTIFIED_DASHBOARD, true);
        eSharedPreferences.commit();
    }

    public synchronized boolean areResultsNotifiedDashboard() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + ARE_RESULTS_NOTIFIED_DASHBOARD,
                false)) {
            Logger.v(TAG, "{} - Results not yet notified to dashboard", getCurrentModeName());
            return true;
        } else {
            Logger.v(TAG, "{} - Results already notified to dashboard", getCurrentModeName());
            return false;
        }
    }

    public synchronized void clearResultsNotifiedDashboard() {
        Logger.d(TAG, "{} - Clearing resultsNotifiedDashboard", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + ARE_RESULTS_NOTIFIED_DASHBOARD);
        eSharedPreferences.commit();
    }

    public synchronized void setResultsNotified() {
        Logger.d(TAG, "{} - Setting resultsNotified to true", getCurrentModeName(), true);

        eSharedPreferences.putBoolean(getCurrentModeName() + ARE_RESULTS_NOTIFIED, true);
        eSharedPreferences.commit();
    }

    public synchronized boolean areResultsNotified() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + ARE_RESULTS_NOTIFIED,
                false)) {
            Logger.v(TAG, "{} - Results not yet notified with notification", getCurrentModeName());
            return true;
        } else {
            Logger.v(TAG, "{} - Results already notified with notification", getCurrentModeName());
            return false;
        }
    }

    public synchronized void clearResultsNotified() {
        Logger.d(TAG, "{} - Clearing resultsNotified", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + ARE_RESULTS_NOTIFIED);
        eSharedPreferences.commit();
    }

    public synchronized void setResultsDownloadedToNow() {
        Logger.d(TAG, "{} - Setting resultsDownloaded to true", getCurrentModeName(), true);

        eSharedPreferences.putLong(getCurrentModeName() + RESULTS_DOWNLOADED,
                Calendar.getInstance().getTimeInMillis());
        eSharedPreferences.commit();
    }

    public synchronized long getResultsDownloadTimestamp() {
        long timestamp = sharedPreferences.getLong(getCurrentModeName() + RESULTS_DOWNLOADED, -1);
        if (timestamp == -1) {
            Logger.v(TAG, "{} - Results not yet downloaded", getCurrentModeName());
            return -1;
        } else {
            Logger.v(TAG, "{} - Results already downloaded", getCurrentModeName());
            return timestamp;
        }
    }

    public synchronized void clearResultsDownloaded() {
        Logger.d(TAG, "{} - Clearing resultsDownloaded", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + RESULTS_DOWNLOADED);
        eSharedPreferences.commit();
    }
    public synchronized void setNotificationExpiryExplained() {
        Logger.d(TAG, "{} - Setting notificationExpiryExplained to true", getCurrentModeName(), true);

        eSharedPreferences.putBoolean(getCurrentModeName() + NOTIFICATION_EXPIRY_EXPLAINED, true);
        eSharedPreferences.commit();
    }

    public synchronized boolean isNotificationExpiryExplained() {
        if (sharedPreferences.getBoolean(getCurrentModeName() + NOTIFICATION_EXPIRY_EXPLAINED,
                false)) {
            Logger.v(TAG, "{} - Notification expiry not yet explained", getCurrentModeName());
            return true;
        } else {
            Logger.v(TAG, "{} - Notification expiry not yet explained", getCurrentModeName());
            return false;
        }
    }

   public synchronized void clearNotificationExpiryExplained() {
       Logger.d(TAG, "{} - Clearing notificationExpiryExplained", getCurrentModeName());

       eSharedPreferences.remove(getCurrentModeName() + NOTIFICATION_EXPIRY_EXPLAINED);
       eSharedPreferences.commit();
   }

    public synchronized void setDashboardRunning(boolean running) {
        Logger.v(TAG, "Setting isDashboardRunning to {}", running);
        isDashboardRunning = true;
        isDashboardRunningTimestamp = Calendar.getInstance().getTimeInMillis();
    }

    public synchronized boolean isDashboardRunning() {
        long now = Calendar.getInstance().getTimeInMillis();
        // Dashboard is running, and we have that information from less than 1 minute ago
        return isDashboardRunning && (now - isDashboardRunningTimestamp < 1 * 60 * 1000);
    }

    /**
     * Set the updated parameters flag to completed.
     */
    public synchronized void setParametersUpdated(boolean updated) {
        Logger.d(TAG, "{0} - Setting parameters updated to {1}", getCurrentModeName(), updated);

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED, updated);
        eSharedPreferences.commit();

        // Broadcast the info so that the dashboard can update its view
        sendParametersStatusChangeBroadcast();
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

    public synchronized void setLatestDailyServiceSystemTimestampToNow() {
        Logger.d(TAG, "Setting last time DailySequenceService ran to now (system timestamp)");
        eSharedPreferences.putLong(LATEST_DAILY_SERVICE_SYSTEM_TIMESTAMP,
                Calendar.getInstance().getTimeInMillis());
        eSharedPreferences.commit();
    }

    public synchronized void checkLatestDailyWasAgesAgo() {
        long latest = sharedPreferences.getLong(LATEST_DAILY_SERVICE_SYSTEM_TIMESTAMP, -1);
        if (latest == -1) {
            // ProbeSchedulerService never ran, which means first launch wasn't finished
            Logger.d(TAG, "ProbeSchedulerService never ran yet, no need to restart it");
        } else {
            if (Calendar.getInstance().getTimeInMillis() - latest > RESTART_SCHEDULER_SERVICE_DELAY) {
                Logger.d(TAG, "ProbeSchedulerService hasn't run since long ago -> restarting it");

                Intent schedulerIntent = new Intent(context, ProbeSchedulerService.class);
                context.startService(schedulerIntent);
            } else {
                Logger.d(TAG, "ProbeSchedulerService ran not long ago, leaving it be");
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
        clearResultsNotified();
        clearResultsNotifiedDashboard();
        clearNotificationExpiryExplained();
        clearResultsDownloaded();

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

        // Clear result flags
        clearResultsNotified();
        clearResultsNotifiedDashboard();
        clearNotificationExpiryExplained();
        clearResultsDownloaded();

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
        Intent pollServiceIntent = new Intent(context, DailySequenceService.class);
        pollServiceIntent.putExtra(DailySequenceService.SEQUENCE_TYPE, Sequence.TYPE_PROBE);
        pollServiceIntent.putExtra(DailySequenceService.CANCEL_PENDING_SEQUENCES, true);
        context.startService(pollServiceIntent);
    }

    public synchronized Boolean isExpRunning() {
        return areParametersUpdated();
    }

    private synchronized boolean areBEQCompleted(String type) {
        if (!areParametersUpdated()) {
            return false;
        }
        ArrayList<Sequence> allLoadedQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(type);
        ArrayList<Sequence> completedQuestionnaires = sequencesStorageProvider.get()
                .getCompletedSequences(type);

        int nCompleted = completedQuestionnaires != null ? completedQuestionnaires.size() : -1;
        int nLoaded = allLoadedQuestionnaires != null ? allLoadedQuestionnaires.size() : -1;

        Logger.d(TAG, "Checking BEQ status");
        Logger.d(TAG, "Loaded : {0} - Completed {1}", Integer.toString(nLoaded), Integer.toString(nCompleted));

        return nLoaded == nCompleted;
    }


    public synchronized boolean areBEQCompleted() {
        if (!areParametersUpdated()) {
            return false;
        }
        String type = getCurrentBEQType();
        return areBEQCompleted(type);
    }

    public synchronized boolean areResultsAvailable() {
        if (!isExpRunning()) return false;

        int daysElapsed = (int)((getLatestNtpTime() - getExperimentStartTimestamp()) /
                (24 * 60 * 60 * 1000));
        int daysToGo = parametersStorageProvider.get().getExpDuration() - daysElapsed;

        if (areBEQCompleted(Sequence.TYPE_END_QUESTIONNAIRE)) {
            return daysToGo <= 0 || getCurrentMode() == MODE_TEST;
        } else {
            return false;
        }
    }

    public void setParametersSyncRunning(boolean running) {
        Logger.v(TAG, "Setting isParametersSyncRunning to {}", running);
        isParametersSyncRunning = running;
        isSyncRunningTimestamp = Calendar.getInstance().getTimeInMillis();
        clearSyncRunningIfAllDone();
    }

    public boolean isParametersSyncRunning() {
        return isParametersSyncRunning;
    }

    public void setRegistrationRunning(boolean running) {
        Logger.v(TAG, "Setting isRegistrationRunning to {}", running);
        isRegistrationRunning = running;
        isSyncRunningTimestamp = Calendar.getInstance().getTimeInMillis();
        clearSyncRunningIfAllDone();
    }

    public void setSequencesSyncRunning(boolean running) {
        Logger.v(TAG, "Setting isSequencesSyncRunning to {}", running);
        isSequencesSyncRunning = running;
        isSyncRunningTimestamp = Calendar.getInstance().getTimeInMillis();
        clearSyncRunningIfAllDone();
    }

    public void setLocationPointsSyncRunning(boolean running) {
        Logger.v(TAG, "Setting isLocationPointsSyncRunning to {}", running);
        isLocationPointsSyncRunning = running;
        isSyncRunningTimestamp = Calendar.getInstance().getTimeInMillis();
        clearSyncRunningIfAllDone();
    }

    public void setProfileSyncRunning(boolean running) {
        Logger.v(TAG, "Setting isProfileSyncRunning to {}", running);
        isProfileSyncRunning = running;
        isSyncRunningTimestamp = Calendar.getInstance().getTimeInMillis();
        clearSyncRunningIfAllDone();
    }

    private void clearSyncRunningIfAllDone() {
        Logger.v(TAG, "Clearing sync running flags if all done");
        if (!isParametersSyncRunning && !isRegistrationRunning &&
                !isSequencesSyncRunning && !isLocationPointsSyncRunning && !isProfileSyncRunning) {
            isParametersSyncRunning = false;
            isRegistrationRunning = false;
            isSequencesSyncRunning = false;
            isLocationPointsSyncRunning = false;
            isProfileSyncRunning = false;
            isSyncRunningTimestamp = -1;
        }
    }

    public boolean isSyncRunning() {
        long now = Calendar.getInstance().getTimeInMillis();
        // Sync is running and we know this from less than a minute ago
        return (isParametersSyncRunning || isRegistrationRunning ||
                isSequencesSyncRunning || isLocationPointsSyncRunning || isProfileSyncRunning)
                && (now - isSyncRunningTimestamp) < 60 * 1000;
    }

    /**
     * Setting current Begin/End questionnaire type to type
     */
    public synchronized void setCurrentBEQType(String type) {
        Logger.d(TAG, "{} - Setting currentBEQType to {}", getCurrentModeName(), type);
        eSharedPreferences.putString(getCurrentModeName() + CURRENT_BEG_END_QUESTIONNAIRE_TYPE, type);
        eSharedPreferences.commit();
    }

    public synchronized String getCurrentBEQType() {
        String currentBEQType = sharedPreferences.getString(
                getCurrentModeName() + CURRENT_BEG_END_QUESTIONNAIRE_TYPE, null);
        if (currentBEQType == null) {
            Logger.d(TAG, "currentBEGType not set, setting it to BeginQuestionnaire");
            setCurrentBEQType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        }
        Logger.d(TAG, "{0} - currentBEQType is {1}", getCurrentModeName(),
                currentBEQType);
        return currentBEQType;
    }

    public synchronized String updateBEQType() {
        // if exp is running
        if (isExpRunning()) {
            String type = getCurrentBEQType();
            int daysElapsed = (int)((getLatestNtpTime() - getExperimentStartTimestamp()) /
                    (24 * 60 * 60 * 1000));
            int daysToGo = parametersStorageProvider.get().getExpDuration() - daysElapsed;
            // if begin Questionnaires are completed
            if (areBEQCompleted(type)) {
                // if we get close to the end
                if (daysToGo < 3) {
                    setCurrentBEQType(Sequence.TYPE_END_QUESTIONNAIRE);
                }
            }
        }
        return getCurrentBEQType();
    }

    public synchronized boolean wereBEQAnsweredOnTime() {
        String type = getCurrentBEQType();
        if ( areBEQCompleted(type) ) {
            Logger.d(TAG, "all BEQ of type {} are answered", type);
            // questionnaires already answered
            return true;
        } else {

            int daysElapsed = (int) ((getLatestNtpTime() - getExperimentStartTimestamp()) /
                    (24 * 60 * 60 * 1000));

            if (type.equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE)) {

                if (daysElapsed > DELAY_TO_ANSWER_BEGQ) {
                    Logger.d(TAG, "{} days elapsed since experiment started and begin questionnaires still unanswered",
                            Integer.toString(daysElapsed));
                    return false;
                }
                return true;

            } else if (type.equals(Sequence.TYPE_END_QUESTIONNAIRE)) {

                if (daysElapsed > parametersStorageProvider.get().getExpDuration()) {
                    Logger.d(TAG, "Experiment time is over (now {} days) but end questionnaires still unanswered",
                            Integer.toString(daysElapsed));
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public synchronized void launchNotifyingServices() {
        if (isFirstLaunchCompleted()) {
            Logger.d(TAG, "First launch is completed, launching scheduler services");

            // Start scheduling polls
            Logger.d(TAG, "Starting ProbeSchedulerService");
            Intent schedulerIntent = new Intent(context, ProbeSchedulerService.class);
            context.startService(schedulerIntent);

            // Start notifying Morning questionnaires
            Logger.d(TAG, "Starting MQSchedulerService");
            Intent MQIntent = new Intent(context, MQSchedulerService.class);
            context.startService(MQIntent);

            // Start notifying Evening questionnaires
            Logger.d(TAG, "Starting EQSchedulerService");
            Intent EQIntent = new Intent(context, EQSchedulerService.class);
            context.startService(EQIntent);
        } else {
            Logger.v(TAG, "First launch not completed -> not re-launching scheduler services");
        }
    }

    public synchronized void launchAllServices() {
        // If first launch hasn't been completed, the user doesn't want
        // anything yet
        if (isFirstLaunchCompleted()) {
            launchNotifyingServices();

            // Start getting location updates
            Logger.d(TAG, "First launch completed, starting LocationPointService");
            Intent locationPointServiceIntent = new Intent(context,
                    LocationPointService.class);
            context.startService(locationPointServiceIntent);
        } else {
            Logger.v(TAG, "First launch not completed -> exiting");
        }
    }


}
