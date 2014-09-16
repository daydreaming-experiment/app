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
    @Inject AlarmManager alarmManager;

    public static String IS_PERSISTENT = "isPersistent";

    String type;
    boolean isPersistent = false;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "BEQService started");
        super.onStartCommand(intent, flags, startId);

        isPersistent = intent.getBooleanExtra(IS_PERSISTENT,false);


        if (statusManager.areParametersUpdated()) {
            if (!statusManager.areBEQCompleted()) {
                type = statusManager.updateBEQType();
                notificationManager.cancel(TAG, 0);
                notifyQuestionnaire();
                scheduleBQEService();
            } else {
                Logger.d(TAG, "no BEQ notifications");
                notificationManager.cancel(TAG, 0);
            }
        }

        stopSelf();
        return START_REDELIVER_INTENT;
    }

    @Override
    public synchronized IBinder onBind(Intent intent) {
        // Don't allow binding
        return null;
    }

    private synchronized Intent createBEQService() {
        Logger.d(TAG, "Creating BEQ Service");
        Intent intent = new Intent(this, BEQActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("questionnaireType",type);
        return intent;
    }

    private synchronized Notification createBEQNotification() {
        Logger.d(TAG, "Creating BEQ notification");
        Intent intent = createBEQService();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT );
        int flags = 0;
        flags |= Notification.FLAG_NO_CLEAR;
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
                .setDefaults(flags)
                .build();
        return notification;
    }

    /**
     * Notify our probe to the user.
     */
    private synchronized void notifyQuestionnaire() {
        Logger.d(TAG, "Launch BEQ notification");
        Notification notification = createBEQNotification();
        // And send it to the system
        notificationManager.cancel(TAG, 0);
        notificationManager.notify(TAG, 0, notification);
    }

    private synchronized void scheduleBQEService() {
        Logger.d(TAG, "Schedule BEQ notification in one hour");
        Intent intent = createBEQService();
        long scheduledTime = SystemClock.elapsedRealtime() + (60 * 60 * 1000); // 1h
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT );
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                scheduledTime, contentIntent);

    }


}
