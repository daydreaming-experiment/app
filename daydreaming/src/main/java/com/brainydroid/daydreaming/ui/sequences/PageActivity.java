package com.brainydroid.daydreaming.ui.sequences;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.LocationCallback;
import com.brainydroid.daydreaming.background.LocationServiceConnection;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.background.SyncService;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.network.SntpClient;
import com.brainydroid.daydreaming.network.SntpClientCallback;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.inject.Inject;

import java.util.Calendar;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_page)
public class PageActivity extends RoboFragmentActivity {

    private static String TAG = "PageActivity";

    public static String EXTRA_SEQUENCE_ID = "sequenceId";

    public static long BACK_REPEAT_DELAY = 2 * 1000; // 2 seconds, in milliseconds

    private int sequenceId;
    private Sequence sequence;
    private Page page;
    private long lastBackTime = 0;
    private boolean isContinuingOrFinishing = false;
    @Inject private PageViewAdapter pageViewAdapter;

    @InjectView(R.id.page_linearLayout) LinearLayout pageLinearLayout;

    @InjectView(R.id.page_nextButton) ImageButton nextButton;
    @InjectView(R.id.page_finishButton) ImageButton finishButton;

    @Inject LocationServiceConnection locationServiceConnection;
    @Inject SequencesStorage sequencesStorage;
    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        checkTestMode();
        super.onCreate(savedInstanceState);

        initVars();
        setChrome();
        pageViewAdapter.inflate(pageLinearLayout);
        setRobotoFont();

        // If this is a probe and we're at the first page, reschedule so as not
        // to have a new probe appear in the middle of this one
        if (sequence.getType().equals(Sequence.TYPE_PROBE) && page.isFirstOfSequence()) {
            startSchedulerService();
        }
    }

    @Override
    public void onResume() {
        Logger.d(TAG, "Resuming");
        super.onResume();

        sequence.retainSaves();
        sequence.setStatus(Sequence.STATUS_RUNNING);
        page.setStatus(Page.STATUS_ASKED);
        page.setSystemTimestamp(Calendar.getInstance().getTimeInMillis());
        sequence.flushSaves();

        // Retain everything until onPause()
        sequence.retainSaves();

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
                    "the sequence -> stopping the sequence");
            stopSequence();
        }

        // Save everything to DB
        sequence.flushSaves();

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
        sequenceId = intent.getIntExtra(EXTRA_SEQUENCE_ID, -1);
        if (sequenceId == -1) {
            String msg = "Could not get EXTRA_SEQUENCE_ID";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        sequence = sequencesStorage.get(sequenceId);
        page = sequence.getCurrentPage();
        pageViewAdapter.setPage(page);
    }

    private void setChrome() {
        Logger.d(TAG, "Setting chrome");

        if (page.isLastOfSequence()) {
            Logger.d(TAG, "Last page -> setting finish button text");
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
            finishButton.setClickable(true);
        }
    }

    private void setIsContinuingOrFinishing() {
        isContinuingOrFinishing = true;
    }

    private void stopSequence() {
        Logger.i(TAG, "Stopping sequence");

        sequence.setStatus(Sequence.STATUS_PARTIALLY_COMPLETED);

        Logger.i(TAG, "Starting sync service to sync answers");
        startSyncService();

        Logger.i(TAG, "Finishing activity");
        finish();
    }

    private void startSyncService() {
        Logger.d(TAG, "Starting SyncService");

        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

    private void startSchedulerService() {
        Logger.d(TAG, "Starting SchedulerService");

        Intent schedulerIntent = new Intent(this, SchedulerService.class);
        startService(schedulerIntent);
    }

    private void startListeningTasks() {
        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {
                Logger.i(TAG, "Received location for page, setting it");
                page.setLocation(location);
            }

        };

        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    page.setNtpTimestamp(sntpClient.getNow());
                    Logger.i(TAG, "Received and saved NTP time for page");
                } else {
                    Logger.e(TAG, "Received successful NTP request but sntpClient is null");
                }
            }

        };

        locationServiceConnection.setQuestionLocationCallback(locationCallback);

        Logger.i(TAG, "Launching NTP request");
        sntpClient.asyncRequestTime(sntpCallback);

        if (!statusManager.isLocationServiceRunning()) {
            Logger.i(TAG, "LocationService not running -> binding and starting");
            locationServiceConnection.bindLocationService();
            locationServiceConnection.startLocationService();
        } else {
            Logger.i(TAG, "LocationService running -> binding (but not starting)");
            locationServiceConnection.bindLocationService();
        }
    }

    public void onClick_nextButton(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked");

        if (pageViewAdapter.validate()) {
            Logger.i(TAG, "Page validation succeeded, " +
                    "setting page status to answered");
            pageViewAdapter.saveAnswers();
            page.setStatus(Page.STATUS_ANSWERED);

            setIsContinuingOrFinishing();
            if (page.isLastOfSequence()) {
                Logger.d(TAG, "Last page -> finishing sequence");
                finishSequence();
            } else {
                Logger.d(TAG, "Launching next page");
                launchNextPage();
            }
        }
    }

    private void launchNextPage() {
        Logger.d(TAG, "Launching next page");

        Intent intent = new Intent(this, PageActivity.class);
        intent.putExtra(EXTRA_SEQUENCE_ID, sequenceId);
        startActivity(intent);

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    private void finishSequence() {
        Logger.i(TAG, "Finishing sequence");

        Toast.makeText(this, getString(R.string.page_thank_you), Toast.LENGTH_SHORT).show();
        sequence.setStatus(Sequence.STATUS_COMPLETED);

        Logger.i(TAG, "Starting sync service to sync answers, and finishing self");
        startSyncService();
        finish();
    }

    public void setRobotoFont() {
        Logger.v(TAG, "Setting Roboto font");
        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
    }

    public void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.daydreamingTheme);
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.daydreamingTestTheme);
        }
    }
}
