package com.brainydroid.daydreaming.background;

import android.content.Intent;

import com.brainydroid.daydreaming.db.Util;
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
public class ProbeSchedulerService extends RecurrentSequenceSchedulerService {

    protected static String TAG = "ProbeSchedulerService";

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
        return Sequence.TYPE_PROBE;
    }

    /**
     * Sample a moment at which the {@link
     * com.brainydroid.daydreaming.sequence.Sequence} should appear.
     * <p/>
     * A delay is sampled from an exponential distribution with parameter
     * {@link 1 / meanDelay}, and is then prolonged to observe the user's
     * preferences in notification time window. This is done by
     * "compactifying" each prohibited time window to one point: imagine a
     * time line where the beginning of a forbidden time window is the same
     * moment as the end of that forbidden time window,
     * and do the scheduling on that time line ; the result of this method
     * is the same (see {@link #makeRespectfulDelay} and {@link
     * #makeRespectfulExpansion} for details).
     *
     * @return Scheduled (and shifted) moment for the sequence to appear,
     *         in milliseconds from epoch
     */
    @Override
    protected synchronized long generateTime() {
        Logger.d(TAG, "Generating a time for schedule");

        // Fix what 'now' means, and retrieve the allowed time window
        fixNowAndGetAllowedWindow();

        // Build a delay that respects the user's settings.
        long respectfulDelay;
        // Sample a delay and prolong it as necessary to respect the
        // user's settings.
        Logger.d(TAG, "Using random time-window-respectful delay");
        respectfulDelay = makeRespectfulDelay(sampleDelay());

        // Get the scheduled time into a palatable object.
        Calendar scheduledCalendar = (Calendar)now.clone();
        scheduledCalendar.add(Calendar.MILLISECOND, (int)respectfulDelay);

        // Now log what's scheduled. This is important to make sure we
        // obverse the user's settings.         Intent schedulerIntent =

        // Compute waiting hour, minute, and second values
        String scheduledString = Util.formatDate(scheduledCalendar.getTime());
        logDelay(respectfulDelay);
        Logger.td(this, "New sequence scheduled at {0}", scheduledString);

        // The scheduled time is returned in milliseconds
        return nowUpTime + respectfulDelay;
    }


    /**
     * Sample a delay following an exponential distribution with parameter
     * {@link 1 / meanDelay}.
     *
     * @return Sampled delay in milliseconds
     */
    protected synchronized long sampleDelay() {
        Logger.d(TAG, "Sampling delay");
        // Delays are given in seconds by the parameters
        int minDelayMilli = 1000 * parametersStorage.getSchedulingMinDelay();
        int meanDelayMilli = 1000 * parametersStorage.getSchedulingMeanDelay();
        return (long)(minDelayMilli -
                Math.log(random.nextDouble()) * (meanDelayMilli - minDelayMilli));
    }

    /**
     * Prolong the waiting delay to respect the user's
     * preferences_appSettings in notification time window.
     * <p/>
     * If the suggested scheduled time falls in the user's forbidden time
     * window, we need to adapt to make sure no sequence will get notified in
     * that time window. We need to do so in a random-respectful way,
     * i.e. so that we don't have half the probes appearing right at the
     * beginning of the allowed time window (which would be the result of a
     * simple policy here).
     * <p/>
     * To do this, we look at each forbidden time window as if it were a
     * unique instant (we "compactify" them). This logic is implemented in
     * a recursive call to {@link #makeRespectfulExpansion}.
     *
     * @param delay Initial delay to make respectful of user's settings
     * @return Resulting respectful delay
     */
    protected synchronized long makeRespectfulDelay(long delay) {
        Logger.d(TAG, "Expanding delay to respect user's time window");

        long expansion = makeRespectfulExpansion(now, delay);

        long milliseconds = expansion;
        long hours = milliseconds / (60 * 60 * 1000);
        milliseconds %= 60 * 60 * 1000;
        long minutes = milliseconds / (60 * 1000);
        milliseconds %= 60 * 1000;
        long seconds = milliseconds / 1000;
        Logger.d(TAG, "Delay expansion: {0}:{1}:{2}",
                hours, minutes, seconds);

        return delay + expansion;
    }

    /**
     * Compute a expansion value that, added to {@code delay},
     * will respect the user's settings if now is {@code hypothesizedNow}.
     * <p/>
     * The expansion is computed by successively removing from {@code delay}
     * all the allowed waiting time that consumes it,
     * at the same time considering forbidden time windows to be
     * compactified, and thus until {@code delay} is short enough and
     * {@code hypothesizedNow} has advanced enough to make {@code
     * hypothesizedNow + delay} fall in an allowed time window without
     * further expansion. You should really read the source to understand the
     * above sentence.
     *
     * @param hypothesizedNow Time we should consider to be 'now'
     * @param delay Suggested waiting delay in milliseconds
     * @return Prolonged waiting delay respecting the user's
     * preferences_appSettings
     */
    protected synchronized long makeRespectfulExpansion(Calendar hypothesizedNow,
                                                        long delay) {
        Logger.d(TAG, "Recursively building expansion value");

        // Convert our start allowed time, end allowed time,
        // next start allowed time, and suggested time, to usable objects.

        Calendar start = (Calendar)hypothesizedNow.clone();
        start.set(Calendar.HOUR_OF_DAY, startAllowedHour);
        start.set(Calendar.MINUTE, startAllowedMinute);

        Calendar end = (Calendar)start.clone();
        end.add(Calendar.MILLISECOND, allowedSpan);

        // This will always be after hypothesizedNow (because on the next
        // day)
        Calendar nextStart = (Calendar)start.clone();
        nextStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar suggested = (Calendar)hypothesizedNow.clone();
        suggested.add(Calendar.MILLISECOND, (int)delay);

        Logger.d(TAG, "Hypothesized now is: {0}",
                Util.formatDate(hypothesizedNow.getTime()));
        Logger.d(TAG, "Suggested is: {0}",
                Util.formatDate(suggested.getTime()));
        Logger.d(TAG, "Allowed start is: {0}",
                Util.formatDate(start.getTime()));
        Logger.d(TAG, "Allowed end is: {0}", Util.formatDate(end.getTime()));
        Logger.d(TAG, "Next allowed start is: {0}",
                Util.formatDate(nextStart.getTime()));

        if (hypothesizedNow.before(start)) {
            // hypothesizedNow is before the allowed time window. So we
            // shift to have the delay begin at the start allowed time,
            // independently of where the suggested time fell (this is
            // compactification of the forbidden time window).

            Logger.d(TAG, "hypothesizedNow < start");
            long delayToStart = start.getTimeInMillis() -
                    hypothesizedNow.getTimeInMillis();

            // Since Calendar.before() is strict (i.e. start.before(start)
            // returns false), we know we're not falling back into this
            // same case in the recursive call.
            return delayToStart + makeRespectfulExpansion(start, delay);
        } else if (hypothesizedNow.before(end)) {
            // hypothesizedNow is in the allowed time window. This includes
            // hypothesizedNow == start.

            if (suggested.before(end)) {
                // Suggested schedule falls in today's allowed time window.
                // Accept that.

                // Recursion will eventually fall into this case because
                // delay is reduced of a lower bounded strictly positive
                // value at each recursive call (because the allowedSpan is
                // at least X hours).

                Logger.d(TAG, "start < hypothesizedNow, suggested < end");
                return 0;
            } else {
                // Suggested schedule falls outside the allowed time window.
                // Shorten delay of what's left from hypothesizedNow to the
                // end of the allowed time window, and compute a new shift
                // starting from the nextStart time (again, this is
                // compactification of the forbidden time window).

                Logger.d(TAG, "start < hypothesizedNow < end < suggested");
                long delayToEnd = end.getTimeInMillis() -
                        hypothesizedNow.getTimeInMillis();
                long newDelay = delay - delayToEnd;
                return delayToEnd + forbiddenSpan +
                        makeRespectfulExpansion(nextStart, newDelay);
            }
        } else {
            // hypothesizedNow is after the end of the allowed time window.
            // So we shift to have the delay begin at the next allowed start
            // time, independently of where the suggested time fell (this
            // is compactification of the forbidden time window).

            Logger.d(TAG, "end < hypothesizedNow");
            long delayToNextStart = nextStart.getTimeInMillis() -
                    hypothesizedNow.getTimeInMillis();

            // Again, since Calendar.before() is strict (i.e.
            // nextStart.before(nextStart) returns false),
            // we know we're not falling back into the top case in the
            // recursive call.
            return delayToNextStart + makeRespectfulExpansion(nextStart, delay);
        }
    }

}
