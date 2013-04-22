package com.brainydroid.daydreaming.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

public class BootReceiver extends BroadcastReceiver {

	public static String TAG = "BootReceiver";

	@Inject StatusManager statusManager;

	@Override
	public void onReceive(Context context, Intent intent) {

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
				Log.i(TAG, "starting SchedulerService");
                Log.i(TAG, "starting LocationItemService");

				Intent schedulerIntent = new Intent(context, SchedulerService.class);
				context.startService(schedulerIntent);

                Intent locationItemServiceIntent = new Intent(context, LocationItemService.class);
                context.startService(locationItemServiceIntent);
			}
		}
	}
}
