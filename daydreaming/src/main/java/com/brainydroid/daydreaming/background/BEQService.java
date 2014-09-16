package com.brainydroid.daydreaming.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.dashboard.BEQActivity;
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
public class BEQService extends RoboService {

    public static String TAG = "BEQService";


    @Inject NotificationManager notificationManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject StatusManager statusManager;

    String type;
    boolean isPersistent = false;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "BEQService started");
        super.onStartCommand(intent, flags, startId);

        isPersistent = intent.getBooleanExtra("isPersistent",false);

        type = statusManager.getCurrentBEQType();

        if (statusManager.areParametersUpdated()) {
            if (!statusManager.areBEQCompleted()) {
                notifyQuestionnaire();
            } else {
                Logger.d(TAG, "cancel BEQ notifications");
                notificationManager.cancel(TAG, 0);
            }
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
    private synchronized Intent createBeginEndQuestionnaireIntent() {
        Logger.d(TAG, "Creating BeginQuestionnaire Intent");
        Intent intent = new Intent(this, BEQActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("questionnaireType",type);
        return intent;
    }

    /**
     * Notify our probe to the user.
     */
    private synchronized void notifyQuestionnaire() {
        Logger.d(TAG, "Notifying BeginQuestionnaire");

        // Create the PendingIntent
        Intent intent = createBeginEndQuestionnaireIntent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT |
                PendingIntent.FLAG_ONE_SHOT);
        int flags = 0;

        // Create our notification
        // if persistent: cant be dismissed and do not disappear on click
        // if not persistent: can be dismissed and self destroy on click

        Notification notification = new NotificationCompat.Builder(this)
        .setTicker(getString(R.string.bqNotification_ticker))
        .setContentTitle(getString(R.string.bqNotification_title))
        .setContentText(getString(R.string.bqNotification_text))
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_stat_notify_small_daydreaming)
        .setOnlyAlertOnce(true)
        .setAutoCancel(!isPersistent)
        .setOngoing(isPersistent)
        .setDefaults(flags)
        .build();

        // And send it to the system
        notificationManager.cancel(TAG, 0);
        notificationManager.notify(TAG, 0, notification);
    }

}
