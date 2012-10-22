package com.brainydroid.daydreaming.background;


import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

// TODO: change this into a service that does its job and quits (like PollService)
public class ScheduleManager {

	private static long SAMPLE_TIME_MEAN = 15 * 1000; // in milliseconds
	private static long SAMPLE_TIME_STD = 5 * 1000; // in milliseconds
	private static long SAMPLE_TIME_MIN = 0; // in milliseconds;

	private static ScheduleManager smInstance = null;

	private final Context context;
	private final Random random;
	private final AlarmManager alarmManager;

	public static ScheduleManager getInstance(Context c) {
		if (smInstance == null) {
			smInstance = new ScheduleManager(c);
		}
		return smInstance;
	}

	private ScheduleManager(Context c) {
		context = c.getApplicationContext();
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		random = new Random(System.currentTimeMillis());
	}

	public void schedulePoll() {
		// TODO: check for current time against times at which polls are allowed
		long scheduledTime = generateTime();

		Intent intent = new Intent(context, PollService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				scheduledTime, pendingIntent);

		Toast.makeText(context, "Poll scheduled in " +
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
