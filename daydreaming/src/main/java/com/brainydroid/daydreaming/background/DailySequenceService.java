package com.brainydroid.daydreaming.background;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.ConsistencyException;
import com.brainydroid.daydreaming.db.Json;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
import com.brainydroid.daydreaming.ui.dashboard.SettingsActivity;
import com.brainydroid.daydreaming.ui.sequences.PageActivity;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Calendar;

import roboguice.service.RoboService;

/**
 * Create and populate a {@link Sequence}, then notify it to the user.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Sequence
 * @see ProbeSchedulerService
 * @see SyncService
 */
public class DailySequenceService extends RoboService {

    private static String TAG = "DailySequenceService";

    public static String CANCEL_PENDING_SEQUENCES = "cancelPendingSequences";
    public static String SEQUENCE_TYPE = "sequenceType";
    public static String EXPIRE_PROBE = "expireProbe";
    public static String DISMISS_PROBE = "dismissProbe";
    public static String PROBE_ID = "probeId";

    @Inject NotificationManager notificationManager;
    @Inject SequencesStorage sequencesStorage;
    @Inject SequenceBuilder sequenceBuilder;
    @Inject SharedPreferences sharedPreferences;
    @Inject SntpClient sntpClient;
    @Inject StatusManager statusManager;
    @Inject ErrorHandler errorHandler;
    @Inject Json json;
    @Inject ParametersStorage parametersStorage;
    @Inject AlarmManager alarmManager;

    String sequenceType;

    @Override
    public synchronized void onDestroy() {
        Logger.v(TAG, "Destroying");
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "DailySequenceService started");

        super.onStartCommand(intent, flags, startId);

        // Record last time we ran
        statusManager.setLatestDailyServiceSystemTimestampToNow();
        // Check LocationPointService hasn't died
        statusManager.checkLatestLocationPointServiceWasAgesAgo();

        sequenceType = intent.getStringExtra(DailySequenceService.SEQUENCE_TYPE);
        if (sequenceType == null) {
            Log.d(TAG, "Sequence type not found in intent, found null");
            throw new NullPointerException();
        }

        if (intent.getBooleanExtra(CANCEL_PENDING_SEQUENCES, false)) {
            Logger.v(TAG, "Started to cancel pending sequences of type {}", sequenceType);
            cancelPendingSequences();
            flushRecentlyMarkedProbes();
            // No need to reschedule:
            // This is run from statusManager, directly to cancel pending stuff,
            // and doesn't interfere with scheduling. If other classes do interfere
            // (e.g. clearing parameters), they relaunch scheduler services.
        } else if (intent.getBooleanExtra(EXPIRE_PROBE, false)) {
            Logger.v(TAG, "Started to expire pending probes");
            int probeId = intent.getIntExtra(PROBE_ID, -1);
            if (probeId == -1) {
                // We have a problem
                errorHandler.logError("ProbeService started to expire a probe, " +
                        "but not probe id given", new ConsistencyException());
                stopSelf();
                return START_REDELIVER_INTENT;
            }

            if (statusManager.is(StatusManager.NOTIFICATION_EXPIRY_EXPLAINED)) {
                expireProbe(probeId);
            }
        } else if (intent.getBooleanExtra(DISMISS_PROBE, false)) {
            Logger.v(TAG, "Started to dismiss probe");
            int probeId = intent.getIntExtra(PROBE_ID, -1);
            if (probeId == -1) {
                // We have a problem
                errorHandler.logError("ProbeService started to dismiss a probe, " +
                        "but not probe id given", new ConsistencyException());
                stopSelf();
                return START_REDELIVER_INTENT;
            }

            dismissProbe(probeId);
        } else {
            Logger.v(TAG, "Started to create and notify a sequence of type {}", sequenceType);

            if (statusManager.areParametersUpdated()) {
                if (sequenceType.equals(Sequence.TYPE_PROBE)) {
                    // If Dashboard is running, reschedule (so as not to flush recently* during dashboard)
                    if (statusManager.isDashboardRunning()) {
                        Logger.v(TAG, "Dashboard is running, rescheduling");
                        startSchedulerService();
                        stopSelf();
                        return START_REDELIVER_INTENT;
                    }

                    // Flush recently* marked probes
                    flushRecentlyMarkedProbes();
                }

                // Populate and notify the sequence
                Sequence sequence = populateSequence();
                notifySequence(sequence);

                if (sequenceType.equals(Sequence.TYPE_PROBE)) {
                    // Schedule expiry
                    scheduleProbeExpiry(sequence);
                }
            }

            // Always reschedule
            startSchedulerService();
        }

