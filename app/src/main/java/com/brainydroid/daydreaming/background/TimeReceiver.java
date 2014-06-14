package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Start {@link SchedulerService} to reschedule the next {@link
 * com.brainydroid.daydreaming.db.Poll} when the user's time settings change.
 * <p/>
 * This service is only started if the first launch has been completed
 * (i.e. the user has registered and is participating in the experiment).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see SchedulerService
 */
public class TimeReceiver extends RoboBroadcastReceiver {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "TimeReceiver";

    @Inject StatusManager statusManager;

    @Override
    public void handleReceive(Context context, Intent intent) {

        // Were we called because the time settings changed?
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            Logger.d(TAG, "TimeReceiver started for ACTION_TIME_CHANGED or " +
                    "ACTION_TIMEZONE_CHANGED");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet
            if (statusManager.isFirstLaunchCompleted()) {
                Logger.d(TAG, "First launch is completed");

                // Reschedule the next poll
                Logger.d(TAG, "Starting SchedulerService");
                Intent schedulerIntent = new Intent(context,
                        SchedulerService.class);
                context.startService(schedulerIntent);
            } else {
                Logger.v(TAG, "First launch not completed -> exiting");
            }
        } else {
            Logger.v(TAG, "BootReceiver started for something different " +
                    "than ACTION_TIME_CHANGED or ACTION_TIMEZONE_CHANGED ->" +
                    " exiting");
        }
    }

}
