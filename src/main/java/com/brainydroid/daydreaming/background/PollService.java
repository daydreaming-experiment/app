package com.brainydroid.daydreaming.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.ui.QuestionActivity;
import com.google.inject.Inject;
import roboguice.service.RoboService;

import java.util.ArrayList;

/**
 * Create and populate a {@link Poll}, then notify it to the user.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class PollService extends RoboService {

    private static String TAG = "PollService";

    /** Number of questions per {@link Poll} */
    public static int N_QUESTIONS_PER_POLL = 3;

    @Inject NotificationManager notificationManager;
    @Inject PollsStorage pollsStorage;
    @Inject SharedPreferences sharedPreferences;
    @Inject Poll poll;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "PollService started");

        super.onStartCommand(intent, flags, startId);

        // Populate and notify the poll
        populatePoll();
        notifyPoll();

        // Schedule the next poll
        startSchedulerService();

        stopSelf();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Don't allow binding
        return null;
    }

    /**
     * Create the {@link QuestionActivity} {@code Intent}.
     *
     * @return An {@code Intent} to launch our {@link Poll}
     */
    private Intent createPollIntent() {
        Logger.d(TAG, "Creating poll Intent");

        Intent intent = new Intent(this, QuestionActivity.class);

        // Set the id of the poll to start
        intent.putExtra(QuestionActivity.EXTRA_POLL_ID, poll.getId());

        // Set the index of the question to open
        intent.putExtra(QuestionActivity.EXTRA_QUESTION_INDEX, 0);

        // Create a new task and don't show up in various Android UI
        // screens
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }

    /**
     * Notify our poll to the user.
     */
    private void notifyPoll() {
        Logger.d(TAG, "Notifying poll");

        // Create the PendingIntent
        Intent intent = createPollIntent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT |
                PendingIntent.FLAG_ONE_SHOT);

        int flags = 0;

        // Should we flash the LED?
        if (sharedPreferences.getBoolean("notification_blink_key", true)) {
            Logger.v(TAG, "Activating lights");
            flags |= Notification.DEFAULT_LIGHTS;
        }

        // Should we vibrate?
        if (sharedPreferences.getBoolean("notification_vibrator_key",
                true)) {
            Logger.v(TAG, "Activating vibration");
            flags |= Notification.DEFAULT_VIBRATE;
        }

        // Should we beep?
        if (sharedPreferences.getBoolean("notification_sound_key", true)) {
            Logger.v(TAG, "Activating sound");
            flags |= Notification.DEFAULT_SOUND;
        }

        // Create our notification
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

        // And send it to the system
        notificationManager.notify(poll.getId(), notification);
    }

    /**
     * Fill our {@link Poll} with questions.
     */
    private void populatePoll() {
        Logger.d(TAG, "Populating poll with questions");

        // Pick from already created polls that were never shown to the
        // user, if there are any
        ArrayList<Poll> pendingPolls = pollsStorage.getPendingPolls();

        if (pendingPolls != null) {
            Logger.d(TAG, "Reusing previously pending poll");
            poll = pendingPolls.get(0);
        } else {
            Logger.d(TAG, "Sampling new questions for poll");
            poll.populateQuestions(N_QUESTIONS_PER_POLL);
        }

        // Update the poll's status
        Logger.d(TAG, "Setting poll status and timestamp, and saving");
        poll.setStatus(Poll.STATUS_PENDING);
        poll.setNotificationTimestamp(SystemClock.elapsedRealtime());
        poll.save();
    }

    /**
     * Start {@link SchedulerService} for the next {@link Poll}.
     */
    private void startSchedulerService() {
        Logger.d(TAG, "Starting SchedulerService");

        Intent schedulerIntent = new Intent(this, SchedulerService.class);
        startService(schedulerIntent);
    }

}
