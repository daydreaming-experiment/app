package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Start {@link ProbeSchedulerService} and {@link LocationPointService} when boot
 * is completed.
 * <p/>
 * These services are only started if the first launch has been completed
 * (i.e. the user has registered and is participating in the experiment).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see ProbeSchedulerService
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

                // FIXME: why is this not in areParametersUpdated?
                // Start scheduling polls
                Logger.d(TAG, "Starting ProbeSchedulerService");
                Intent schedulerIntent = new Intent(context, ProbeSchedulerService.class);
                context.startService(schedulerIntent);

                // Start notifying BE questionnaires
                if (statusManager.areParametersUpdated()) {
                    Logger.d(TAG, "Starting BEQService");
                    Intent BEQIntent = new Intent(context, BEQSchedulerService.class);
                    BEQIntent.putExtra(BEQSchedulerService.IS_PERSISTENT, true);
                    context.startService(BEQIntent);

                // Start notifying Morning questionnaires
                    Logger.d(TAG, "Starting MQService");
                    Intent MQIntent = new Intent(context, MQSchedulerService.class);
                    context.startService(MQIntent);

                // Start notifying Evening questionnaires
                    Logger.d(TAG, "Starting EQService");
                    Intent EQIntent = new Intent(context, EQSchedulerService.class);
                    context.startService(EQIntent);
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
