package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
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
 */
public class TimeReceiver extends RoboBroadcastReceiver {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "TimeReceiver";

    @Inject StatusManager statusManager;

    @Override
    public void handleReceive(Context context, Intent intent) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onReceive");
        }

        String action = intent.getAction();

        // Were we called because the time settings changed?
        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            // Info
            Log.i(TAG, "Received ACTION_TIME_CHANGED or ACTION_TIMEZONE_CHANGED");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet
            if (statusManager.isFirstLaunchCompleted()) {

                // Info
                Log.i(TAG, "first launch is completed");
                Log.i(TAG, "starting SchedulerService");

                // Reschedule the next poll
                Intent schedulerIntent = new Intent(context, SchedulerService.class);
                context.startService(schedulerIntent);
            }
        }
    }

}
