package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Start {@link SyncService} when Internet becomes available.
 * <p/>
 * This service is only started if the first launch has been completed
 * (i.e. the user has registered and is participating in the experiment).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class NetworkReceiver extends RoboBroadcastReceiver {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "NetworkReceiver";

    @Inject StatusManager statusManager;

    @Override
    public void handleReceive(Context context, Intent intent) {

        // Were we called because Internet just became available?
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Logger.d(TAG, "NetworkReceiver started for CONNECTIVITY_ACTION");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet. We also want Internet to be available.
            if (statusManager.isFirstLaunchCompleted() &&
                    statusManager.isDataEnabled()) {
                Logger.d(TAG, "First launch is completed and data is " +
                        "enabled");

                // Start synchronizing answers
                Logger.d(TAG, "Starting SyncService");
                Intent syncIntent = new Intent(context, SyncService.class);
                context.startService(syncIntent);
            } else {
                Logger.v(TAG, "First launch not completed or data not " +
                        "enabled -> exiting");
            }
        } else {
            Logger.v(TAG, "NetworkReceived started for something different " +
                    "than CONNECTIVITY_ACTION -> exiting");
        }
    }

}
