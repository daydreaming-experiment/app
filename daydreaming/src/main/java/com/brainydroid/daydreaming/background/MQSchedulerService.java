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
        return Sequence.TYPE_MORNING_QUESTIONNAIRE;
    }

    @Override
    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule of MQ");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        Calendar todayBotherStart = (Calendar) now.clone();
        todayBotherStart.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        todayBotherStart.set(Calendar.MINUTE, startAllowedMinute);

        // Start 3 hours before opening of bother window
        Calendar todayBegin = (Calendar) todayBotherStart.clone();
        todayBegin.add(Calendar.HOUR_OF_DAY, -3);

        // Allow notifying up to 3 hours after opening of bother window
        Calendar todayEnd = (Calendar) todayBotherStart.clone();
        todayEnd.add(Calendar.HOUR_OF_DAY, 3);

        // Allow starting tomorrow if necessary
        Calendar tomorrowBegin = (Calendar) todayBotherStart.clone();
        tomorrowBegin.add(Calendar.DAY_OF_YEAR, 1);
        tomorrowBegin.add(Calendar.HOUR_OF_DAY, -3);

        long scheduledTime;
        if (now.before(todayBegin)) {
            Logger.d(TAG, "morning time today - 3 > now -> scheduling today");
            // still time to schedule for today!
            scheduledTime = todayBegin.getTimeInMillis();
        } else if (now.after(todayBegin) && now.before(todayEnd) && statusManager.isLastMQNotifLongAgo()) {
            Logger.d(TAG, "morning time today + 3 > now > morning time today - 3," +
                    "and last notification was a long time ago -> scheduling today and now");
            scheduledTime = now.getTimeInMillis();
        } else {
            Logger.d(TAG, "Either already notified not long ago, " +
                    "or now > morning time today + 3 -> scheduling tomorrow");
            scheduledTime = tomorrowBegin.getTimeInMillis();
        }

        // Add 10 seconds to time to make sure the scheduled time
        // hasn't gone past since we decided on it
        long delay = scheduledTime - now.getTimeInMillis() + 10 * 1000;
        logDelay(delay);
        return nowUpTime + delay;
    }

}
