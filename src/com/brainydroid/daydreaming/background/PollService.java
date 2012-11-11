package com.brainydroid.daydreaming.background;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.ui.QuestionActivity;

public class PollService extends Service {

	private static String TAG = "PollService";

	public static String POLL_DEBUGGING = "pollDebugging";

	private static int nQuestionsPerPoll = 3;

	private NotificationManager notificationManager;
	private PollsStorage pollsStorage;

	@Override
	public void onCreate() {

		// Debug
		Log.d(TAG, "[fn] onCreate");

		super.onCreate();
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Debug
		Log.d(TAG, "[fn] onStartCommand");

		super.onStartCommand(intent, flags, startId);

		initVars();
		pollsStorage.cleanPolls();
		createAndLaunchPoll(intent.getBooleanExtra(POLL_DEBUGGING, false));
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

		pollsStorage = PollsStorage.getInstance(this);
	}

	private void createAndLaunchPoll(boolean startNow) {

		// Debug
		Log.d(TAG, "[fn] createAndLaunchPoll");

		Poll poll = createPoll();
		if (startNow) {
			launchPoll(poll);
		} else {
			notifyPoll(poll);
		}
	}

	private Intent createPollIntent(Poll poll) {
		return createPollIntent(poll, false);
	}

	private Intent createPollIntent(Poll poll, boolean startNow) {

		// Debug
		Log.d(TAG, "[fn] createPollIntent");

		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra(QuestionActivity.EXTRA_POLL_ID, poll.getId());
		intent.putExtra(QuestionActivity.EXTRA_QUESTION_INDEX, 0);
		intent.putExtra(POLL_DEBUGGING, startNow);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		return intent;
	}

	private void launchPoll(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] launchPoll");

		Intent intent = createPollIntent(poll, true);
		startActivity(intent);
	}

	private void notifyPoll(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] notifyPoll");

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

		// TODO: add expiry of notification and poll:
		// update poll status to Poll.STATUS_EXPIRED or Poll.STATUS_DISMISSED
		// TODO: add way to keep the notification open (after having been opened)
		// for 5 minutes. On expiry, set status to Poll.STATUS_PARTIALLY_COMPLETED

		notificationManager.notify(poll.getId(), notification);
	}

	private Poll createPoll() {

		// Debug
		Log.d(TAG, "[fn] createPoll");

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
}
