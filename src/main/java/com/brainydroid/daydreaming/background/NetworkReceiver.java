package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onReceive");
        }

        String action = intent.getAction();

        // Were we called because Internet just became available?
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            // Info
            Log.i(TAG, "Received CONNECTIVITY_ACTION");

            // If first launch hasn't been completed, the user doesn't want
            // anything yet. We also want Internet to be available.
            if (statusManager.isFirstLaunchCompleted() &&
                    statusManager.isDataEnabled()) {

                // Info
                Log.i(TAG, "first launch is completed");
                Log.i(TAG, "starting SyncService");

                // Start synchronizing answers
                Intent syncIntent = new Intent(context, SyncService.class);
                context.startService(syncIntent);
            }
        }
    }

}
