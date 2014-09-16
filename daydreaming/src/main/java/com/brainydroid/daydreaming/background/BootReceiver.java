package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
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
 * @see SchedulerService
 * @see LocationPointService
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
            Logger.d(TAG, "BootReceiver started for ACTION_BOOT_COMPLETED");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet
            if (statusManager.isFirstLaunchCompleted()) {
                Logger.d(TAG, "First launch is completed");

                // Start scheduling polls
                Logger.d(TAG, "Starting SchedulerService");
                Intent schedulerIntent = new Intent(context,
                        SchedulerService.class);
                context.startService(schedulerIntent);

                // Start notifying questionnaires
                if (statusManager.areParametersUpdated()) {
                    Logger.d(TAG, "Starting BQEService");
                    Intent BQEIntent = new Intent(context,
                            BEQService.class);
                    BQEIntent.putExtra(BEQService.IS_PERSISTENT, true);
                    context.startService(schedulerIntent);
                }

                // Start getting location updates
                Logger.d(TAG, "Starting LocationPointService");
                Intent locationPointServiceIntent = new Intent(context,
                        LocationPointService.class);
                context.startService(locationPointServiceIntent);
            } else {
                Logger.v(TAG, "First launch not completed -> exiting");
            }
        } else {
            Logger.v(TAG, "BootReceiver started for something different " +
                    "than ACTION_BOOT_COMPLETED -> exiting");
        }
    }

}
