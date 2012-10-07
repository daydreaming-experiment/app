package com.brainydroid.daydreaming;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ExperimentService extends Service {

	private final int SERVICE_NOTIFICATION = 1;

	private SharedPreferences mDPrefs;
	private SharedPreferences.Editor eDPrefs;

	private boolean stopSelfOnUnbind = false;
	private final IBinder mBinder = new LocalBinder();

	private NotificationManager notificationManager;

	public class LocalBinder extends Binder {
		ExperimentService getService() {
			// Return this instance of ExperimentService so clients can call public methods
			return ExperimentService.this;
		}
	}

	@Override
	public void onCreate() {
		mDPrefs = getSharedPreferences(getString(R.pref.dashboardPrefs), MODE_PRIVATE);
		eDPrefs = mDPrefs.edit();

		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), true);
		eDPrefs.commit();

		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, DashboardActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification = new NotificationCompat.Builder(this)
		.setTicker(getString(R.string.serviceNotification_ticker))
		.setContentTitle(getString(R.string.serviceNotification_title))
		.setContentText(getString(R.string.serviceNotification_text))
		.setContentIntent(contentIntent)
		.setSmallIcon(android.R.drawable.ic_dialog_info)
		.setOngoing(true)
		.build();
		notificationManager.notify(SERVICE_NOTIFICATION, notification);

		Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (notificationManager != null) {
			notificationManager.cancel(SERVICE_NOTIFICATION);
		}

		eDPrefs.putBoolean(getString(R.pref.dashboardExpRunning), false);
		eDPrefs.commit();

		Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(getApplicationContext(), "Service bound", Toast.LENGTH_SHORT).show();
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Toast.makeText(getApplicationContext(), "Service rebound", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);

		Toast.makeText(getApplicationContext(), "Service unbound", Toast.LENGTH_SHORT).show();
		if (stopSelfOnUnbind) {
			stopSelf();
		}

		return true;
	}

	public void stopServiceOnUnbind() {
		stopSelfOnUnbind = true;
	}
}
