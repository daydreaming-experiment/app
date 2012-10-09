package com.brainydroid.daydreaming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Booter extends BroadcastReceiver {

	private StatusManager status;
	SharedPreferences mPrefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		status = StatusManager.getInstance(context);
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			mPrefs = context.getSharedPreferences(context.getString(R.pref.dashboardPrefs),
					Context.MODE_PRIVATE);
			if (mPrefs.getBoolean(context.getString(R.pref.dashboardStartServiceAtBoot),
					false) && status.isFirstLaunchCompleted()) {
				status.manageExpService(true);
			}
		}
	}
}