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
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by vincent on 10/09/14.
 */
@ContentView(R.layout.activity_beginquestionnaires_layout)

public class BeginQuestionnairesActivity extends RoboFragmentActivity {

    private static String TAG = "BeginQuestionnairesActivity";
    @Inject  StatusManager statusManager;
    @Inject  ParametersStorage parametersStorage;
    @Inject  Provider<SequencesStorage> sequencesStorageProvider;
    @Inject  SequenceBuilder sequenceBuilder;
    @Inject
    HashMap<String,TextView> questionnairesTextViews;

    @InjectView(R.id.beginquestionnaires_questionnaires_layout)
    LinearLayout beginQuestionnairesLinearLayout;


    private static ArrayList<Sequence> questionnaires;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        createQuestionnaires();
        populateQuestionnairesView();
        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        updateQuestionnairesStatusView();

    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        updateQuestionnairesStatusView();
        super.onResume();
    }

    /**
     * Check if Begin Questionnaries were instantiated. If not instantiates them
     *
     */
    protected void createQuestionnaires(){
        Logger.v(TAG, "Instantiating Begin Questionnaires");

        ArrayList<SequenceDescription> allBeginQuestionnaires = parametersStorage.getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        ArrayList<Sequence> loadedBeginQuestionnaires = sequencesStorageProvider.get().getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);

        if (allBeginQuestionnaires == null | allBeginQuestionnaires.isEmpty() ) {
            // if empty do nothing
            Logger.v(TAG, "No Begin Questionnaires in parameters");
        } else {
            Logger.v(TAG, "Begin Questionnaires found in parameters");
            if (loadedBeginQuestionnaires == null | loadedBeginQuestionnaires.isEmpty() ) {
                Logger.v(TAG, "Begin Questionnaires already instantiated");
            } else {
                Logger.v(TAG, "Instantiating Begin Questionnaires...");
                for (SequenceDescription sd : allBeginQuestionnaires) {
                    Sequence bqSequence = sequenceBuilder.buildSave(sd.getName());
                }
            }
        }
    }

    protected ArrayList<String> getSequenceNames(ArrayList<Sequence> sequences){
        ArrayList<String> names = new ArrayList<String>();
        if (sequences != null){
            if (!sequences.isEmpty() ) {
                for (Sequence s : sequences) {
                    names.add(s.getName());
                }
            }
        }
        return names;
    }

    protected ArrayList<String> getSequenceDescriptionNames(ArrayList<SequenceDescription> sequenceDescriptions){
        ArrayList<String> names = new ArrayList<String>();
        if (sequenceDescriptions != null){
            if (!sequenceDescriptions.isEmpty() ) {
                for (SequenceDescription sd : sequenceDescriptions) {
                    names.add(sd.getName());
                }
            }
        }
        return names;
    }

    protected void populateQuestionnairesView() {
        Logger.v(TAG, "Populating questionnaire list");

        // Get list of questionnaires (sequences) to be displayed
        ArrayList<SequenceDescription> allBeginQuestionnaires = parametersStorage.getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        ArrayList<Sequence> loadedBeginQuestionnaires = sequencesStorageProvider.get().getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);


        int index = 0;
        if (allBeginQuestionnaires == null | allBeginQuestionnaires.isEmpty() ) {
            Logger.v(TAG, "No Begin Questionnaires in parameters");
        } else {
            Logger.v(TAG, "Begin Questionnaires found in parameters");

            for (SequenceDescription sd : allBeginQuestionnaires) {
                index += 1;
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

    protected void updateQuestionnairesStatusView() {
        Logger.v(TAG, "Updating questionnaires list view");

        ArrayList<SequenceDescription> allBeginQuestionnaires = parametersStorage.getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        ArrayList<Sequence> completedBeginQuestionnaires = sequencesStorageProvider.get().getUploadableSequences(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        ArrayList<String> allBeginQuestionnairesNames = getSequenceDescriptionNames(allBeginQuestionnaires);
        ArrayList<String> completedBeginQuestionnairesNames = getSequenceNames(completedBeginQuestionnaires);

        for (int i = 0; i < beginQuestionnairesLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView)beginQuestionnairesLinearLayout.getChildAt(i).findViewById(R.id.begin_questionnaire_name);
            String bq_name = allBeginQuestionnairesNames.get(i);
            boolean isComplete = completedBeginQuestionnairesNames.contains(bq_name);
            tv.setCompoundDrawablesWithIntrinsicBounds(isComplete ? R.drawable.status_ok : R.drawable.status_wrong, 0, 0, 0);
            Logger.v(TAG, "Questionnaire textview text: {} - completion is", tv.getText());
        }
    }

    protected void openBeginQuestionnaireByIndex(int index) {
        // open a questionnaire
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

}
