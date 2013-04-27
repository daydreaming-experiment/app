package com.brainydroid.daydreaming.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import roboguice.service.RoboService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class SchedulerService extends RoboService {

	private static String TAG = "SchedulerService";

	public static String SCHEDULER_DEBUGGING = "schedulerDebugging";

	public static long DEBUG_DELAY = 5 * 1000; // 5 seconds (in milliseconds)
	public static long SAMPLE_TIME_MEAN = 2 * 60 * 60 * 1000; // 2 hours (in milliseconds)
	public static long SAMPLE_TIME_STD = 1 * 60 * 60 * 1000; // 1 hour (in milliseconds)
	public static long SAMPLE_TIME_MIN = 10 * 60 * 1000; // 10 minutes (in milliseconds)

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Inject SharedPreferences sharedPreferences;
	@Inject Random random;
	@Inject AlarmManager alarmManager;

	@Override
	public void onCreate() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onStartCommand");
		}

		super.onStartCommand(intent, flags, startId);

		// Since the poll gets created when the notification shows up, there's a good chance
		// the questions will have finished updating (if internet connection is available)
		// before poll creation.
		startSyncService();
		schedulePoll(intent.getBooleanExtra(SCHEDULER_DEBUGGING, false));
		stopSelf();
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onDestroy");
		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBind");
		}

		// Don't allow binding
		return null;
	}

	private void schedulePoll(boolean debugging) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] schedulePoll");
		}

		long scheduledTime = generateTime(debugging);

		Intent intent = new Intent(this, PollService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				scheduledTime, pendingIntent);

		Calendar scheduled = Calendar.getInstance();
		long wait = scheduledTime - SystemClock.elapsedRealtime();
		scheduled.add(Calendar.MILLISECOND, (int)wait);

		long hours = wait / (60 * 60 * 1000);
		wait -= hours * 60 * 60 * 1000;
		long minutes = wait / (60 * 1000);
		wait -= minutes * 60 * 1000;
		long seconds = wait / 1000;

		String target = sdf.format(scheduled.getTime());

		// Info
		Log.i(TAG, "poll scheduled in " + hours + " hours, " +
				minutes + " minutes, and " + seconds + " seconds (i.e. " + target + ")");

		if (Config.TOASTI) {
			Toast.makeText(this, "New poll scheduled at " + target, Toast.LENGTH_LONG).show();
		}
	}

	private void startSyncService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] startSyncService");
		}

		Intent syncIntent = new Intent(this, SyncService.class);
		startService(syncIntent);
	}

	private long generateTime(boolean debugging) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] generateTime");
		}

		if (debugging) {
			return SystemClock.elapsedRealtime() + DEBUG_DELAY;
		}

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

		return SystemClock.elapsedRealtime() + shiftWaitTime(wait);
	}

	private long shiftWaitTime(long wait) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] shiftWaitTime");
		}

		String startString = sharedPreferences.getString("time_window_lb_key",
				getString(R.pref.settings_time_window_lb_default));
		String endString = sharedPreferences.getString("time_window_ub_key",
				getString(R.pref.settings_time_window_ub_default));

		int startHour = Util.getHour(startString);
		int startMinute = Util.getMinute(startString);
		int endHour = Util.getHour(endString);
		int endMinute = Util.getMinute(endString);

		// Debug
		if (Config.LOGD){
			Log.d(TAG, "allowed start: " + startHour + ":" + startMinute);
			Log.d(TAG, "allowed end: " + endHour + ":" + endMinute);
		}

		Calendar now = Calendar.getInstance();

		Calendar suggested = (Calendar)now.clone();
		suggested.add(Calendar.MILLISECOND, (int)wait);

		Calendar start = (Calendar)now.clone();
		start.set(Calendar.HOUR_OF_DAY, startHour);
		start.set(Calendar.MINUTE, startMinute);

		Calendar end = (Calendar)now.clone();
		end.set(Calendar.HOUR_OF_DAY, endHour);
		end.set(Calendar.MINUTE, endMinute);
		if (endHour * 60 + endMinute < startHour * 60 + startMinute) {
			// The time window goes through midnight. Account for this in `end`
			end.add(Calendar.DAY_OF_YEAR, 1);
		}

		Calendar nextStart = (Calendar)start.clone();
		nextStart.add(Calendar.DAY_OF_YEAR, 1);

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "now is: " + sdf.format(now.getTime()));
			Log.d(TAG, "suggested is: " + sdf.format(suggested.getTime()));
			Log.d(TAG, "allowed start is: " + sdf.format(start.getTime()));
			Log.d(TAG, "allowed end is: " + sdf.format(end.getTime()));
			Log.d(TAG, "allowed next start is: " + sdf.format(nextStart.getTime()));
		}

		long shift;

		if (now.before(start)) {
			// Now is before the starting time for notifications

			if (suggested.before(start)) {
				// Suggested schedule is also before start
				// Shift to the beginning of the allowed window
				shift = start.getTimeInMillis() - now.getTimeInMillis();

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "now, suggested < start");
				}
			} else {
				// Suggested schedule falls in the allowed window
				// Accept that
				shift = 0;

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "now < start < suggested");
				}
			}

		} else if (now.before(end)) {
			// Now is in the allowed window

			if (suggested.before(end) || suggested.after(nextStart)) {
				// Suggested schedule falls in the allowed window (today or tomorrow)
				// Accept that
				shift = 0;

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "start < now, suggested < end OR now < end, nextStart < suggested");
				}
			} else {
				// Suggested schedule falls outside the allowed window
				// Add the surplus after the next allowed start time
				shift = nextStart.getTimeInMillis() - end.getTimeInMillis();

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "start < now < end < suggested");
				}
			}
		} else {
			// Now is after the stop time

			if (suggested.before(nextStart)) {
				// Suggested is still in the forbidden window
				// Shift to the beginning of the allowed window
				shift = nextStart.getTimeInMillis() - now.getTimeInMillis();

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "end < now, suggested < nextStart");
				}
			} else {
				// Suggested is in the next allowed window
				// Accept that
				shift = 0;

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "end < now < nextStart < suggested");
				}
			}
		}

		// Debug
		if (Config.LOGD) {
			long tmpShift = shift;
			long hours = tmpShift / (60 * 60 * 1000);
			tmpShift -= hours * 60 * 60 * 1000;
			long minutes = tmpShift / (60 * 1000);
			tmpShift -= minutes * 60 * 1000;
			long seconds = tmpShift / 1000;
			Log.d(TAG, "shift: " + hours + ":" + minutes + ":" + seconds);
		}

		return wait + shift;
	}

}
