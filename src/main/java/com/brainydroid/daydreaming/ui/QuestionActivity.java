package com.brainydroid.daydreaming.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.*;
import com.brainydroid.daydreaming.db.Poll;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.Question;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_question)
public class QuestionActivity extends RoboSherlockFragmentActivity {

    private static String TAG = "QuestionActivity";

    public static String EXTRA_POLL_ID = "pollId";
    public static String EXTRA_QUESTION_INDEX = "questionIndex";

    public static long BACK_REPEAT_DELAY = 2 * 1000; // 2 seconds, in milliseconds

    private int pollId;
    private Poll poll;
    private int questionIndex;
    private Question question;
    private int nQuestions;
    private boolean isContinuingOrFinishing = false;
    private long lastBackTime = 0;
    private IQuestionViewAdapter questionViewAdapter;

    @InjectView(R.id.question_linearLayout) LinearLayout questionLinearLayout;
    @InjectView(R.id.question_nextButton) Button nextButton;
    @InjectResource(R.string.question_button_finish) String nextButtonFinishText;

    @Inject LocationServiceConnection locationServiceConnection;
    @Inject PollsStorage pollsStorage;
    @Inject StatusManager statusManager;
    @Inject QuestionViewAdapterFactory questionViewAdapterFactory;
    @Inject SntpClient sntpClient;

    public static class LocationAlertDialogFragment extends SherlockDialogFragment {

        private static String TAG = "LocationAlertDialogFragment";

