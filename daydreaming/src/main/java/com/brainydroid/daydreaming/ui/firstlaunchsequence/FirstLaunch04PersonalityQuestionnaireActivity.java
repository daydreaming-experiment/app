package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.ui.AlphaSeekBar;
import com.google.inject.Inject;
import roboguice.inject.ContentView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity at first launch
 * Asking a few questions about user
 *
 * In first launch sequence of apps
 *
 * Previous activity :  FirstLaunch03ProfileActivity
 * This activity     :  FirstLaunch04PersonalityQuestionnaireActivity
 * Next activity     :  FirstLaunch05MeasuresActivity
 *
 */
@ContentView(R.layout.activity_first_launch_questionnaire)
public class FirstLaunch04PersonalityQuestionnaireActivity
        extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch04PersonalityQuestionnaireActivity";

    @SuppressWarnings("FieldCanBeLocal")
    private static int MAX_PROGRESS_SEEKBAR = 100;
    @SuppressWarnings("FieldCanBeLocal")
    private static int INIT_PROGRESS_SEEKBAR = 50;

    @Inject ProfileStorage profileStorage;
    @Inject HashMap<String, SeekBar> seekBars;
    @Inject HashMap<String, Boolean> seekBarsTouchedStates;

    private List<String> hints;
    private int nHints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        String[] hintsPre = getBaseContext().getResources().getStringArray(
                R.array.tipi_answers);
        hints = Arrays.asList(hintsPre);
        nHints = hints.size();
        String[] objects = getBaseContext().getResources().getStringArray(
                R.array.tipi_questions);
        for (String s : objects) {
            seekBarsTouchedStates.put(s, false);
            seekBars.put(s, inflateView(s));
        }

        setRobotoFont(this);
    }

    public void onClick_buttonNext(
            @SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked");

        if (retrieveTipiAnswers()) {
            statusManager.setTipiQuestionnaireCompleted();
            launchNextActivity(FirstLaunch05MeasuresActivity.class);
        }
    }

    private boolean retrieveTipiAnswers() {
        Logger.d(TAG, "Retrieving answers from layout");

        for (Map.Entry<String, Boolean> seekBarTouched :
                seekBarsTouchedStates.entrySet()) {
            if (!seekBarTouched.getValue()) {
                Logger.d(TAG, "Some seekBar were not touched -> " +
                        "asking the user to answer all questions");
                Toast.makeText(this, "Please answer all the questions",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        Logger.d(TAG, "All seekBars were touched -> retrieving answers");

        HashMap<String, Integer> answers = new HashMap<String, Integer>();
        for (Map.Entry<String, SeekBar> seekBar : seekBars.entrySet()) {
            answers.put(seekBar.getKey(), seekBar.getValue().getProgress());
        }
        profileStorage.setTipiAnswers(answers);

        return true;
    }

    // No need anymore, since we removed the skipped button
    public void onClick_buttonSkip(
            @SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Skip button clicked ");
    }

    /**
     * Creating and adding each individual question view to question layout
     * Addition from Question string.
     */
    @TargetApi(11)
    private SeekBar inflateView(final String questionText) {
        Logger.v(TAG, "Inflating view for tipi question");

        LinearLayout questions_layout =
                (LinearLayout)findViewById(R.id.questions_linear_layout);
        View view = getLayoutInflater().inflate(
                R.layout.personality_question_layout, questions_layout, false);

        TextView question =
                (TextView)view.findViewById(R.id.Questionnaire_question);
        question.setText(questionText);

        final TextView answer =
                (TextView)view.findViewById(R.id.Questionnaire_answer);
        answer.setText(hints.get(3));

        AlphaSeekBar seekBar =
                (AlphaSeekBar)view.findViewById(R.id.Questionnaire_seekBar);
        seekBar.setMax(MAX_PROGRESS_SEEKBAR);
        seekBar.setProgress(INIT_PROGRESS_SEEKBAR);
        //seekBar.setPadding(thumbOffset, 0, thumbOffset, 0);
        seekBar.setProgressDrawable(view.getResources().getDrawable(R
                .drawable.question_slider_progress));
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaSeekBar exists),
        // hence the @TargetApi(11) above.
        seekBar.setAlpha(0.5f);

        seekBar.setThumb(view.getResources().getDrawable(
                R.drawable.question_slider_thumb));

        AlphaSeekBar.OnAlphaSeekBarChangeListener onSeekBarChange =
                new AlphaSeekBar.OnAlphaSeekBarChangeListener() {

            @TargetApi(11)
            @Override
            public void onProgressChanged(AlphaSeekBar seekBar, int progress,
                                          boolean fromUser) {
                int index = (int)FloatMath.floor((progress / 101f) * nHints);
                answer.setText(hints.get(index));
                seekBarsTouchedStates.put(questionText, true);
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaSeekBar exists),
                // hence the @TargetApi(11) above.
                seekBar.setAlpha(1f);
            }

            @Override
            public void onStartTrackingTouch(AlphaSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(AlphaSeekBar seekBar) {}

        };

        seekBar.setOnSeekBarChangeListener(onSeekBarChange);

        questions_layout.addView(view);
        return seekBar;
    }

    // Overriding parent method
    @Override
    public boolean shouldFinishIfTipiQuestionnaireCompleted() {
        return true;
    }

}

