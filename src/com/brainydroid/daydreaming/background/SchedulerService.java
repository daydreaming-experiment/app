package com.brainydroid.daydreaming.background;

import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

public class SchedulerService extends Service {

	private static long SAMPLE_TIME_MEAN = 15 * 1000; // in milliseconds
	private static long SAMPLE_TIME_STD = 5 * 1000; // in milliseconds
	private static long SAMPLE_TIME_MIN = 0; // in milliseconds;

	private Random random;
	private AlarmManager alarmManager;

	@Override
	public void onCreate() {
		super.onCreate();
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		schedulePoll();
		stopSelf();
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

	public void schedulePoll() {
		// TODO: check for current time against times at which polls are allowed
		long scheduledTime = generateTime();

		Intent intent = new Intent(this, PollService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				scheduledTime, pendingIntent);

		Toast.makeText(this, "Poll scheduled in " +
				((scheduledTime - SystemClock.elapsedRealtime()) / 1000f) +
				" seconds", Toast.LENGTH_LONG).show();
	}

	private long generateTime() {
		long wait = SAMPLE_TIME_MIN;

		for (int i = 0; i < 1000; i++) {
			wait = (long)(random.nextGaussian() * SAMPLE_TIME_STD) + SAMPLE_TIME_MEAN;
			if (wait >= SAMPLE_TIME_MIN) {
				break;
			}
		}

		if (wait < SAMPLE_TIME_MIN) {
			wait = SAMPLE_TIME_MIN;
		}

		return SystemClock.elapsedRealtime() + wait;
	}
}
