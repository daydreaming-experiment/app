package com.brainydroid.daydreaming;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.brainydroid.daydreaming.ExpService.LocalBinder;

public class ExpServiceConnection implements ServiceConnection {

	private boolean stopServiceOnBound = false;
	private ExpService expService;
	private final Context context;

	public ExpServiceConnection(Context c) {
		context = c;
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// We've bound to ExperimentService, cast the IBinder and get ExperimentService instance
		LocalBinder binder = (LocalBinder)service;
		expService = binder.getService();
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
			expService.setStopServiceOnUnbind();
			context.unbindService(this);
		}
	}
}
