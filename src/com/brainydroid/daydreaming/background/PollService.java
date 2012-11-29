package com.brainydroid.daydreaming.background;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionActivity;

public class PollService extends Service {

	private static String TAG = "PollService";

	public static String POLL_DEBUGGING = "pollDebugging";
	public static String POLL_CLEAR_NOTIFICATION_ID = "pollClearNotificationId";
	public static int EXPIRY_DELAY = 5 * 60 * 1000; // 5 minutes (in milliseconds)

	private static int nQuestionsPerPoll = 3;

	private NotificationManager notificationManager;
	private PollsStorage pollsStorage;
	private AlarmManager alarmManager;

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

		initVars();
		pollsStorage.cleanPolls();

		int pollClearNotificationId = intent.getIntExtra(POLL_CLEAR_NOTIFICATION_ID, -1);
		if (pollClearNotificationId == -1) {
			createAndLaunchPoll(intent.getBooleanExtra(POLL_DEBUGGING, false));
		} else {
			clearPollNotification(pollClearNotificationId);
		}
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

	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		pollsStorage = PollsStorage.getInstance(this);
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}

	private void clearPollNotification(int id) {
		notificationManager.cancel(id);
		Poll poll = pollsStorage.getPoll(id);
		poll.setStatus(Poll.STATUS_EXPIRED);
	}

	private void createAndLaunchPoll(boolean debugging) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createAndLaunchPoll");
		}

		Poll poll = createPoll();
		if (!debugging) {
			startSchedulerService();
		}
		notifyPoll(poll);
	}

	private Intent createPollIntent(Poll poll) {
		return createPollIntent(poll, false);
	}

	private Intent createPollIntent(Poll poll, boolean startNow) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createPollIntent");
		}

		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(QuestionActivity.EXTRA_POLL_ID, poll.getId());
		intent.putExtra(QuestionActivity.EXTRA_QUESTION_INDEX, 0);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		return intent;
	}

	private void notifyPoll(Poll poll) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] notifyPoll");
		}

		// Build notification
		Intent intent = createPollIntent(poll);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new NotificationCompat.Builder(this)
		.setTicker(getString(R.string.pollNotification_ticker))
		.setContentTitle(getString(R.string.pollNotification_title))
		.setContentText(getString(R.string.pollNotification_text))
		.setContentIntent(contentIntent)
		.setSmallIcon(android.R.drawable.ic_dialog_info)
		.setAutoCancel(true)
		.build();

		notificationManager.notify(poll.getId(), notification);

		// Build notification expirer
		Intent expirerIntent = new Intent(this, PollService.class);
		expirerIntent.putExtra(POLL_CLEAR_NOTIFICATION_ID, poll.getId());
		PendingIntent expirerPendingIntent = PendingIntent.getService(this, 0,
				expirerIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
		long expiry = SystemClock.elapsedRealtime() + EXPIRY_DELAY;
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				expiry, expirerPendingIntent);
	}

	private Poll createPoll() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createPoll");
		}

		ArrayList<Poll> pendingPolls = pollsStorage.getPendingPolls();
		Poll poll;

		if (pendingPolls != null) {
			poll = pendingPolls.get(0);
		} else {
			poll = Poll.create(this, nQuestionsPerPoll);
		}

		poll.setStatus(Poll.STATUS_PENDING);
		poll.save();
		return poll;
	}

	private void startSchedulerService() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] startSchedulerService");
		}

		Intent schedulerIntent = new Intent(this, SchedulerService.class);
		startService(schedulerIntent);
	}
}
