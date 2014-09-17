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
import com.brainydroid.daydreaming.sequence.Sequence;
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
 * @see SyncService
 * @see DailySequenceService
 */
public class ProbeSchedulerService extends SequenceSchedulerService {

    protected static String TAG = "ProbeSchedulerService";

    /** Extra to set to {@code true} for debugging */
    public static String SCHEDULER_DEBUGGING = "schedulerDebugging";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "ProbeSchedulerService started");

        super.onStartCommand(intent, flags, startId);

        // Record last time we ran
        statusManager.setLatestSchedulerServiceSystemTimestampToNow();

        // Check LocationPointService hasn't died
        statusManager.checkLatestLocationPointServiceWasAgesAgo();

        // Notify results if they're available
        notifyResultsIfAvailable();

        // Check if we are getting close to the end to enable the final Begin/End questionnaires
        statusManager.updateBEQType();


        // Synchronise answers and get parameters if we don't have them. If parameters
        // happen to be updated, the ProbeSchedulerService will be run again.
        startSyncService();

        // Schedule a sequence
        if (!statusManager.areParametersUpdated()) {
            Logger.d(TAG, "Parameters not updated yet. aborting scheduling.");
            return START_REDELIVER_INTENT;
        }

        // Schedule a sequence
        debugging = intent.getBooleanExtra(SCHEDULER_DEBUGGING, false);
        scheduleSequence(Sequence.TYPE_PROBE);
        stopSelf();

        return START_REDELIVER_INTENT;
    }

}
