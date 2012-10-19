package com.brainydroid.daydreaming.background;


import android.app.AlarmManager;
import android.content.Context;

public class ScheduleManager {

	private static ScheduleManager smInstance = null;

	private final StatusManager status;
	private final Context context;
	private final AlarmManager alarmManager;

	public static ScheduleManager getInstance(Context c) {
		if (smInstance == null) {
			smInstance = new ScheduleManager(c);
		}
		return smInstance;
	}

	private ScheduleManager(Context c) {
		context = c.getApplicationContext();
		status = StatusManager.getInstance(c);
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	private void schedulePoll() {
		//		Toast.makeText(context, "Scheduling alarm", Toast.LENGTH_SHORT).show();
		//		Intent intent = new Intent(context, DashboardActivity.class);
		//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		//				intent, PendingIntent.FLAG_ONE_SHOT);
		//		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		//				SystemClock.elapsedRealtime() + 30*1000, pendingIntent);
	}

	// Check that this day is scheduled. If it's the end of this day, or between days, reschedule.
	//	public void updateSchedule() {}
}
