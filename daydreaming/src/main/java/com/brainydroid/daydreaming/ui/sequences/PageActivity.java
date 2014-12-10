package com.brainydroid.daydreaming.ui.sequences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.EQSchedulerService;
import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.LocationCallback;
import com.brainydroid.daydreaming.background.LocationServiceConnection;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.MQSchedulerService;
import com.brainydroid.daydreaming.background.ProbeSchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.ConsistencyException;
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
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_page)
public class PageActivity extends RoboFragmentActivity {

    private static String TAG = "PageActivity";

    public static final String EXTRA_SEQUENCE_ID = "sequenceId";
    private static final String EQ_ACTIVITY_PAGE_NAME = "usualActivities";

    private int sequenceId;
    private Sequence sequence;
    private Page currentPage;
    private boolean isContinuingOrFinishing = false;
    private boolean isTooLate = false;

    @InjectView(R.id.page_relativeLayout) RelativeLayout outerPageLayout;
    @InjectView(R.id.page_linearLayout) LinearLayout pageLinearLayout;

    @InjectView(R.id.page_nextButton) ImageButton nextButton;
    @InjectView(R.id.page_finishButton) ImageButton finishButton;

    @InjectView(R.id.page_progress_current) TextView page_index_current;
    @InjectView(R.id.page_progress_total) TextView page_index_total;

    @InjectResource(R.string.page_too_late_title) String tooLateTitle;
    @InjectResource(R.string.page_too_late_body) String tooLateBody;
    @InjectResource(R.string.page_eq_edit_activities_title) String explanationTitle;
    @InjectResource(R.string.page_eq_edit_activities_text) String explanationText;

    @Inject private PageViewAdapter pageViewAdapter;
    @Inject LocationServiceConnection locationServiceConnection;
    @Inject SequencesStorage sequencesStorage;
    @Inject StatusManager statusManager;
    @Inject SntpClient sntpClient;
    @Inject ErrorHandler errorHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        checkTestMode();
        super.onCreate(savedInstanceState);

        initVars();
        setChrome();

        pageViewAdapter.inflate(this, outerPageLayout, pageLinearLayout);

        page_index_current.setText(Integer.toString(sequence.getIndexOfPage(currentPage, !sequence.isSkipBonuses())));
        page_index_total.setText(" / " + Integer.toString(sequence.getTotalPageCount(!sequence.isSkipBonuses())));

        setRobotoFont();

