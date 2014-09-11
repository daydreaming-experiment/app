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
        populateQuestionnaires();
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

    protected void populateQuestionnaires() {
        Logger.v(TAG, "Populating questionnaire list");

        // Get list of questionnaires (sequences) to be displayed
        ArrayList<Sequence> sequencesLoaded = sequencesStorageProvider.get().getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);
        ArrayList<SequenceDescription> sequenceDescriptionAll = parametersStorage.getSequencesByType(Sequence.TYPE_BEGIN_QUESTIONNAIRE);

        // make string list of names
        ArrayList<String> sequencesLoadedNames = new ArrayList<String>();
        if (sequencesLoaded!=null){
            for (Sequence s : sequencesLoaded) { sequencesLoadedNames.add(s.getName()); }
        }
        ArrayList<String> sequenceDescriptionAllNames = new ArrayList<String>();
        if (sequenceDescriptionAll!=null){
            for (SequenceDescription sd : sequenceDescriptionAll) { sequenceDescriptionAllNames.add(sd.getName()); }
        }

        // load from parameters, save and display
        int index = 0;
        if (sequenceDescriptionAll != null) {
            for (SequenceDescription sd : sequenceDescriptionAll) {

                // build and save questionnaire
                if (!sequencesLoadedNames.contains(sd.getName())){
                    Sequence bqSequence = sequenceBuilder.buildSave(sd.getName());
                }

                // inflate sequence
                index += 1;
                LinearLayout bq_linearLayout = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.begin_questionnaire_layout, beginQuestionnairesLinearLayout, false);
                TextView questionnaireName = (TextView) bq_linearLayout.findViewById(R.id.begin_questionnaire_name);
                questionnaireName.setText("Questionnaire " + Integer.toString(index));
                questionnairesTextViews.put(sd.getName(), questionnaireName);
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

        for (int i = 0; i < beginQuestionnairesLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView)beginQuestionnairesLinearLayout.getChildAt(i).findViewById(R.id.begin_questionnaire_name);
            //do something
        }
    }

    protected void openBeginQuestionnaireByIndex(int index){
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
