package com.brainydroid.daydreaming.background;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	private StatusManager status;

	@Override
	public void onReceive(Context context, Intent intent) {
		status = StatusManager.getInstance(context);
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (status.isFirstLaunchCompleted()) {
				// TODO: add call to SchedulerService
				Toast.makeText(context, "Will start SchedulerService", Toast.LENGTH_SHORT).show();
			}
		}
	}
}