        stopSelf();
        return START_REDELIVER_INTENT;
    }

    @Override
    public synchronized IBinder onBind(Intent intent) {
        // Don't allow binding
        return null;
    }

    /**
     * Create the {@link PageActivity} {@link Intent}.
     *
     * @return An {@link Intent} to launch our {@link Sequence}
     */
    private synchronized Intent createSequenceIntent(Sequence sequence) {
        Logger.d(TAG, "Creating sequence Intent - type: {}", sequenceType);

        Intent intent = new Intent(this, PageActivity.class);

        // Set the id of the sequence to start
        intent.putExtra(PageActivity.EXTRA_SEQUENCE_ID, sequence.getId());

        // Create a new task. The rest is defined in the App manifest.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private synchronized void dismissProbe(int probeId) {
        Logger.v(TAG, "Dismissing probe");
        Sequence probe = sequencesStorage.get(probeId);
        probe.setStatus(Sequence.STATUS_RECENTLY_DISMISSED);
        // Notification was already removed by the user.
    }

    private synchronized void expireProbe(int probeId) {
        Logger.v(TAG, "Expiring probe");
        Sequence probe = sequencesStorage.get(probeId);
        if (probe == null) {
            Logger.v(TAG, "Probe not in DB any more, probably answered+synced+flushed. " +
                    "No need to expire it.");
            return;
        }

        String status = probe.getStatus();
        if (status.equals(Sequence.STATUS_PENDING)) {
            probe.setStatus(Sequence.STATUS_RECENTLY_MISSED);
            notificationManager.cancel(sequenceType, probe.getRecurrentNotificationId());
        } else {
            Logger.v(TAG, "Probe {0} was not pending any more, but {1}. Not expiring.", status);
        }
    }

    private synchronized void scheduleProbeExpiry(Sequence sequence) {
        // Create and schedule the PendingIntent for DailySequenceService
        Intent intent = new Intent(this, DailySequenceService.class);
        intent.putExtra(SEQUENCE_TYPE, sequenceType);
        intent.putExtra(EXPIRE_PROBE, true);
        intent.putExtra(PROBE_ID, sequence.getId());

        long scheduledTime = SystemClock.elapsedRealtime() + Sequence.EXPIRY_DELAY;
        PendingIntent pendingIntent = PendingIntent.getService(this,
                Sequence.getRecurrentRequestCode(sequenceType, EXPIRE_PROBE),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, pendingIntent);
    }

    private synchronized void flushRecentlyMarkedProbes() {
        ArrayList<Sequence> recentProbes = sequencesStorage.getRecentlyMarkedSequences(
                Sequence.TYPE_PROBE);
        if (recentProbes != null && recentProbes.size() > 0) {
            if (recentProbes.size() > 1) {
                Logger.e(TAG, "Found more than one recently marked probe. Offending probes:");
                Logger.eRaw(TAG, json.toJsonInternal(recentProbes));
                errorHandler.logError("Found more than one recently marked probe",
                        new ConsistencyException());
            }

            // One or many, flush them all
            for (Sequence probe : recentProbes) {
                notificationManager.cancel(probe.getId());
                sequencesStorage.remove(probe.getId());
            }
        }
    }

    /**
     * Notify our sequence to the user.
     */
    private synchronized void notifySequence(Sequence sequence) {
        Logger.d(TAG, "Notifying sequence of type {}", sequenceType);

        // Create the PendingIntent
        Intent intent = createSequenceIntent(sequence);
        // Make sure our PendingIntent is original: cancel current one, and set a different request
        // code for different types of sequences
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                Sequence.getRecurrentRequestCode(sequenceType), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        int flags = 0;

        // If this is NOT an MQ, we can flash lights and vibrate.
        // Beeping is dealt with further down (custom sound on the notification object).
        if (!sequenceType.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)) {
            // Should we flash the LED?
            if (sharedPreferences.getBoolean(SettingsActivity.NOTIF_BLINK, true)) {
                Logger.v(TAG, "Activating lights");
                flags |= Notification.DEFAULT_LIGHTS;
            }

            // Should we vibrate?
            if (sharedPreferences.getBoolean(SettingsActivity.NOTIF_VIBRATION, true)) {
                Logger.v(TAG, "Activating vibration");
                flags |= Notification.DEFAULT_VIBRATE;
            }
        }

        // Create our notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setTicker(getString(sequence.getIdTicker()))
        .setContentTitle(getString(sequence.getIdTitle()))
        .setContentText(getString(sequence.getIdText()))
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_stat_notify_small_daydreaming)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .setDefaults(flags);

        if (sequenceType.equals(Sequence.TYPE_PROBE)) {
            // Create dismissal intent
            Intent dismissalIntent = new Intent(this, DailySequenceService.class);
            dismissalIntent.putExtra(SEQUENCE_TYPE, sequenceType);
            dismissalIntent.putExtra(DISMISS_PROBE, true);
            dismissalIntent.putExtra(PROBE_ID, sequence.getId());
            PendingIntent pendingDismissal = PendingIntent.getService(this,
                    Sequence.getRecurrentRequestCode(sequenceType, DISMISS_PROBE),
                    dismissalIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setDeleteIntent(pendingDismissal);
        }

        Notification notification = notificationBuilder.build();

        // Should we beep?
        if (!sequenceType.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)
                && sharedPreferences.getBoolean(SettingsActivity.NOTIF_SOUND, true)) {
            Logger.v(TAG, "Activating beep for notification, custom sound");
            notification.sound = Uri.parse("android.resource://" + "com.brainydroid.daydreaming" +
                    "/" + R.raw.notification);
        }

        if (sequenceType.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)) {
            statusManager.setLastMQNotifToNow();
        }

        // Get a proper id for the notification, to replace the right notifications
        // That way only one of morning notification and one evening notification will ever be there
        notificationManager.cancel(sequenceType, sequence.getRecurrentNotificationId());
        notificationManager.notify(sequenceType, sequence.getRecurrentNotificationId(),
                notification);
    }

    /**
     * Fill our {@link Sequence} with questions.
     */
    private synchronized Sequence populateSequence() {
        Logger.d(TAG, "Populating sequence with sequence of type {}", sequenceType);

        // Pick from already created sequences of type sequenceType that were never shown to the
        // user, if there are any
        ArrayList<Sequence> pendingSequences = sequencesStorage.getPendingSequences(
                sequenceType);

        final Sequence sequence;
        if (pendingSequences != null && pendingSequences.size() > 0) {
            Logger.d(TAG, "Reusing previously pending sequence of type {}", sequenceType);
            sequence = pendingSequences.get(0);
            // Cancelling the notification if this is a probe, is done in notifyProbe()

            if (sequenceType.equals(Sequence.TYPE_PROBE)) {
                // Check that these pending probes are not an error.
                Logger.w(TAG, "Found pending probes, this is highly unlikely.");
                if (Sequence.EXPIRY_DELAY <= parametersStorage.getSchedulingMinDelay()) {
                    Logger.e(TAG, "Found pending probes when EXPIRY_DELAY <= SCHEDULING_MIN_DELAY");
                    Logger.e(TAG, "The only possibility is that the phone rebooted before expiry of a probe, " +
                            "and notification was recreated.");
                    if (pendingSequences.size() > 1) {
                        Logger.e(TAG, "There are even several pending probes, which is really wrong");
                    }
                    Logger.e(TAG, "Offending probes:");
                    Logger.eRaw(TAG, json.toJsonInternal(pendingSequences));
                    errorHandler.logError("Found pending probe(s) in unlikely situation.",
                            new ConsistencyException());
                }
            }
        } else {
            Logger.d(TAG, "Creating new sequence of type {}",sequenceType);
            sequence = sequenceBuilder.buildSave(sequenceType);
        }

        // Update the sequence's status
        Logger.d(TAG, "Setting sequence status and timestamp, and saving");
        sequence.retainSaves();
        sequence.setNotificationSystemTimestamp(
                Calendar.getInstance().getTimeInMillis());
        sequence.setStatus(Sequence.STATUS_PENDING);
        sequence.flushSaves();

        // Pre-load it
        sequence.onPreLoaded(null);

        // Get a timestamp for the sequence
        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    sequence.setNotificationNtpTimestamp(sntpClient.getNow());
                    Logger.i(TAG, "Received and saved NTP time for " +
                            "sequence notification");
                } else {
                    Logger.e(TAG, "Received successful NTP request but " +
                            "sntpClient is null");
                }
            }

        };

        Logger.i(TAG, "Launching NTP request");
        sntpClient.asyncRequestTime(sntpCallback);

        return sequence;
    }

    /**
     * Cancel any pending {@link Sequence}s already notified.
     */
    private synchronized void cancelPendingSequences() {
        Logger.d(TAG, "Cancelling pending sequences of type {}", sequenceType);
        ArrayList<Sequence> pendingSequences = sequencesStorage.getPendingSequences(
                sequenceType);
        if (pendingSequences != null && pendingSequences.size() > 0) {
            for (Sequence sequence : pendingSequences) {
                notificationManager.cancel(sequenceType, sequence.getRecurrentNotificationId());
                sequencesStorage.remove(sequence.getId());
            }
        } else {
            Logger.v(TAG, "No pending sequences of type {} to cancel", sequenceType);
        }
    }

    private void startSchedulerService() {
        Logger.d(TAG, "Starting scheduler for type {}", sequenceType);

        Intent schedulerIntent;
        if (sequenceType.equals(Sequence.TYPE_PROBE)) {
            schedulerIntent = new Intent(this, ProbeSchedulerService.class);
        } else if (sequenceType.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)) {
            schedulerIntent = new Intent(this, MQSchedulerService.class);
        } else if (sequenceType.equals(Sequence.TYPE_EVENING_QUESTIONNAIRE)) {
            schedulerIntent = new Intent(this, EQSchedulerService.class);
        } else {
            throw new RuntimeException("Could not match sequence type to start scheduler ("
                    + sequenceType + ")");
        }
        startService(schedulerIntent);
    }

}
