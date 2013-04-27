package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

public class BootReceiver extends RoboBroadcastReceiver {

	public static String TAG = "BootReceiver";

	@Inject StatusManager statusManager;

	@Override
	public void handleReceive(Context context, Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onReceive");
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {

			// Info
			Log.i(TAG, "Received ACTION_BOOT_COMPLETED");

			if (statusManager.isFirstLaunchCompleted()) {

				// Info
				Log.i(TAG, "first launch is completed");

                // Info
                Log.i(TAG, "starting SchedulerService");

				Intent schedulerIntent = new Intent(context, SchedulerService.class);
				context.startService(schedulerIntent);

                // Info
                Log.i(TAG, "starting LocationPointService");

                Intent locationItemServiceIntent = new Intent(context, LocationPointService.class);
                context.startService(locationItemServiceIntent);
			}
		}
	}

}
