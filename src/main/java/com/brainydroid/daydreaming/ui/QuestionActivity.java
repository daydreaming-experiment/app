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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationCallback;
import com.brainydroid.daydreaming.background.LocationServiceConnection;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.db.*;
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

            // Debug
            if (Config.LOGD) {
                Log.d(TAG, "[fn] newInstance");
            }

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

            // Debug
            if (Config.LOGD) {
                Log.d(TAG, "[fn] onCreateDialog");
            }

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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate(savedInstanceState);

        initVars();
        setChrome();
        questionViewAdapter.inflate(isFirstQuestion());
    }

    @Override
    public void onStart() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStart");
        }

        super.onStart();

        poll.setStatus(Poll.STATUS_RUNNING);
        question.setStatus(Question.STATUS_ASKED);

        if (statusManager.isDataAndLocationEnabled()) {
            startListeningTasks();
        }
    }

    @Override
    public void onStop() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onStop");
        }

        super.onStop();
        if (!isContinuingOrFinishing) {
            dismissPoll();
        }

        locationServiceConnection.clearQuestionLocationCallback();
        // the LocationService finishes if nobody else has listeners registered
        locationServiceConnection.unbindLocationService();
    }

    @Override
    public void onBackPressed() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onBackPressed");
        }

        if (!isRepeatingBack()) {
            Toast.makeText(this, getString(R.string.questionActivity_catch_key),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
        super.onBackPressed();
    }

    private boolean isRepeatingBack() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isRepeatingBack");
        }

        long now = SystemClock.elapsedRealtime();
        boolean ret = (lastBackTime != 0) && (lastBackTime + BACK_REPEAT_DELAY >= now);
        lastBackTime = now;
        return ret;
    }

    private void initVars() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] initVars");
        }

        Intent intent = getIntent();
        pollId = intent.getIntExtra(EXTRA_POLL_ID, -1);
        poll = pollsStorage.getPoll(pollId);
        questionIndex = intent.getIntExtra(EXTRA_QUESTION_INDEX, -1);
        question = poll.getQuestionByIndex(questionIndex);
        nQuestions = poll.getLength();
        questionViewAdapter = questionViewAdapterFactory.create(question,
                questionLinearLayout);
    }

    private void setChrome() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setChrome");
        }

        setTitle(getString(R.string.app_name) + " " + (questionIndex + 1) + "/" + nQuestions);

        if (!isFirstQuestion()) {
            TextView welcomeText = (TextView)questionLinearLayout.findViewById(R.id.question_welcomeText);
            questionLinearLayout.removeView(welcomeText);

            if (isLastQuestion()) {
                nextButton.setText(nextButtonFinishText);
            }
        }
    }

    private void startListeningTasks() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startListeningTasks");
        }

        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] (locationCallback) onLocationReceived");
                }

                question.setLocation(location);
            }

        };

        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {

                // Debug
                if (Config.LOGD) {
                    Log.d(TAG, "[fn] (sntpCallback) onTimeReceived");
                }

                if (sntpClient != null) {
                    question.setTimestamp(sntpClient.getNow());
                }
            }

        };

        locationServiceConnection.setQuestionLocationCallback(locationCallback);

        sntpClient.asyncRequestTime(sntpCallback);

        if (!statusManager.isLocationServiceRunning()) {
            locationServiceConnection.bindLocationService();
            locationServiceConnection.startLocationService();
        } else {
            locationServiceConnection.bindLocationService();
        }
    }

    public void onClick_nextButton(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_nextButton");
        }

        if (questionViewAdapter.validate()) {
            questionViewAdapter.saveAnswer();
            question.setStatus(Question.STATUS_ANSWERED);

            if (isLastQuestion()) {

                finishPoll();

            } else {

                if (statusManager.isDataAndLocationEnabled()) {
                    launchNextQuestion();
                } else {
                    launchLocationAlertDialog();
                }

            }
        }
    }

    private void launchLocationAlertDialog() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchLocationAlertDialog");
        }

        int titleId;
        int textId;

        if (!statusManager.isNetworkLocEnabled()) {

            if (!statusManager.isDataEnabled()) {
                titleId = R.string.locationAlert_title_location_and_data;
                textId = R.string.locationAlert_text_location_and_data;
            } else {
                titleId = R.string.locationAlert_title_location;
                textId = R.string.locationAlert_text_location;
            }

        } else {

            titleId = R.string.locationAlert_title_data;
            textId = R.string.locationAlert_text_data;

        }

        DialogFragment locationAlert = LocationAlertDialogFragment.newInstance(
                titleId, textId, R.string.locationAlert_button_settings);
        locationAlert.show(getSupportFragmentManager(), "locationAlert");
    }

    private void launchNextQuestion() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchNextQuestion");
        }

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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] launchSettings");
        }

        Intent settingsIntent;

        if (!statusManager.isNetworkLocEnabled()) {

            settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        } else {

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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] dismissPoll");
        }

        question.setStatus(Question.STATUS_ASKED_DISMISSED);
        poll.setStatus(Poll.STATUS_PARTIALLY_COMPLETED);

        startSyncService();
    }

    private void finishPoll() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] finishPoll");
        }

        setIsContinuingOrFinishing();

        Toast.makeText(this, getString(R.string.question_thank_you), Toast.LENGTH_SHORT).show();
        poll.setStatus(Poll.STATUS_COMPLETED);

        startSyncService();
        finish();
    }

    private boolean isLastQuestion() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isLastQuestion");
        }

        return questionIndex == nQuestions - 1;
    }

    private boolean isFirstQuestion() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isFirstQuestion");
        }

        return questionIndex == 0;
    }

    private void setIsContinuingOrFinishing() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setIsContinuingOrFinishing");
        }

        isContinuingOrFinishing = true;
    }

    private void startSyncService() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] startSyncService");
        }

        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

}
