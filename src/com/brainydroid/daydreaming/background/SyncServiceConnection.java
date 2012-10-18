package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.brainydroid.daydreaming.background.SyncService.LocalBinder;

public class SyncServiceConnection implements ServiceConnection {

	private boolean stopServiceOnBound = false;
	private SyncService syncService;
	private final Context context_;

	public SyncServiceConnection(Context context) {
		context_ = context.getApplicationContext();
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// We've bound to ExperimentService, cast the IBinder and get ExperimentService instance
		LocalBinder binder = (LocalBinder)service;
		syncService = binder.getService();
		onBound();
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
	}

	public void setStopServiceOnBound(boolean stop) {
		stopServiceOnBound = stop;
	}

	private void onBound() {
		if (stopServiceOnBound) {
			syncService.setStopServiceOnUnbind();
			context_.unbindService(this);
		}
	}
}
