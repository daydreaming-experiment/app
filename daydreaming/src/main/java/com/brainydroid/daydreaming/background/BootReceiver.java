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
            statusManager.relaunchAllServices();
        }
    }

}
