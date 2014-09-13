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
import com.brainydroid.daydreaming.ui.dashboard.BeginQuestionnairesActivity;
import com.google.inject.Inject;

import roboguice.service.RoboService;

/**
 * Creates a notification launching the BeginQuestionnaireActivity and notifies the user
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see com.brainydroid.daydreaming.sequence.Sequence
 * @see SchedulerService
 * @see com.brainydroid.daydreaming.background.SyncService
 */
public class BeginQuestionnaireService extends RoboService {

    private static String TAG = "BeginQuestionnaireService";


    @Inject NotificationManager notificationManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject StatusManager statusManager;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "BeginQuestionnaireService started");
        super.onStartCommand(intent, flags, startId);
        if (statusManager.areParametersUpdated() &&
                !statusManager.areBeginQuestionnairesCompleted()) {
            notifyQuestionnaire();
        }
        //TODO[Vincent] Schedule the next Questionnaire reminder
        stopSelf();
        return START_REDELIVER_INTENT;
    }

    @Override
    public synchronized IBinder onBind(Intent intent) {
        // Don't allow binding
        return null;
    }

    /**
     * Create the {@link com.brainydroid.daydreaming.ui.sequences.PageActivity} {@link android.content.Intent}.
     *
     * @return An {@link android.content.Intent} to launch our {@link com.brainydroid.daydreaming.sequence.Sequence}
     */
    private synchronized Intent createBeginQuestionnaireIntent() {
        Logger.d(TAG, "Creating BeginQuestionnaire Intent");
        Intent intent = new Intent(this, BeginQuestionnairesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Notify our probe to the user.
     */
    private synchronized void notifyQuestionnaire() {
        Logger.d(TAG, "Notifying BeginQuestionnaire");

        // Create the PendingIntent
        Intent intent = createBeginQuestionnaireIntent();
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
        .setTicker(getString(R.string.bqNotification_ticker))
        .setContentTitle(getString(R.string.bqNotification_title))
        .setContentText(getString(R.string.bqNotification_text))
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
        notificationManager.notify(TAG, 0, notification);
    }

}
