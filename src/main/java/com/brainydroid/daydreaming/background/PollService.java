package com.brainydroid.daydreaming.background;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionActivity;

public class PollService extends Service {

	private static String TAG = "PollService";

	//	public static String POLL_EXPIRE_ID = "pollExpireId";
	//	public static int EXPIRY_DELAY = 5 * 60 * 1000; // 5 minutes (in milliseconds)

	private static int nQuestionsPerPoll = 3;

	private NotificationManager notificationManager;
	private PollsStorage pollsStorage;
	//	private AlarmManager alarmManager;
	private SharedPreferences sharedPrefs;

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

		//		int pollExpireId = intent.getIntExtra(POLL_EXPIRE_ID, -1);
		//		if (pollExpireId == -1) {
		createAndLaunchPoll();
		//		} else {
		//			expirePoll(pollExpireId);
		//		}
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
		//		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	//	private void expirePoll(int id) {
	//
	//		// Debug
	//		if (Config.LOGD){
	//			Log.d(TAG, "[fn] expirePoll");
	//		}
	//
	//		notificationManager.cancel(id);
	//		Poll poll = pollsStorage.getPoll(id);
	//		if (poll != null) {
	//			poll.setStatus(Poll.STATUS_EXPIRED);
	//		}
	//	}

	private void createAndLaunchPoll() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createAndLaunchPoll");
		}

		Poll poll = createPoll();
		startSchedulerService();
		notifyPoll(poll);
	}

	private Intent createPollIntent(Poll poll) {

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
				PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		int flags = 0;
		if (sharedPrefs.getBoolean("notification_blink_key", true)) {
			flags |= Notification.DEFAULT_LIGHTS;
		}

		if (sharedPrefs.getBoolean("notification_vibrator_key", true)) {
			flags |= Notification.DEFAULT_VIBRATE;
		}

		if (sharedPrefs.getBoolean("notification_sound_key", true)) {
			flags |= Notification.DEFAULT_SOUND;
		}

		Notification notification = new NotificationCompat.Builder(this)
		.setTicker(getString(R.string.pollNotification_ticker))
		.setContentTitle(getString(R.string.pollNotification_title))
		.setContentText(getString(R.string.pollNotification_text))
		.setContentIntent(contentIntent)
		.setSmallIcon(android.R.drawable.ic_dialog_info)
		.setAutoCancel(true)
		.setOnlyAlertOnce(true)
		.setDefaults(flags)
		.build();

		notificationManager.notify(poll.getId(), notification);

		// Build notification expirer
		//		Intent expirerIntent = new Intent(this, PollService.class);
		//		expirerIntent.putExtra(POLL_EXPIRE_ID, poll.getId());
		//		PendingIntent expirerPendingIntent = PendingIntent.getService(this, 0,
		//				expirerIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		//		long expiry = SystemClock.elapsedRealtime() + EXPIRY_DELAY;
		//		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		//				expiry, expirerPendingIntent);
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
		poll.setNotificationTimestamp(SystemClock.elapsedRealtime());
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
