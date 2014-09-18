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
 * @see com.brainydroid.daydreaming.background.SyncService
 * @see com.brainydroid.daydreaming.background.DailySequenceService
 */
public class EQSchedulerService extends SequenceSchedulerService {

    protected static String TAG = "EQSchedulerService";

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
        scheduleSequence(Sequence.TYPE_EVENING_QUESTIONNAIRE);
        stopSelf();

        return START_REDELIVER_INTENT;
    }


    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of EQ");
        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();
        Calendar startIfToday = (Calendar) now.clone();
        startIfToday.set(Calendar.HOUR_OF_DAY, endAllowedHour );
        startIfToday.set(Calendar.MINUTE, endAllowedMinute);
        Calendar startIfTomorrow = (Calendar) startIfToday.clone();
        startIfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        long scheduledTime;
        if (now.before(startIfToday)) {
            Logger.d(TAG, "now < morning time today -> sheduling today : {}",  startIfToday.toString());
            // still time to schedule for today!
            scheduledTime =  startIfToday.getTimeInMillis();
            startIfToday.toString();
        } else {
            Logger.d(TAG, "now > morning time today -> sheduling tomorrow : {}",  startIfTomorrow.toString());
            scheduledTime = startIfTomorrow.getTimeInMillis();
        }

        long delay = scheduledTime - now.getTimeInMillis() + 5000;
        printLogOfDelay(delay);
        return nowUpTime +delay;
    }



}
