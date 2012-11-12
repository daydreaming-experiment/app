package com.brainydroid.daydreaming.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

public class TimeReceiver extends BroadcastReceiver {

	private static String TAG = "TimeReceiver";

	private StatusManager status;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onReceive");
		}

		status = StatusManager.getInstance(context);
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

			// Info
			Log.i(TAG, "Received ACTION_TIME_CHANGED or ACTION_TIMEZONE_CHANGED");

			if (status.isFirstLaunchCompleted()) {

				// Info
				Log.i(TAG, "first launch is completed");
				Log.i(TAG, "starting SchedulerService");

				Intent schedulerIntent = new Intent(context, SchedulerService.class);
				context.startService(schedulerIntent);
			}
		}
	}
}
