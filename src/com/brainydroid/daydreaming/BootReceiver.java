package com.brainydroid.daydreaming;

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
				Toast.makeText(context, "Will start questions scheduler", Toast.LENGTH_SHORT).show();
			}
		}
	}
}