        // If this is a probe that is not being re-opened, and this is the first page,
        // then if we're too late -> expire the probe.
        if (sequence.getType().equals(Sequence.TYPE_PROBE) &&
                currentPage.isFirstOfSequence() && !sequence.wasMissedOrDismissedOrPaused()) {
            checkTooLate();
        }
    }

    @Override
    public void onResume() {
        Logger.d(TAG, "Resuming");
        super.onResume();

        sequence.retainSaves();
        sequence.setStatus(Sequence.STATUS_RUNNING);
        currentPage.setStatus(Page.STATUS_ASKED);
        currentPage.setSystemTimestamp(Calendar.getInstance().getTimeInMillis());
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

        if (currentPage.getName().equals(EQ_ACTIVITY_PAGE_NAME) &&
                !statusManager.is(StatusManager.EQ_EDIT_ACTIVITIES_EXPLAINED)) {
            Logger.d(TAG, "Edition of evening activities not yet explained, showing popup");
            statusManager.set(StatusManager.EQ_EDIT_ACTIVITIES_EXPLAINED);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(false)
                    .setTitle(explanationTitle)
                    .setMessage(explanationText)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            alertBuilder.show();
        }
    }

    @Override
    public void onPause() {
        Logger.d(TAG, "Pausing");
        super.onPause();

        // BEQs and self-initiated probes don't interfere with normal scheduling.
        // Note that if the sequence is in fact finishing, this is run in finishSequence()
        // (and skipped here).
        // Finally, note that this *does* run if the PageActivity is being finished
        // because a probe was open too late.
        String sequenceType = sequence.getType();
        if (!isContinuingOrFinishing && !sequence.isSelfInitiated()
                && !(sequenceType.equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE) ||
                sequenceType.equals(Sequence.TYPE_END_QUESTIONNAIRE))) {
            startSchedulerService();
        }

        if (!isContinuingOrFinishing && !isTooLate) {
            Logger.d(TAG, "We're not finishing the sequence -> pausing it");
            if (sequence.wasMissedOrDismissedOrPaused() ||
                    !sequence.getType().equals(Sequence.TYPE_PROBE)) {
                // We were already paused before, or we're not a probe.
                // Closing this sequence definitively.
                sequence.setStatus(Sequence.STATUS_MISSED_OR_DISMISSED_OR_INCOMPLETE);
            } else {
                // Never paused before, we're allowing the user to take up again.
                sequence.setStatus(Sequence.STATUS_RECENTLY_PARTIALLY_COMPLETED);
            }

            finish();
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
        finish();
        super.onBackPressed();
    }

    private boolean checkTooLate() {
        long now = Calendar.getInstance().getTimeInMillis();
        // Add 30 seconds to the expiry delay to make sure the user didn't just click the
        // notification milliseconds before it was to expire.
        if (now - sequence.getNotificationSystemTimestamp() > Sequence.EXPIRY_DELAY + 30 * 1000) {
            if (statusManager.is(StatusManager.NOTIFICATION_EXPIRY_EXPLAINED)) {
                // We shouldn't be here: we already explained this and somehow a probe did not expire.
                // Log this error, but keep going.
                errorHandler.logError("We already explained the notification expiry, " +
                                "but somehow got to this point where we must re-explain it.",
                        new ConsistencyException());
            }

            statusManager.set(StatusManager.NOTIFICATION_EXPIRY_EXPLAINED);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(false)
                    .setTitle(tooLateTitle)
                    .setMessage(tooLateBody)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            sequence.setStatus(Sequence.STATUS_RECENTLY_MISSED);
                            setIsTooLate();
                            finish();
                        }
                    });
            alertBuilder.show();

            return false;
        }

        return true;
    }

    private void setIsTooLate() {
        isTooLate = true;
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
        sequence.onPreLoaded(null);
        currentPage = sequence.getCurrentPage();
        pageViewAdapter.setPage(currentPage);
    }

    private void setChrome() {
        Logger.d(TAG, "Setting chrome");

        if (isPotentialEndPage()) {
            Logger.d(TAG, "This is potentially the end page -> setting finish button text");
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
            finishButton.setClickable(true);
        }
    }

    private void setIsContinuingOrFinishing() {
        isContinuingOrFinishing = true;
    }

    public boolean isPotentialEndPage() {
        // If we're the last question, or the last before bonus
        // and we don't yet know if we're going to skip them or not
        return currentPage.isLastOfSequence() ||
                (currentPage.isLastBeforeBonuses() && !sequence.isSkipBonusesAsked());
    }

    private boolean isEndPage() {
        // If we're the last question, or the last before bonus
        // and we know we're going to skip them
        return currentPage.isLastOfSequence() ||
                (currentPage.isLastBeforeBonuses() && sequence.isSkipBonusesAsked()
                        && sequence.isSkipBonuses());
    }

    private void startSchedulerService() {
        String type = sequence.getType();
        Logger.d(TAG, "Starting scheduler for type {}", type);

        Intent schedulerIntent;
        if (type.equals(Sequence.TYPE_PROBE)) {
            schedulerIntent = new Intent(this, ProbeSchedulerService.class);
        } else if (type.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)) {
            schedulerIntent = new Intent(this, MQSchedulerService.class);
        } else if (type.equals(Sequence.TYPE_EVENING_QUESTIONNAIRE)) {
            schedulerIntent = new Intent(this, EQSchedulerService.class);
        } else {
            // This was a begin/end questionnaire, do nothing.
            return;
        }
        startService(schedulerIntent);
    }

    private void startListeningTasks() {
        LocationCallback locationCallback = new LocationCallback() {

            private final String TAG = "LocationCallback";

            @Override
            public void onLocationReceived(Location location) {
                Logger.i(TAG, "Received location for page, setting it");
                currentPage.setLocation(location);
            }

        };

        SntpClientCallback sntpCallback = new SntpClientCallback() {

            private final String TAG = "SntpClientCallback";

            @Override
            public void onTimeReceived(SntpClient sntpClient) {
                if (sntpClient != null) {
                    currentPage.setNtpTimestamp(sntpClient.getNow());
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
            currentPage.setStatus(Page.STATUS_ANSWERED);

            if (currentPage.isNextBonus() && !sequence.isSkipBonusesAsked()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                String skipText = "Skip";
                if (currentPage.isLastBeforeBonuses()) {
                    skipText = "Nope, had enough";
                }
                builder.setTitle("Bonus")
                        .setMessage("Do you want to go for bonus questions?")
                        .setCancelable(false)
                        .setPositiveButton("Go for bonus", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                sequence.setSkipBonuses(false);
                                transitionToNext();
                            }

                        })
                        .setNegativeButton(skipText, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                sequence.setSkipBonuses(true);
                                transitionToNext();
                            }

                        });

                // create alert dialog
                AlertDialog bonusAlert = builder.create();
                // show it
                bonusAlert.show();
                FontUtils.setRobotoToAlertDialog(bonusAlert, PageActivity.this);

            } else {
                transitionToNext();
            }
        }
    }

    private void transitionToNext() {
        setIsContinuingOrFinishing();
        if (isEndPage()) {
            Logger.d(TAG, "End page -> finishing sequence");
            finishSequence();
        } else {
            Logger.d(TAG, "Launching next page");
            launchNextPage();
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
        sequence.skipRemainingBonuses();
        sequence.setStatus(Sequence.STATUS_COMPLETED);

        String sequenceType = sequence.getType();
        if (!sequenceType.equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE) &&
                !sequenceType.equals(Sequence.TYPE_END_QUESTIONNAIRE) &&
                !sequence.isSelfInitiated()) {
            // Self-initiated probes don't interfere with normal scheduling
            startSchedulerService();
        }

        if (sequenceType.equals(Sequence.TYPE_MORNING_QUESTIONNAIRE)) {
            // Show questionnaire notification if necessary, right after morning questionnaire
            statusManager.updateBEQNotification();
        }

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
