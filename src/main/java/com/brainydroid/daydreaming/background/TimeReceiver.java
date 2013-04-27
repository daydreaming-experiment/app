package com.brainydroid.daydreaming.background;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

public class TimeReceiver extends RoboBroadcastReceiver {

	public static String TAG = "TimeReceiver";

    @Inject StatusManager statusManager;

	@Override
	public void handleReceive(Context context, Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onReceive");
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

			// Info
			Log.i(TAG, "Received ACTION_TIME_CHANGED or ACTION_TIMEZONE_CHANGED");

			if (statusManager.isFirstLaunchCompleted()) {

				// Info
				Log.i(TAG, "first launch is completed");
				Log.i(TAG, "starting SchedulerService");

				Intent schedulerIntent = new Intent(context, SchedulerService.class);
				context.startService(schedulerIntent);
			}
		}
	}

}
