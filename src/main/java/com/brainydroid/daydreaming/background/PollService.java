package com.brainydroid.daydreaming.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionActivity;
import com.google.inject.Inject;
import roboguice.service.RoboService;

import java.util.ArrayList;

public class PollService extends RoboService {

	private static String TAG = "PollService";

	public static int N_QUESTIONS_PER_POLL = 3;

    @Inject NotificationManager notificationManager;
    @Inject PollsStorage pollsStorage;
    @Inject SharedPreferences sharedPreferences;

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

		pollsStorage.cleanPolls();
		createAndLaunchPoll();
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
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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
		if (sharedPreferences.getBoolean("notification_blink_key", true)) {
			flags |= Notification.DEFAULT_LIGHTS;
		}

		if (sharedPreferences.getBoolean("notification_vibrator_key", true)) {
			flags |= Notification.DEFAULT_VIBRATE;
		}

		if (sharedPreferences.getBoolean("notification_sound_key", true)) {
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
			poll = Poll.create(N_QUESTIONS_PER_POLL);
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
