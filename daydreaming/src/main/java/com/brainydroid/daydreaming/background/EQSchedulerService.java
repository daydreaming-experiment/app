package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.sequence.Sequence;

import java.util.Calendar;

public class EQSchedulerService extends RecurrentSequenceSchedulerService {

    protected static String TAG = "EQSchedulerService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "Started");

        super.onStartCommand(intent, flags, startId);

        // If exp is not running, don't schedule.
        if (!statusManager.isExpRunning()) {
            Logger.d(TAG, "Experiment is not running. Aborting scheduling.");
            return START_REDELIVER_INTENT;
        }

        // Schedule a sequence
        scheduleSequence();
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    @Override
    protected String getSequenceType() {
        return Sequence.TYPE_EVENING_QUESTIONNAIRE;
    }

    @Override
    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of EQ");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        Calendar startIfToday = (Calendar)now.clone();
        startIfToday.set(Calendar.HOUR_OF_DAY, endAllowedHour);
        startIfToday.set(Calendar.MINUTE, endAllowedMinute);
        Calendar startIfTomorrow = (Calendar)startIfToday.clone();
        startIfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        long scheduledTime;
        if (now.before(startIfToday)) {
            Logger.d(TAG, "now < evening time today -> scheduling today");
            // still time to schedule for today!
            scheduledTime =  startIfToday.getTimeInMillis();
        } else {
            Logger.d(TAG, "now > evening time today -> scheduling tomorrow");
            scheduledTime = startIfTomorrow.getTimeInMillis();
        }

        // Add 10 seconds to time to make sure the scheduled time
        // hasn't gone past since we decided on it
        long delay = scheduledTime - now.getTimeInMillis() + 10 * 1000;
        logDelay(delay);
        return nowUpTime + delay;
    }

}
