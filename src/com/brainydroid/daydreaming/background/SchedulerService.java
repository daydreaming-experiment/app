package com.brainydroid.daydreaming.background;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;

public class SchedulerService extends Service {

	private static String TAG = "SchedulerService";

	private static long SAMPLE_TIME_MEAN = 2 * 60 * 60 * 1000; // 2 hours (in milliseconds)
	private static long SAMPLE_TIME_STD = 1 * 60 * 60 * 1000; // 1 hour (in milliseconds)
	private static long SAMPLE_TIME_MIN = 10 * 60 * 1000; // 10 minutes (in milliseconds)

	private SharedPreferences sharedPrefs;
	private Random random;
	private AlarmManager alarmManager;

	@Override
	public void onCreate() {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate();
		initVars();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		Log.d(TAG, "[fn] onStartCommand");

		super.onStartCommand(intent, flags, startId);

		// Since the poll gets created when the notification shows up, there's a good chance
		// the questions will have finished updating (if internet connection is available)
		// before poll creation.
		startSyncService();
		schedulePoll();
		stopSelf();
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {

		// Debug
		Log.d(TAG, "[fn] onDestroy");

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		Log.d(TAG, "[fn] onBind");

		// Don't allow binding
		return null;
	}

	private void initVars() {

		// Debug
		Log.d(TAG, "[fn] initVars");

		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		random = new Random(System.currentTimeMillis());
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	private void schedulePoll() {

		// Debug
		Log.d(TAG, "[fn] schedulePoll");

		long scheduledTime = generateTime();

		Intent intent = new Intent(this, PollService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				scheduledTime, pendingIntent);

		Calendar now = Calendar.getInstance();
		long wait = scheduledTime - SystemClock.elapsedRealtime();
		now.add(Calendar.MILLISECOND, (int)wait);

		long hours = wait / (60 * 60 * 1000);
		wait -= hours * 60 * 60 * 1000;
		long minutes = wait / (60 * 1000);
		wait -= minutes * 60 * 1000;
		long seconds = wait / 1000;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String target = sdf.format(now.getTime());

		// Info

		Log.i(TAG, "poll scheduled in " + hours +" hours, " +
				minutes + " minutes, and " + seconds + " seconds (i.e. " + target + ")");

		Toast.makeText(this, "New poll scheduled at " + target, Toast.LENGTH_LONG).show();
	}

	private void startSyncService() {

		// Debug
		Log.d(TAG, "[fn] startSyncService");

		Intent syncIntent = new Intent(this, SyncService.class);
		startService(syncIntent);
	}

	private long generateTime() {

		// Debug
		Log.d(TAG, "[fn] generateTime");

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

		return SystemClock.elapsedRealtime() + scaleWaitTime(wait);
	}

	private long scaleWaitTime(long wait) {

		// Debug
		Log.d(TAG, "[fn] scaleWaitTime");

		String[] startPieces = sharedPrefs.getString("time_window_lb_key",
				getString(R.pref.settings_time_window_lb_default)).split(":");
		String[] endPieces = sharedPrefs.getString("time_window_ub_key",
				getString(R.pref.settings_time_window_ub_default)).split(":");

		int startHour = Integer.parseInt(startPieces[0]);
		int startMinute = Integer.parseInt(startPieces[1]);
		int endHour = Integer.parseInt(endPieces[0]);
		int endMinute = Integer.parseInt(endPieces[1]);

		Calendar now = Calendar.getInstance();

		Calendar suggested = (Calendar)now.clone();
		suggested.add(Calendar.MILLISECOND, (int)wait);

		Calendar start = (Calendar)now.clone();
		start.set(Calendar.HOUR_OF_DAY, startHour);
		start.set(Calendar.MINUTE, startMinute);

		Calendar end = (Calendar)now.clone();
		end.set(Calendar.HOUR_OF_DAY, endHour);
		end.set(Calendar.MINUTE, endMinute);

		Calendar nextStart = (Calendar)start.clone();
		nextStart.add(Calendar.DAY_OF_YEAR, 1);

		if (now.before(start)) {
			// Now is before the starting time for notifications

			if (suggested.before(start)) {
				// Suggested schedule is also before start
				// Shift to the beginning of the allowed window
				return wait + start.getTimeInMillis() - now.getTimeInMillis();
			} else {
				// Suggested schedule falls in the allowed window
				// Accept that
				return wait;
			}

		} else if (now.before(end)) {
			// Now is in the allowed window

			if (suggested.before(end) || suggested.after(nextStart)) {
				// Suggested schedule falls in the allowed window (today or tomorrow)
				// Accept that
				return wait;
			} else {
				// Suggested schedule falls outside the allowed window
				// Add the surplus after the next allowed start time
				return wait + nextStart.getTimeInMillis() - end.getTimeInMillis();
			}
		} else {
			// Now is after the stop time

			if (suggested.before(nextStart)) {
				// Suggested is still in the forbidden window
				// Shift to the beginning of the allowed window
				return wait + nextStart.getTimeInMillis() - now.getTimeInMillis();
			} else {
				// Suggested is in the next allowed window
				// Accept that
				return wait;
			}
		}
	}
}
