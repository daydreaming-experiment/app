package com.brainydroid.daydreaming.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Don't allow binding
		return null;
	}
}