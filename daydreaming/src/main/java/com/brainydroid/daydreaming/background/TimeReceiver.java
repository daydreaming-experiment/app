package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Start {@link ProbeSchedulerService} to reschedule the next {@link
 * com.brainydroid.daydreaming.sequence.Sequence} when the user's time settings change.
 * <p/>
 * This service is only started if the first launch has been completed
 * (i.e. the user has registered and is participating in the experiment).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see ProbeSchedulerService
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

            statusManager.relaunchAllServices();
        } else {
            Logger.v(TAG, "BootReceiver started for something different " +
                    "than ACTION_TIME_CHANGED or ACTION_TIMEZONE_CHANGED ->" +
                    " exiting");
        }
    }

}
