package com.brainydroid.daydreaming.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.sequence.ISequence;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.brainydroid.daydreaming.ui.sequences.PageActivity;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_beginendquestionnaires_layout)

public class BEQActivity extends RoboFragmentActivity {

    private static String TAG = "BEQActivity";

    @Inject Provider<StatusManager> statusManagerProvider;
    @Inject ParametersStorage parametersStorage;
    @Inject Provider<SequencesStorage> sequencesStorageProvider;
    @Inject StatusManager statusManager;

    @InjectView(R.id.beginendquestionnaires_questionnaires_layout)
    LinearLayout beginEndQuestionnairesLinearLayout;

    private ArrayList<Sequence> loadedBeginEndQuestionnaires;
    private ArrayList<Sequence> completedBeginEndQuestionnaires;

    private boolean testModeThemeActivated = false;
    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        checkFirstLaunch();
        checkTestMode();
        populateQuestionnairesView();
        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        type = statusManagerProvider.get().getCurrentBEQType();
    }

    @Override
    public synchronized void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();
        checkExperimentModeActivatedDirty();
        updateQuestionnairesStatusView();
    }

    @Override
    public synchronized void onPause() {
        Logger.v(TAG, "Pausing");
        super.onPause();
    }

    protected ArrayList<String> getSequenceNames(ArrayList<? extends ISequence> sequences) {
        ArrayList<String> names = new ArrayList<String>();
        if (sequences != null) {
            if (!sequences.isEmpty()) {
                for (ISequence s : sequences) {
                    names.add(s.getName());
                }
            }
        }
        return names;
    }

    protected synchronized void populateQuestionnairesView() {

        type = statusManagerProvider.get().getCurrentBEQType();
        Logger.v(TAG, "Populating questionnaire list - type:{}",type);

        loadedBeginEndQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(type);

        if (loadedBeginEndQuestionnaires == null || loadedBeginEndQuestionnaires.isEmpty() ) {
            Logger.v(TAG, "No Begin Questionnaires in parameters");
        } else {
            Logger.v(TAG, "Begin Questionnaires found in parameters");

            for (int index = 0; index < loadedBeginEndQuestionnaires.size(); index++) {
                LinearLayout bq_linearLayout = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.beginend_questionnaire_layout, beginEndQuestionnairesLinearLayout, false);
                TextView bq_name = (TextView) bq_linearLayout.findViewById(R.id.beq_name);
                bq_name.setText("Questionnaire " + Integer.toString(index+1));
                final int finalIndex = index;
                LinearLayout.OnClickListener bqClickListener = new LinearLayout.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String status = loadedBeginEndQuestionnaires.get(finalIndex).getStatus();
                        if (status != null && (status.equals(Sequence.STATUS_UPLOADED_AND_KEEP)
                                || status.equals(Sequence.STATUS_COMPLETED))) {
                            Logger.d(TAG,"Questionnaire {} completed already", Integer.toString(finalIndex+1));
                            Toast.makeText(BEQActivity.this,"Already done",Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.d(TAG,"Questionnaire {} not completed, opening", Integer.toString(finalIndex+1));
                            openBeginQuestionnaireByIndex(finalIndex);
                        }
                    }
                };
                bq_linearLayout.setOnClickListener(bqClickListener);
                beginEndQuestionnairesLinearLayout.addView(bq_linearLayout);
            }
        }
    }

    protected synchronized void updateQuestionnairesStatusView() {
        Logger.v(TAG, "Updating questionnaires list view");

        loadedBeginEndQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(type);
        completedBeginEndQuestionnaires = sequencesStorageProvider.get()
                .getCompletedSequences(type);

        ArrayList<String> allBeginQuestionnairesNames = getSequenceNames(loadedBeginEndQuestionnaires);
        ArrayList<String> completedBeginQuestionnairesNames = getSequenceNames(completedBeginEndQuestionnaires);

        for (int i = 0; i < beginEndQuestionnairesLinearLayout.getChildCount(); i++) {
            LinearLayout bq_linearLayout = (LinearLayout) beginEndQuestionnairesLinearLayout.getChildAt(i);
            TextView tv = (TextView)bq_linearLayout.findViewById(R.id.beq_name);
            String bq_name = allBeginQuestionnairesNames.get(i);

            boolean isComplete = completedBeginQuestionnairesNames.contains(bq_name);
            String status = loadedBeginEndQuestionnaires.get(i).getStatus();
            if (status != null && status.equals(Sequence.STATUS_MISSED_OR_DISMISSED_OR_INCOMPLETE)) {
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.status_loading_blue, 0, 0, 0);
            } else {
                tv.setCompoundDrawablesWithIntrinsicBounds(isComplete ? R.drawable.status_ok_blue : R.drawable.status_wrong_blue, 0, 0, 0);
            }
            Logger.v(TAG, "Questionnaire TextView text: {0} - completion is {1}", tv.getText(), isComplete ? "true" : "false");
        }
    }

    protected void openBeginQuestionnaireByIndex(int index) {
        Logger.i(TAG, "Launching questionnaire with index {}" , Integer.toString(index));
        Logger.i(TAG, "There are {} loaded questionnaires" , Integer.toString(loadedBeginEndQuestionnaires.size()));
        Sequence questionnaire = loadedBeginEndQuestionnaires.get(index);
        Intent intent = createQuestionnaireIntent(questionnaire);
        launchQuestionnaireIntent(intent);
    }

    protected void checkFirstLaunch() {
        if (!statusManagerProvider.get().is(StatusManager.EXP_STATUS_FL_COMPLETED)) {
            Logger.i(TAG, "First launch not completed -> starting first " +
                    "launch sequence and finishing this activity");
            Intent intent = new Intent(this, FirstLaunch00WelcomeActivity.class);
            // No need for Intent.FLAG_ACTIVITY_CLEAR_TOP here since FirstLaunch00WelcomeActivity
            // is "noHistory" and as such never exists in the back stack.
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            Logger.v(TAG, "First launch completed");
        }
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed");
        super.onBackPressed();
        overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
    }

    public void onClick_backToDashboard(@SuppressWarnings("UnusedParameters") View v) {
        Logger.v(TAG, "Back to dashboard button clicked");
        onBackPressed();
    }

    /**
     * Create the {@link com.brainydroid.daydreaming.ui.sequences.PageActivity} {@link Intent}.
     *
     * @return An {@link Intent} to launch our {@link Sequence}
     */
    private synchronized Intent createQuestionnaireIntent(Sequence questionnaire) {
        Logger.d(TAG, "Creating Questionnaire Intent");

        Intent intent = new Intent(this, PageActivity.class);
        // Set the id of the probe to start
        intent.putExtra(PageActivity.EXTRA_SEQUENCE_ID, questionnaire.getId());
        // Create a new task. The rest is defined in the App manifest.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Launch Questionnaire
     */
    private synchronized void launchQuestionnaireIntent(Intent questionnaireIntent) {
        Logger.d(TAG, "Launching Questionnaire");
        startActivity(questionnaireIntent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void checkExperimentModeActivatedDirty() {
        if ((statusManager.getCurrentMode() == StatusManager.MODE_TEST && !testModeThemeActivated)
                || (statusManager.getCurrentMode() == StatusManager.MODE_PROD && testModeThemeActivated)) {
            Logger.w(TAG, "Test/production mode theme discrepancy, " +
                    "meaning a vicious activity path didn't let us update");
            finish();
        } else {
            Logger.v(TAG, "No test mode theming discrepancy");
        }
    }

    private void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.daydreamingTheme);
            testModeThemeActivated = false;
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.daydreamingTestTheme);
            testModeThemeActivated = true;
        }
    }

}
