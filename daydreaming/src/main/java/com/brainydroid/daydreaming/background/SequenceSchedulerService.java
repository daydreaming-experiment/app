package com.brainydroid.daydreaming.background;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.dashboard.ResultsActivity;
import com.google.inject.Inject;

import java.util.Calendar;
import java.util.Random;

import roboguice.service.RoboService;

/**
 * Schedule a {@link com.brainydroid.daydreaming.sequence.Sequence} to be created and
 * notified later on. The delay before creation-notification of the {@link
 * com.brainydroid.daydreaming.sequence.Sequence} is both well randomized (a Poisson
 * process) and respectful of the user's notification settings.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see com.brainydroid.daydreaming.background.SyncService
 * @see com.brainydroid.daydreaming.background.DailySequenceService
 */
public abstract class SequenceSchedulerService extends RoboService {

    private static String TAG = "SequenceSchedulerService";

    /** Scheduling delay when debugging is activated */
    public static long DEBUG_DELAY = 5 * 1000; // 5 seconds

    // Handy object that will be holding the 'now' time
    protected Calendar now;
    protected long nowUpTime;

    // Useful data about the user's settings
    protected int startAllowedHour;
    protected int startAllowedMinute;
    protected int endAllowedHour;
    protected int endAllowedMinute;
    protected int allowedSpan;
    protected int forbiddenSpan;

    @Inject SharedPreferences sharedPreferences;
    @Inject StatusManager statusManager;
    @Inject ParametersStorage parametersStorage;
    @Inject Random random;
    @Inject AlarmManager alarmManager;
    @Inject NotificationManager notificationManager;

    protected boolean debugging;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "Started (super from sub-class)");

        super.onStartCommand(intent, flags, startId);

        // Record last time we ran
        // FIXME: do this by type
        statusManager.setLatestSchedulerServiceSystemTimestampToNow();
        // Check LocationPointService hasn't died
        statusManager.checkLatestLocationPointServiceWasAgesAgo();
        // Notify results if they're available
        notifyResultsIfAvailable();
        // Check if we are getting close to the end to enable the final Begin/End questionnaires
        statusManager.updateBEQType();

        // Synchronise answers and get parameters if we don't have them. If parameters
        // happen to be updated, all the *SchedulerService will be run again.
        startSyncService();

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Don't allow binding
        return null;
    }

    protected synchronized void notifyResultsIfAvailable() {
        if (statusManager.areResultsAvailable() && !statusManager.areResultsNotified()) {
            Intent intent = new Intent(this, ResultsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getString(R.string.results_notification_ticker))
                    .setContentTitle(getString(R.string.results_notification_title))
                    .setContentText(getString(R.string.results_notification_content))
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_stat_notify_small_daydreaming)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE
                            | Notification.DEFAULT_SOUND)
                    .build();

            notificationManager.notify(TAG, -1, notification);

            // Remember we did all this
            statusManager.setResultsNotified();
        }
    }

    protected synchronized void scheduleSequence() {
        Logger.d(TAG, "Scheduling new sequence of type {}", getSequenceType());

        // Generate the time at which the sequence will appear
        long scheduledTime = generateTime();

        // Create and schedule the PendingIntent for DailySequenceService
        Intent intent = new Intent(this, DailySequenceService.class);
        intent.putExtra(DailySequenceService.SEQUENCE_TYPE, getSequenceType());
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }

    protected abstract long generateTime();

    protected abstract String getSequenceType();

    protected synchronized void fixNowAndGetAllowedWindow() {
        Logger.d(TAG, "Fixing now and obtaining allowed time window");

        now = Calendar.getInstance();
        nowUpTime = SystemClock.elapsedRealtime();

        // Get the user's allowed time window
        String startAllowedString = sharedPreferences.getString(
                "time_window_lb_key",
                getString(R.string.settings_time_window_lb_default));
        String endAllowedString = sharedPreferences.getString(
                "time_window_ub_key",
                getString(R.string.settings_time_window_ub_default));

        startAllowedHour = Util.getHour(startAllowedString);
        startAllowedMinute = Util.getMinute(startAllowedString);
        endAllowedHour = Util.getHour(endAllowedString);
        endAllowedMinute = Util.getMinute(endAllowedString);

        Logger.d(TAG, "Allowed start: {0}:{1}",
                startAllowedHour, startAllowedMinute);
        Logger.d(TAG, "Allowed end: {0}:{1}",
                endAllowedHour, endAllowedMinute);

        // Convert those to a usable format
        Calendar start = (Calendar)now.clone();
        start.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        start.set(Calendar.MINUTE, startAllowedMinute);

        Calendar end = (Calendar)now.clone();
        end.set(Calendar.HOUR_OF_DAY, endAllowedHour);
        end.set(Calendar.MINUTE, endAllowedMinute);
        if (endAllowedHour * 60 + endAllowedMinute <
                startAllowedHour * 60 + startAllowedMinute) {
            // The time window goes through midnight. Account for this in
            // our end Calendar object.
            end.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Compute the span of our allowed and forbidden time windows
        allowedSpan = (int)end.getTimeInMillis() -
                (int)start.getTimeInMillis();
        forbiddenSpan = 24 * 60 * 60 * 1000 - allowedSpan;
    }


    protected void logDelay(long delay) {
        long hours = delay / (60 * 60 * 1000);
        delay %= 60 * 60 * 1000;
        long minutes = delay / (60 * 1000);
        delay %= 60 * 1000;
        long seconds = delay / 1000;
        Logger.i(TAG, "Sequence of type {3} scheduled in {0} hours, {1} minutes, and {2} seconds",
                hours, minutes, seconds, getSequenceType());
    }


    /**
     * Start {@link com.brainydroid.daydreaming.background.SyncService} to synchronize answers.
     */
    protected synchronized void startSyncService() {
        Logger.d(TAG, "Starting SyncService");
        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

}
