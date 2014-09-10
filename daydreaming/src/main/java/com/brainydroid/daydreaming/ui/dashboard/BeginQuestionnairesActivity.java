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
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.google.inject.Inject;

import java.util.ArrayList;

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
    }

    @Override
    public void onResume() {
        Logger.v(TAG, "Resuming");
        updateQuestionnairesStatusView();
        super.onResume();
    }

    protected void populateQuestionnaires() {
        // Get list of questionnaires (sequences) to be displayed
        ArrayList<SequenceDescription> sequenceDescriptionArrayList = parametersStorage.getSequences();
        // load them

        int index = 0;
        for (SequenceDescription sd : sequenceDescriptionArrayList) {
            if (sd.getType().equals(Sequence.TYPE_BEGIN_QUESTIONNAIRE)) {
                index += 1;
                LinearLayout bq_linearLayout = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.begin_questionnaire_layout, beginQuestionnairesLinearLayout, false);
                TextView questionnaireName = (TextView) bq_linearLayout.findViewById(R.id.begin_questionnaire_name);
                questionnaireName.setText("Questionnaire " + Integer.toString(index));


                final int finalIndex = index;
                LinearLayout.OnClickListener bqClickListener = new LinearLayout.OnClickListener(){
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
        // updates the drawable of the view of each questionnaire depending on completion
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
