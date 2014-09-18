package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.sequence.Sequence;

import java.util.Calendar;

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
public class MQSchedulerService extends SequenceSchedulerService {

    protected static String TAG = "MorningQSchedulerService";

    /** Extra to set to {@code true} for debugging */
    public static String SCHEDULER_DEBUGGING = "schedulerDebugging";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "MorningSchedulerService started");

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
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of MQ");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        Calendar startIfToday = (Calendar) now.clone();
        startIfToday.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        startIfToday.set(Calendar.MINUTE, startAllowedMinute);
        Calendar startIfTomorrow = (Calendar) startIfToday.clone();
        startIfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        long scheduledTime;
        if (nowUpTime < startIfToday.getTimeInMillis()) {
            // still time to schedule for today!
            scheduledTime =  startIfToday.getTimeInMillis();
        } else {
            scheduledTime = startIfTomorrow.getTimeInMillis();
        }

        long delay = scheduledTime - nowUpTime + 5000;
        printLogOfDelay(delay);
        return scheduledTime;
    }

}
