package com.brainydroid.daydreaming.background;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.LocationPointsStorage;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.ui.dashboard.BEQActivity;
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
    public static String EXP_STATUS_FL_COMPLETED =
            "expStatusFlCompleted";

    /** Preference key storing the status of initial questions update */
    private static String EXP_STATUS_PARAMETERS_UPDATED =
            "expStatusQuestionsUpdated";
    /** Preference key storing the status of initial questions update */
    public static String EXP_STATUS_PARAMETERS_FLUSHED =
            "expStatusQuestionsFlushed";

    /** Preference key storing timestamp of the last sync operation */
    private static String LAST_SYNC_TIMESTAMP = "lastSyncTimestamp";

    /** Preference key storing timestamp of the last morning questionnaire */
    private static String LAST_MORNING_Q_TIMESTAMP = "lastMQNotifTimestamp";


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

    private static String CURRENT_BEG_END_QUESTIONNAIRE_TYPE = "currentBEQType";

    private static String RESULTS_DOWNLOADED = "resultsDownloaded";
    public static String NOTIFICATION_EXPIRY_EXPLAINED = "notificationExpiryExplained";
    public static String GLOSSARY_EXPLAINED = "glossaryExplained";
    public static String EQ_EDIT_ACTIVITIES_EXPLAINED = "eqEditActivitiesExplained";

    public static final String ACTION_PARAMETERS_STATUS_CHANGE = "actionParametersStatusChange";

    public static String STORAGE_VERSION = "storageVersion";

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
    // Use providers here to prevent circular dependencies
    @Inject Provider<ProfileStorage> profileStorageProvider;
    @Inject Provider<SequencesStorage> sequencesStorageProvider;
    @Inject Provider<LocationPointsStorage> locationPointsStorageProvider;
    @Inject Provider<ParametersStorage> parametersStorageProvider;
    @Inject Provider<CryptoStorage> cryptoStorageProvider;
    @Inject NotificationManager notificationManager;

    Context context;
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
    public StatusManager(Context context, SharedPreferences sharedPreferences) {
        Logger.d(TAG, "StatusManager created");
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
        checkStorageVersion();
        updateCachedCurrentMode();
    }

    public String getDebugInfoString() {
        return "app version: " + profileStorageProvider.get().getAppVersionName()
                + "\nparameters version: " + profileStorageProvider.get().getParametersVersion();
    }

    public synchronized boolean is(String flagName) {
        if (sharedPreferences.getBoolean(getCurrentModeName() + flagName, false)) {
            Logger.d(TAG, "{0} - {1} flag is set", getCurrentModeName(), flagName);
            return true;
        } else {
            Logger.d(TAG, "{0} - {1} flag is not set", getCurrentModeName(), flagName);
            return false;
        }
    }

    public synchronized void set(String flagName) {
        set(flagName, true);
    }

    public synchronized void clear(String flagName) {
        Logger.d(TAG, "{0} - Clearing {1} flag", getCurrentModeName(), flagName);
        eSharedPreferences.remove(getCurrentModeName() + flagName);
        eSharedPreferences.commit();
    }

    public synchronized void set(String flagName, boolean value) {
        Logger.d(TAG, "{0} - Setting {1} flag to {2}", getCurrentModeName(), flagName, value);
        eSharedPreferences.putBoolean(getCurrentModeName() + flagName, value);
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
    public synchronized void setParametersUpdated(boolean updated) {
        Logger.d(TAG, "{0} - Setting parameters updated to {1}", getCurrentModeName(), updated);

        eSharedPreferences.putBoolean(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED, updated);
        eSharedPreferences.commit();

        // (Re)create BEQ notification if necessary
        updateBEQNotification();

        // Broadcast the info so that the dashboard can update its view
        sendParametersStatusChangeBroadcast();
    }

    public synchronized void clearParametersUpdated() {
        Logger.d(TAG, "{} - Clearing parameters updated", getCurrentModeName());

        eSharedPreferences.remove(getCurrentModeName() + EXP_STATUS_PARAMETERS_UPDATED);
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
        long now = Calendar.getInstance().getTimeInMillis();
        Logger.d(TAG, "{} - Setting last sync timestamp to now", getCurrentModeName());
        eSharedPreferences.putLong(getCurrentModeName() + LAST_SYNC_TIMESTAMP, now);
        eSharedPreferences.commit();
    }

    /**
     * Set the timestamp of the last MQ notification to now.
     */
    public synchronized void setLastMQNotifToNow() {
        long now = Calendar.getInstance().getTimeInMillis();
        Logger.d(TAG, "{} - Setting last MQ notif timestamp to now", getCurrentModeName());
        eSharedPreferences.putLong(getCurrentModeName() + LAST_MORNING_Q_TIMESTAMP, now);
        eSharedPreferences.commit();
    }

    /**
     * Check if a morning questionnaire notification was long ago.
     */
    public synchronized boolean isLastMQNotifLongAgo() {
        long delay = 18 * 3600 * 1000;  // 24h - 3h - 3h = 18h (in milliseconds)
        long threshold = sharedPreferences.getLong(getCurrentModeName() + LAST_MORNING_Q_TIMESTAMP, - delay) + delay;
        if (threshold < Calendar.getInstance().getTimeInMillis()) {
            Logger.v(TAG, "{} - Last MQ notif was yesterday", getCurrentModeName());
            return true;
        } else {
            Logger.v(TAG, "{} - Last MQ notif was recent, do not notify", getCurrentModeName());
            return false;
        }
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
        if (threshold < Calendar.getInstance().getTimeInMillis()) {
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
        clear(EXP_STATUS_FL_COMPLETED);
        clearExperimentStartTimestamp();
        clear(ARE_RESULTS_NOTIFIED);
        clear(ARE_RESULTS_NOTIFIED_DASHBOARD);
        clear(NOTIFICATION_EXPIRY_EXPLAINED);
        clear(GLOSSARY_EXPLAINED);
        clear(EQ_EDIT_ACTIVITIES_EXPLAINED);
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

        // Clear result and explanation flags
        clear(ARE_RESULTS_NOTIFIED);
        clear(ARE_RESULTS_NOTIFIED_DASHBOARD);
        clear(NOTIFICATION_EXPIRY_EXPLAINED);
        clear(GLOSSARY_EXPLAINED);
        clear(EQ_EDIT_ACTIVITIES_EXPLAINED);
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

        //noinspection SimplifiableIfStatement
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
                    updateBEQNotification();
                }
            }
        }
        return getCurrentBEQType();
    }

    public synchronized void updateBEQNotification() {
        // if exp is running
        if (isExpRunning()) {
            if (areBEQCompleted()) {
                Logger.d(TAG, "BEQs completed");
                notificationManager.cancel(Sequence.TYPE_BEGIN_END_QUESTIONNAIRE, 0);
            } else {
                Logger.d(TAG, "BEQs not completed, refreshing/creating notification");

                // Get the proper text
                String type = getCurrentBEQType();
                boolean pendingAfterDeadline = areBEQPendingAfterDeadline();
                String ticker, title, text;
                if (type.equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE)) {
                    if (pendingAfterDeadline) {
                        ticker = context.getString(R.string.beginNotification_ticker_after_deadline);
                        title = context.getString(R.string.beginNotification_title_after_deadline);
                        text = context.getString(R.string.beginNotification_text_after_deadline);
                    } else {
                        ticker = context.getString(R.string.beginNotification_ticker_before_deadline);
                        title = context.getString(R.string.beginNotification_title_before_deadline);
                        text = context.getString(R.string.beginNotification_text_before_deadline);
                    }
                } else {
                    if (pendingAfterDeadline) {
                        ticker = context.getString(R.string.endNotification_ticker_after_deadline);
                        title = context.getString(R.string.endNotification_title_after_deadline);
                        text = context.getString(R.string.endNotification_text_after_deadline);
                    } else {
                        ticker = context.getString(R.string.endNotification_ticker_before_deadline);
                        title = context.getString(R.string.endNotification_title_before_deadline);
                        text = context.getString(R.string.endNotification_text_before_deadline);
                    }
                }

                Intent intent = new Intent(context, BEQActivity.class);
                // No need for a request code here, BEQActivity is only ever started from
                // a PendingIntent from here.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                int flags = 0;
                flags |= Notification.FLAG_NO_CLEAR;
                // Create our notification
                // if persistent: cant be dismissed and do not disappear on click
                // if not persistent: can be dismissed and self destroy on click
                Notification notification = new NotificationCompat.Builder(context)
                        .setTicker(ticker)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_stat_notify_small_daydreaming)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true)
                        .setDefaults(flags)
                        .build();

                // Only one begin/end notification should ever exist. id = 0
                notificationManager.cancel(Sequence.TYPE_BEGIN_END_QUESTIONNAIRE, 0);
                notificationManager.notify(Sequence.TYPE_BEGIN_END_QUESTIONNAIRE, 0, notification);
            }
        }
    }

    public synchronized boolean areBEQPendingAfterDeadline() {
        String type = getCurrentBEQType();
        if (areBEQCompleted(type)) {
            Logger.d(TAG, "All BEQ of type {} are answered", type);
            // questionnaires already answered
            return false;
        } else {

            int daysElapsed = (int) ((getLatestNtpTime() - getExperimentStartTimestamp()) /
                    (24 * 60 * 60 * 1000));

            if (type.equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE)) {

                if (daysElapsed > DELAY_TO_ANSWER_BEGQ) {
                    Logger.d(TAG, "{} days elapsed since experiment started and begin questionnaires still unanswered",
                            Integer.toString(daysElapsed));
                    return true;
                }
                return false;

            } else if (type.equals(Sequence.TYPE_END_QUESTIONNAIRE)) {

                if (daysElapsed > parametersStorageProvider.get().getExpDuration()) {
                    Logger.d(TAG, "Experiment time is over (now {} days) but end questionnaires still unanswered",
                            Integer.toString(daysElapsed));
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public synchronized void launchNotifyingServices() {
        if (is(EXP_STATUS_FL_COMPLETED)) {
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
        if (is(EXP_STATUS_FL_COMPLETED)) {
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

    public synchronized void setAppStorageVersion() {
        Logger.d(TAG, "Setting Storage version");

        try {
            int versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
            eSharedPreferences.putInt(STORAGE_VERSION, versionCode);
            eSharedPreferences.commit();
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "Package not found when retrieving app versionCode");
            throw new RuntimeException(e);
        }
    }

    public synchronized int getAppStorageVersion() {
        Logger.d(TAG, "Get Storage version from sharedPreferences");
        return sharedPreferences.getInt(STORAGE_VERSION, -1);
    }

    public int getAppVersionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "Package not found when retrieving app versionCode");
            throw new RuntimeException(e);
        }
    }

    public synchronized void checkStorageVersion() {
        int savedStorageVersion = getAppStorageVersion();
        int ongoingStorageVersion = getAppVersionCode();

        if (savedStorageVersion == ongoingStorageVersion) {
            Logger.i(TAG, "Stored and ongoing storage version are the same");
        } else {
            Logger.i(TAG, "Updating storage version {0} to {1}", savedStorageVersion,
                    ongoingStorageVersion);

            // If we change the structure of the storage, here is the place
            // to upgrade from a previous version to the ongoing version
            // (using another class for all the logic).

            setAppStorageVersion();
        }
    }

}
