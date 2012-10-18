package com.brainydroid.daydreaming.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.brainydroid.daydreaming.background.SyncService.SyncServiceBinder;

public class SyncServiceConnection implements ServiceConnection {

	private boolean stopServiceOnBound = false;
	private SyncService syncService;
	private final Context _context;

	public SyncServiceConnection(Context context) {
		_context = context.getApplicationContext();
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// We've bound to SyncService, cast the IBinder and get SyncService instance
		SyncServiceBinder binder = (SyncServiceBinder)service;
		syncService = binder.getService();
		onBound();
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {}

	public void setStopServiceOnBound(boolean stop) {
		stopServiceOnBound = stop;
	}

	private void onBound() {
		if (stopServiceOnBound) {
			syncService.setStopServiceOnUnbind();
			_context.unbindService(this);
		}
	}
}
