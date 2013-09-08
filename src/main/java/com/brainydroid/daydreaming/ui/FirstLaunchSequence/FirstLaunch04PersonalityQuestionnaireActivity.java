package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.View;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//@ContentView(R.layout.activity_first_launch_questionnaire)
public class FirstLaunch04PersonalityQuestionnaireActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch04PersonalityQuestionnaireActivity";


    //private SharedPreferences sharedPreferences;

    public static int MAX_SEEKBAR = 100;
    public static int INIT_PROGRESS_SEEKBAR = 50;

    public int ThumbOffset;

    public List<String> hints;
    public int hintsNumber;

    public ArrayList<SeekBar> seekBars = new ArrayList<SeekBar>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_launch_questionnaire);


        String[] hints1 = getBaseContext().getResources().getStringArray(R.array.tipi_answers);
        hints = Arrays.asList(hints1);
        hintsNumber = hints.size();

        ThumbOffset = dpToPx(getBaseContext().getResources().getInteger(R.integer.Thumb_lateral_offset));

        LinearLayout questions_layout =  (LinearLayout) findViewById(R.id.Questions_linear_layout);
        String[] objects = getBaseContext().getResources().getStringArray(R.array.tipi_questions);
        for (String s : objects) {
            seekBars.add(inflateView(s));
        }
    }


    // TODO decide where and how to save Personnality questionnaire answers
    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked");

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i = 0; i < seekBars.size(); i++) {
            int index = (int)FloatMath.floor((seekBars.get(i).getProgress() / 101f) * hintsNumber);
            editor.putInt("tipi_question"+Integer.toString(i),index );
        }
        editor.commit();


        launchNextActivity(FirstLaunch05MeasuresActivity.class);
    }


    // No need anymore, since we removed the skipped button
    public void onClick_buttonSkip(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Skip button clicked ");
    }


    /**
     * Creating and adding each individual question view to question layout
     * Addition from Question string.
     * @param question_text
     * @return
     */
    private SeekBar inflateView(String question_text) {
        Logger.v(TAG, "Inflating view for Question");

        // View view = layoutInflater.inflate(R.layout.personality_question_layout, null);
        LinearLayout questions_layout =  (LinearLayout) findViewById(R.id.Questions_linear_layout);
        View view = getLayoutInflater().inflate(R.layout.personality_question_layout, questions_layout,false);


        TextView question = (TextView)view.findViewById(R.id.Questionnaire_question);
        question.setText(question_text);

        final TextView answers = (TextView)view.findViewById(R.id.Questionnaire_answer);
        answers.setText(hints.get(3));

        SeekBar seekBar = (SeekBar)view.findViewById(R.id.Questionnaire_seekBar);
        seekBar.setMax(MAX_SEEKBAR);
        seekBar.setProgress(INIT_PROGRESS_SEEKBAR);
        seekBar.setPadding(ThumbOffset,0,ThumbOffset,0);
        seekBar.setProgressDrawable(view.getResources().getDrawable(R.drawable.question_slider_progress));
        seekBar.setThumb(view.getResources().getDrawable(R.drawable.question_slider_thumb));

        //seekBar.setOnSeekBarChangeListener(OnSeekBarProgress);


        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                int index = (int)FloatMath.floor((progress / 101f) * hintsNumber);
                answers.setText(hints.get(index));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
        seekBar.setOnSeekBarChangeListener(listener);



        questions_layout.addView(view);
        return seekBar;

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }




}

