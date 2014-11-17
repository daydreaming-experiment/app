package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.sequence.Sequence;

import java.util.Calendar;

public class MQSchedulerService extends RecurrentSequenceSchedulerService {

    protected static String TAG = "MQSchedulerService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "Started");

        super.onStartCommand(intent, flags, startId);

        // Schedule a sequence
        if (!statusManager.areParametersUpdated()) {
            Logger.d(TAG, "Parameters not updated yet. aborting scheduling.");
            return START_REDELIVER_INTENT;
        }

        // Schedule a sequence
        scheduleSequence();
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    @Override
    protected String getSequenceType() {
        return Sequence.TYPE_MORNING_QUESTIONNAIRE;
    }

    @Override
    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of MQ");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        Calendar todayBegin = (Calendar) now.clone();
        todayBegin.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        Calendar todayEnd = (Calendar) todayBegin.clone();
        // Allow notifying up to 3 hours after opening of bother window
        todayEnd.add(Calendar.HOUR_OF_DAY, 3);
        // Start 3 hours before opening of bother window
        todayBegin.add(Calendar.HOUR_OF_DAY, -3);
        todayBegin.set(Calendar.MINUTE, startAllowedMinute);
        Calendar tomorrowBegin = (Calendar) todayBegin.clone();
        tomorrowBegin.add(Calendar.DAY_OF_YEAR, 1);

        long scheduledTime;
        if (now.before(todayBegin)) {
            Logger.d(TAG, "now < morning time today - 3 -> scheduling today");
            // still time to schedule for today!
            scheduledTime = todayBegin.getTimeInMillis();
        } else if (now.after(todayBegin) && now.before(todayEnd) && statusManager.isLastMQNotifLongAgo()) {
            Logger.d(TAG, "morning time today + 3 > now > morning time today - 3 -> scheduling today and now");
            scheduledTime = now.getTimeInMillis() + 10 * 1000;
        } else {
            Logger.d(TAG, "Either already notified less than 18h ago, " +
                    "or now > morning time today + 3 -> scheduling tomorrow");
            scheduledTime = tomorrowBegin.getTimeInMillis();
        }

        long delay = scheduledTime - now.getTimeInMillis();
        logDelay(delay);
        return nowUpTime + delay;
    }

}
