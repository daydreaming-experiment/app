package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.sequence.Sequence;

import java.util.Calendar;

public class MQSchedulerService extends SequenceSchedulerService {

    protected static String TAG = "MQSchedulerService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "MQSchedulerService started");

        super.onStartCommand(intent, flags, startId);

        // Schedule a sequence
        if (!statusManager.areParametersUpdated()) {
            Logger.d(TAG, "Parameters not updated yet. aborting scheduling.");
            return START_REDELIVER_INTENT;
        }

        // Schedule a sequence
        scheduleSequence(Sequence.TYPE_MORNING_QUESTIONNAIRE);
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of MQ");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        Calendar startIfToday = (Calendar)now.clone();
        startIfToday.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        startIfToday.set(Calendar.MINUTE, startAllowedMinute);
        Calendar startIfTomorrow = (Calendar)startIfToday.clone();
        startIfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        long scheduledTime;
        if (now.before(startIfToday)) {
            Logger.d(TAG, "now < morning time today -> scheduling today");
            // still time to schedule for today!
            scheduledTime =  startIfToday.getTimeInMillis();
        } else {
            Logger.d(TAG, "now > morning time today -> scheduling tomorrow");
            scheduledTime = startIfTomorrow.getTimeInMillis();
        }

        long delay = scheduledTime - now.getTimeInMillis() + 5000;
        logDelay(delay);
        return nowUpTime + delay;
    }

}
