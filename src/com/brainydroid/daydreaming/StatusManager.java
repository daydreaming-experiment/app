package com.brainydroid.daydreaming;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StatusManager {

	private static StatusManager smInstance = null;

	private final String EXP_STATUS = "expStatus";
	private final String FL_STARTED = "flStarted";
	private final String FL_COMPLETED = "flCompleted";
	private final String SERVICE_SHOULD_RUN = "serviceShouldRun";

	private final SharedPreferences expStatus;
	private final SharedPreferences.Editor eExpStatus;

	private final Context context;

	private final ExpServiceConnection expServiceConnection;

	public static StatusManager getInstance(Context c) {
		if (smInstance == null) {
			smInstance = new StatusManager(c);
		}
		return smInstance;
	}

	private StatusManager(Context c) {
		context = c.getApplicationContext();
		expStatus = context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
		expServiceConnection = new ExpServiceConnection(context);
	}

	public boolean isFirstLaunchStarted() {
		return expStatus.getBoolean(FL_STARTED, false);
	}

	public void setFirstLaunchStarted() {
		eExpStatus.putBoolean(FL_STARTED, true);
		eExpStatus.commit();
	}

	public boolean isFirstLaunchCompleted() {
		return expStatus.getBoolean(FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {
		eExpStatus.putBoolean(FL_COMPLETED, true);
		eExpStatus.commit();
	}

	public boolean isExpServiceSouldRun() {
		return expStatus.getBoolean(SERVICE_SHOULD_RUN, true);
	}

	public void setExpServiceShouldRun(boolean status) {
		eExpStatus.putBoolean(SERVICE_SHOULD_RUN, status);
		eExpStatus.commit();
	}

	public boolean isExpServiceRunning() {
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (ExpService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void checkService() {
		if (isExpServiceRunning()) {
			if (!isExpServiceSouldRun()) {
				stopExpService();
			}
		} else {
			if (isExpServiceSouldRun()) {
				startExpService();
			}
		}
	}

	public void manageExpService(boolean start) {
		if (start) {
			startExpService();
		} else {
			stopExpService();
		}
	}

	private void startExpService() {
		if (!isExpServiceRunning()) {
			Intent expServiceIntent = new Intent(context, ExpService.class);
			context.startService(expServiceIntent);
		}
	}

	private void stopExpService() {
		if (isExpServiceRunning()) {
			expServiceConnection.setStopServiceOnBound(true);
			Intent expServiceIntent = new Intent(context, ExpService.class);
			context.bindService(expServiceIntent, expServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}
}