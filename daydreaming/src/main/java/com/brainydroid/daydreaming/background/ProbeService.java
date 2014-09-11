package com.brainydroid.daydreaming.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
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
 * @see SchedulerService
 * @see SyncService
 */
public class ProbeService extends RoboService {

    private static String TAG = "ProbeService";

    public static String CANCEL_PENDING_POLLS = "cancelPendingProbes";

    @Inject NotificationManager notificationManager;
    @Inject SequencesStorage sequencesStorage;
    @Inject SequenceBuilder sequenceBuilder;
    @Inject SharedPreferences sharedPreferences;
    @Inject SntpClient sntpClient;
    @Inject Sequence probe;
    @Inject StatusManager statusManager;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "ProbeService started");

        super.onStartCommand(intent, flags, startId);

        if (intent.getBooleanExtra(CANCEL_PENDING_POLLS, false)) {
            Logger.v(TAG, "Started to cancel pending probes");
            cancelPendingProbes();
        } else {
            Logger.v(TAG, "Started to create and notify a probe");

            // If the questions haven't been downloaded (which is probably
            // because the json was malformed), only reschedule (which will
            // re-download the questions; hopefully they will have been fixed)
            // and don't show any probe.
            if (statusManager.areParametersUpdated()) {
                // Populate and notify the probe
                populateProbe();
                notifyProbe();
            }

            // Schedule the next probe
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
    private synchronized Intent createProbeIntent() {
        Logger.d(TAG, "Creating probe Intent");

        Intent intent = new Intent(this, PageActivity.class);

        // Set the id of the probe to start
        intent.putExtra(PageActivity.EXTRA_SEQUENCE_ID, probe.getId());

        // Create a new task. The rest is defined in the App manifest.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Notify our probe to the user.
     */
    private synchronized void notifyProbe() {
        Logger.d(TAG, "Notifying probe");

        // Create the PendingIntent
        Intent intent = createProbeIntent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT |
                PendingIntent.FLAG_ONE_SHOT);

        int flags = 0;

        // Should we flash the LED?
        if (sharedPreferences.getBoolean("notification_blink_key", true)) {
            Logger.v(TAG, "Activating lights");
            flags |= Notification.DEFAULT_LIGHTS;
        }

        // Should we vibrate?
        if (sharedPreferences.getBoolean("notification_vibrator_key", true)) {
            Logger.v(TAG, "Activating vibration");
            flags |= Notification.DEFAULT_VIBRATE;
        }

        // Should we beep?
        if (sharedPreferences.getBoolean("notification_sound_key", true)) {
            Logger.v(TAG, "Activating sound");
            flags |= Notification.DEFAULT_SOUND;
        }

        // Create our notification
        Notification notification = new NotificationCompat.Builder(this)
        .setTicker(getString(R.string.probeNotification_ticker))
        .setContentTitle(getString(R.string.probeNotification_title))
        .setContentText(getString(R.string.probeNotification_text))
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_stat_notify_small_daydreaming)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .setDefaults(flags)
        .build();

        // How to beep?
        if (sharedPreferences.getBoolean("notification_sound_key", true)) {
            Logger.v(TAG, "Adding custom sound");
            notification.sound = Uri.parse("android.resource://" + "com.brainydroid.daydreaming" + "/" + R.raw.notification);
        }

        // And send it to the system
        notificationManager.cancel(probe.getId());
        notificationManager.notify(probe.getId(), notification);
    }

    /**
     * Fill our {@link Sequence} with questions.
     */
    private synchronized void populateProbe() {
        Logger.d(TAG, "Populating probe with sequence");

        // Pick from already created probes that were never shown to the
        // user, if there are any
        ArrayList<Sequence> pendingProbes = sequencesStorage.getPendingSequences(
                Sequence.TYPE_PROBE);

        if (pendingProbes != null) {
            Logger.d(TAG, "Reusing previously pending probe");
            probe = pendingProbes.get(0);
        } else {
            Logger.d(TAG, "Creating new probe");
            probe = sequenceBuilder.buildSave(Sequence.TYPE_PROBE);
        }

        // Update the probe's status
        Logger.d(TAG, "Setting probe status and timestamp, and saving");
        probe.retainSaves();
        probe.setNotificationSystemTimestamp(
                Calendar.getInstance().getTimeInMillis());
        probe.setStatus(Sequence.STATUS_PENDING);
        probe.flushSaves();

        // Get a timestamp for the probe
        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    probe.setNotificationNtpTimestamp(sntpClient.getNow());
                    Logger.i(TAG, "Received and saved NTP time for " +
                            "probe notification");
                } else {
                    Logger.e(TAG, "Received successful NTP request but " +
                            "sntpClient is null");
                }
            }

        };

        Logger.i(TAG, "Launching NTP request");
        sntpClient.asyncRequestTime(sntpCallback);
    }

    /**
     * Start {@link SchedulerService} for the next {@link Sequence}.
     */
    private synchronized void startSchedulerService() {
        Logger.d(TAG, "Starting SchedulerService");

        Intent schedulerIntent = new Intent(this, SchedulerService.class);
        startService(schedulerIntent);
    }

    /**
     * Cancel any pending {@link Sequence}s already notified.
     */
    private synchronized void cancelPendingProbes() {
        Logger.d(TAG, "Cancelling pending probes");
        ArrayList<Sequence> pendingProbes = sequencesStorage.getPendingSequences(
                Sequence.TYPE_PROBE);
        if (pendingProbes != null) {
            for (Sequence probe : pendingProbes) {
                notificationManager.cancel(probe.getId());
                sequencesStorage.remove(probe.getId());
            }
        } else {
            Logger.v(TAG, "No pending probes to cancel");
        }
    }

}
