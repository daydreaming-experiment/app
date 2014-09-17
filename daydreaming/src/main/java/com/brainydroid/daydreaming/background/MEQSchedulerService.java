package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.sequence.Sequence;

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
public class MEQSchedulerService extends SequenceSchedulerService {

    protected static String TAG = "MEQSchedulerService";

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
        scheduleSequence(Sequence.TYPE_MORNING_QUESTIONNAIRE);
        scheduleSequence(Sequence.TYPE_EVENING_QUESTIONNAIRE);
        stopSelf();

        return START_REDELIVER_INTENT;
    }

}
