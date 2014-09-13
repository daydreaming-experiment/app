package com.brainydroid.daydreaming.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.sequence.ISequence;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.brainydroid.daydreaming.ui.sequences.PageActivity;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.HashMap;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_beginquestionnaires_layout)

public class BeginQuestionnairesActivity extends RoboFragmentActivity {

    private static String TAG = "BeginQuestionnairesActivity";
    @Inject  StatusManager statusManager;
    @Inject  ParametersStorage parametersStorage;
    @Inject  Provider<SequencesStorage> sequencesStorageProvider;
    @Inject  SequenceBuilder sequenceBuilder;
    @Inject HashMap<String,TextView> questionnairesTextViews;

    @InjectView(R.id.beginquestionnaires_questionnaires_layout)
    LinearLayout beginQuestionnairesLinearLayout;

    private ArrayList<SequenceDescription> allBeginQuestionnairesDescriptions;
    private ArrayList<Sequence> loadedBeginQuestionnaires;
    private ArrayList<Sequence> completedBeginQuestionnaires;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        checkFirstLaunch();
        createQuestionnaires();
        populateQuestionnairesView();
        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        updateQuestionnairesStatusView();
    }

    @Override
    public synchronized void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();
        updateQuestionnairesStatusView();
    }

    /**
     * Check if Begin Questionnaires were instantiated. If not instantiate them
     */
    protected synchronized void createQuestionnaires() {
        Logger.v(TAG, "Instantiating Begin Questionnaires");

        allBeginQuestionnairesDescriptions = parametersStorage.getSequencesByType(
                Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        completedBeginQuestionnaires = sequencesStorageProvider.get()
                .getUploadableSequences(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        loadedBeginQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);

        if (allBeginQuestionnairesDescriptions == null || allBeginQuestionnairesDescriptions.isEmpty()) {
            // if empty do nothing
            Logger.v(TAG, "No Begin Questionnaires in parameters");
        } else {
            Logger.v(TAG, "Begin Questionnaires found in parameters");
            if (loadedBeginQuestionnaires == null || loadedBeginQuestionnaires.isEmpty()) {
                Logger.v(TAG, "Instantiating Begin Questionnaires...");
                for (SequenceDescription sd : allBeginQuestionnairesDescriptions) {
                    if (!getSequenceNames(loadedBeginQuestionnaires).contains(sd.getName())){
                        Logger.v(TAG, "Instanciating questionnaire {}",sd.getName());
                        sequenceBuilder.buildSave(sd.getName());
                    }
                }
            } else {
                Logger.v(TAG, "Begin Questionnaires already instantiated");
            }
        }
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
        Logger.v(TAG, "Populating questionnaire list");

        loadedBeginQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        completedBeginQuestionnaires = sequencesStorageProvider.get()
                .getUploadableSequences(Sequence.TYPE_BEGIN_QUESTIONNAIRE);

        if (loadedBeginQuestionnaires == null || loadedBeginQuestionnaires.isEmpty() ) {
            Logger.v(TAG, "No Begin Questionnaires in parameters");
        } else {
            Logger.v(TAG, "Begin Questionnaires found in parameters");

            for (int index = 0; index < loadedBeginQuestionnaires.size(); index++) {
                LinearLayout bq_linearLayout = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.begin_questionnaire_layout, beginQuestionnairesLinearLayout, false);
                TextView questionnaireName = (TextView) bq_linearLayout.findViewById(R.id.begin_questionnaire_name);
                questionnaireName.setText("Questionnaire " + Integer.toString(index));
                final int finalIndex = index;
                LinearLayout.OnClickListener bqClickListener = new LinearLayout.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBeginQuestionnaireByIndex(finalIndex);
                    }
                };
                bq_linearLayout.setOnClickListener(bqClickListener);
                beginQuestionnairesLinearLayout.addView(bq_linearLayout);
            }
        }
    }

    protected synchronized void updateQuestionnairesStatusView() {
        Logger.v(TAG, "Updating questionnaires list view");

        loadedBeginQuestionnaires = sequencesStorageProvider.get()
                .getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        completedBeginQuestionnaires = sequencesStorageProvider.get()
                .getUploadableSequences(Sequence.TYPE_BEGIN_QUESTIONNAIRE);

        ArrayList<String> allBeginQuestionnairesNames = getSequenceNames(allBeginQuestionnairesDescriptions);
        ArrayList<String> completedBeginQuestionnairesNames = getSequenceNames(completedBeginQuestionnaires);

        for (int i = 0; i < beginQuestionnairesLinearLayout.getChildCount(); i++) {
            LinearLayout bq_linearLayout = (LinearLayout)beginQuestionnairesLinearLayout.getChildAt(i);
            TextView tv = (TextView)bq_linearLayout.findViewById(R.id.begin_questionnaire_name);
            String bq_name = allBeginQuestionnairesNames.get(i);
            boolean isComplete = completedBeginQuestionnairesNames.contains(bq_name);
            tv.setCompoundDrawablesWithIntrinsicBounds(isComplete ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);
            Logger.v(TAG, "Questionnaire TextView text: {0} - completion is {1}" , tv.getText(), isComplete ? "true":"false");
            bq_linearLayout.setClickable(!isComplete);
        }
    }

    protected void openBeginQuestionnaireByIndex(int index) {
        Logger.i(TAG, "Launching questionnaire with index {}" , Integer.toString(index));
        Logger.i(TAG, "There are {} loaded questionnaires" , Integer.toString(loadedBeginQuestionnaires.size()));

        Sequence questionnaire = loadedBeginQuestionnaires.get(index);
        Intent intent = createQuestionnaireIntent(questionnaire);
        launchQuestionnaireIntent(intent);
    }

    protected void checkFirstLaunch() {
        if (!statusManager.isFirstLaunchCompleted()) {
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

}
