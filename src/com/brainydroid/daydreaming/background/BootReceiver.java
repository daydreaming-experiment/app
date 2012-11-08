package com.brainydroid.daydreaming.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static String TAG = "BootReceiver";

	private StatusManager status;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Debug
		Log.d(TAG, "[fn] onReceive");

		status = StatusManager.getInstance(context);
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {

			// Info
			Log.i(TAG, "Received ACTION_BOOT_COMPLETED");

			if (status.isFirstLaunchCompleted()) {

				// Info
				Log.i(TAG, "First launch is completed");
				Log.i(TAG, "Will start SchedulerService");

				// TODO: add call to SchedulerService
			}
		}
	}
}
