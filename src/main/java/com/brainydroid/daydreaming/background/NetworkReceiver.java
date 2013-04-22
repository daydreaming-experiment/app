package com.brainydroid.daydreaming.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

public class NetworkReceiver extends BroadcastReceiver {

	public static String TAG = "NetworkReceiver";

    @Inject StatusManager statusManager;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onReceive");
		}

		String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			// Info
			Log.i(TAG, "Received CONNECTIVITY_ACTION");

			if (statusManager.isFirstLaunchCompleted() && statusManager.isDataEnabled()) {

				// Info
				Log.i(TAG, "first launch is completed");
				Log.i(TAG, "starting SyncService");

				Intent syncIntent = new Intent(context, SyncService.class);
				context.startService(syncIntent);
			}
		}
	}
}
