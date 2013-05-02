package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onReceive");
        }

        String action = intent.getAction();

        // Were we called because the boot just completed?
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {

            // Info
            Log.i(TAG, "Received ACTION_BOOT_COMPLETED");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet
            if (statusManager.isFirstLaunchCompleted()) {

                // Info
                Log.i(TAG, "first launch is completed");
                Log.i(TAG, "starting SchedulerService");

                // Start scheduling polls
                Intent schedulerIntent = new Intent(context,
                        SchedulerService.class);
                context.startService(schedulerIntent);

                // Info
                Log.i(TAG, "starting LocationPointService");

                // Start getting location updates
                Intent locationPointServiceIntent = new Intent(context,
                        LocationPointService.class);
                context.startService(locationPointServiceIntent);
            }
        }
    }

}