        public static LocationAlertDialogFragment newInstance(int title, int text, int posText) {
            LocationAlertDialogFragment frag = new LocationAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putInt("text", text);
            args.putInt("posText", posText);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Logger.d(TAG, "Creating location alert dialog");

            int title = getArguments().getInt("title");
            int text = getArguments().getInt("text");
            int posText = getArguments().getInt("posText");

            AlertDialog.Builder alertSettings = new AlertDialog.Builder(getSherlockActivity())
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton(posText,
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((QuestionActivity)getSherlockActivity()).launchSettings();
                }
            }).setIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    R.drawable.ic_action_about_holo_light : R.drawable.ic_action_about_holo_dark);

            return alertSettings.create();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        initVars();
        setChrome();
        questionViewAdapter.inflate(isFirstQuestion());
    }

    @Override
    public void onResume() {
        Logger.d(TAG, "Resuming");
        super.onResume();

        poll.setStatus(Poll.STATUS_RUNNING);
        question.setStatus(Question.STATUS_ASKED);

        if (statusManager.isDataAndLocationEnabled()) {
            Logger.i(TAG, "Data and location enabled -> starting listening " +
                    "tasks");
            startListeningTasks();
        } else {
            Logger.i(TAG, "No data or no location -> not starting listening" +
                    " tasks");
        }
    }

    @Override
    public void onPause() {
        Logger.d(TAG, "Pausing");
        super.onPause();
        if (!isContinuingOrFinishing) {
            Logger.d(TAG, "We're not moving to next question or finishing " +
                    "the poll -> dismissing");
            dismissPoll();
        }

        Logger.d(TAG, "Clearing LocationService callback and unbinding");
        locationServiceConnection.clearQuestionLocationCallback();
        // the LocationService finishes if nobody else has listeners registered
        locationServiceConnection.unbindLocationService();
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed");
        if (!isRepeatingBack()) {
            Toast.makeText(this, getString(R.string.questionActivity_catch_key),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        super.onBackPressed();
    }

    private boolean isRepeatingBack() {
        long now = SystemClock.elapsedRealtime();
        boolean ret = (lastBackTime != 0) && (lastBackTime + BACK_REPEAT_DELAY >= now);
        lastBackTime = now;
        return ret;
    }

    private void initVars() {
        Logger.d(TAG, "Initializing variables");
        Intent intent = getIntent();
        pollId = intent.getIntExtra(EXTRA_POLL_ID, -1);
        poll = pollsStorage.get(pollId);
        questionIndex = intent.getIntExtra(EXTRA_QUESTION_INDEX, -1);
        question = poll.getQuestionByIndex(questionIndex);
        nQuestions = poll.getLength();
        questionViewAdapter = questionViewAdapterFactory.create(question,
                questionLinearLayout);
    }

    private void setChrome() {
        Logger.d(TAG, "Setting chrome");

        setTitle(getString(R.string.app_name) + " " + (questionIndex + 1) + "/" + nQuestions);

        if (!isFirstQuestion()) {
            Logger.d(TAG, "Not the first question -> removing welcome text");
            TextView welcomeText = (TextView)questionLinearLayout.findViewById(R.id.question_welcomeText);
            questionLinearLayout.removeView(welcomeText);

            if (isLastQuestion()) {
                Logger.d(TAG, "Last question -> setting finish button text");
                nextButton.setText(nextButtonFinishText);
            }
        }
    }

    private void startListeningTasks() {
        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {
                Logger.i(TAG, "Received location for question, setting it");
                question.setLocation(location);
            }

        };

        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    question.setTimestamp(sntpClient.getNow());
                    Logger.i(TAG, "Received and saved NTP time for " +
                            "question");
                } else {
                    Logger.e(TAG, "Received successful NTP request but " +
                            "sntpClient is null");
                }
            }

        };

        locationServiceConnection.setQuestionLocationCallback(locationCallback);

        Logger.i(TAG, "Launching NTP request");
        sntpClient.asyncRequestTime(sntpCallback);

        if (!statusManager.isLocationServiceRunning()) {
            Logger.i(TAG, "LocationService not running -> binding and " +
                    "starting");
            locationServiceConnection.bindLocationService();
            locationServiceConnection.startLocationService();
        } else {
            Logger.i(TAG, "LocationService running -> binding (but not " +
                    "starting)");
            locationServiceConnection.bindLocationService();
        }
    }

    public void onClick_nextButton(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked");

        if (questionViewAdapter.validate()) {
            Logger.i(TAG, "Question validation succeeded, " +
                    "setting question status to answered");
            questionViewAdapter.saveAnswer();
            question.setStatus(Question.STATUS_ANSWERED);

            if (isLastQuestion()) {
                Logger.d(TAG, "Last question -> finishing poll");
                finishPoll();
            } else {
                if (statusManager.isDataAndLocationEnabled()) {
                    Logger.d(TAG, "Data and location enabled -> launching " +
                            "next question");
                    launchNextQuestion();
                } else {
                    Logger.d(TAG, "Either data or location disabled -> " +
                            "launching alert dialog");
                    launchLocationAlertDialog();
                }
            }
        }
    }

    private void launchLocationAlertDialog() {
        Logger.d(TAG, "Launching alert dialog");

        int titleId;
        int textId;

        if (!statusManager.isNetworkLocEnabled()) {
            if (!statusManager.isDataEnabled()) {
                Logger.d(TAG, "Network location and data disabled");
                titleId = R.string.locationAlert_title_location_and_data;
                textId = R.string.locationAlert_text_location_and_data;
            } else {
                Logger.d(TAG, "Network location disabled, data enabled");
                titleId = R.string.locationAlert_title_location;
                textId = R.string.locationAlert_text_location;
            }
        } else {
            Logger.d(TAG, "Network location enabled, location disabled");
            titleId = R.string.locationAlert_title_data;
            textId = R.string.locationAlert_text_data;
        }

        DialogFragment locationAlert = LocationAlertDialogFragment.newInstance(
                titleId, textId, R.string.locationAlert_button_settings);
        locationAlert.show(getSupportFragmentManager(), "locationAlert");
    }

    private void launchNextQuestion() {
        Logger.d(TAG, "Launching next question");

        setIsContinuingOrFinishing();

        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(EXTRA_POLL_ID, pollId);
        intent.putExtra(EXTRA_QUESTION_INDEX, questionIndex + 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    private void launchSettings() {
        Intent settingsIntent;

        if (!statusManager.isNetworkLocEnabled()) {
            Logger.d(TAG, "Launching location settings");
            settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        } else {
            Logger.d(TAG, "Launching data settings");
            settingsIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
                settingsIntent.setComponent(cName);
            }

            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }

        startActivity(settingsIntent);
    }

    private void dismissPoll() {
        Logger.i(TAG, "Dismissing poll");

        question.setStatus(Question.STATUS_ASKED_DISMISSED);
        poll.setStatus(Poll.STATUS_PARTIALLY_COMPLETED);

        Logger.i(TAG, "Starting sync service to sync answers");
        startSyncService();
    }

    private void finishPoll() {
        Logger.i(TAG, "Finishing poll");

        setIsContinuingOrFinishing();

        Toast.makeText(this, getString(R.string.question_thank_you), Toast.LENGTH_SHORT).show();
        poll.setStatus(Poll.STATUS_COMPLETED);

        Logger.i(TAG, "Starting sync service to sync answers, " +
                "and finishing self");
        startSyncService();
        finish();
    }

    private boolean isLastQuestion() {
        return questionIndex == nQuestions - 1;
    }

    private boolean isFirstQuestion() {
        return questionIndex == 0;
    }

    private void setIsContinuingOrFinishing() {
        isContinuingOrFinishing = true;
    }

    private void startSyncService() {
        Logger.d(TAG, "Starting SyncService");

        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

}
