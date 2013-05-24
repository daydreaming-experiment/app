package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Start {@link SchedulerService} and {@link LocationPointService} when boot
 * is completed.
 * <p/>
 * These services are only started if the first launch has been completed
 * (i.e. the user has registered and is participating in the experiment).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class BootReceiver extends RoboBroadcastReceiver {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "BootReceiver";

    @Inject StatusManager statusManager;

    @Override
    public void handleReceive(Context context, Intent intent) {

        // Were we called because the boot just completed?
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Logger.i(TAG, "Received ACTION_BOOT_COMPLETED");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet
            if (statusManager.isFirstLaunchCompleted()) {
                Logger.i(TAG, "First launch is completed");

                // Start scheduling polls
                Logger.i(TAG, "Starting SchedulerService");
                Intent schedulerIntent = new Intent(context,
                        SchedulerService.class);
                context.startService(schedulerIntent);

                // Start getting location updates
                Logger.i(TAG, "Starting LocationPointService");
                Intent locationPointServiceIntent = new Intent(context,
                        LocationPointService.class);
                context.startService(locationPointServiceIntent);
            }
        }
    }

}
