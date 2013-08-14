package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.FloatMath;
import android.view.View;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity at first launch
 *
 * In first launch sequence of apps
 *
 * Previous activity :  none
 * This activity     :  FirstLaunch00WelcomeActivity
 * Next activity     :  FirstLaunch01DescriptionActivity
 *
 */
@ContentView(R.layout.activity_first_launch_questionnaire)
public class FirstLaunch04PersonalityQuestionnaireActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch04PersonalityQuestionnaireActivity";
    private static CountDownTimer Timer;


    private SharedPreferences sharedPreferences;

    public static int MAX_SEEKBAR = 100;
    public static int INIT_PROGRESS_SEEKBAR = 50;

    public SeekBar sb1, sb2, sb3, sb4, sb5, sb6, sb7, sb8, sb9, sb10;
    public TextView tvq1, tvq2, tvq3, tvq4, tvq5, tvq6, tvq7, tvq8, tvq9,tvq10;
    public TextView tva1, tva2, tva3, tva4, tva5, tva6, tva7, tva8, tva9,tva10;

    public List<String> hints;
    public int hintsNumber;
    public int questionsNumber = 10;

    public List<SeekBar> sb = new ArrayList<SeekBar>();
    public List<TextView> tvq = new ArrayList<TextView>();
    public List<TextView> tva = new ArrayList<TextView>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);



        //sb.add((SeekBar) findViewById(R.id.Questionnaire_seekBar1));


        sb1=(SeekBar) findViewById(R.id.Questionnaire_seekBar1);  sb.add(sb1);   sb1.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb2=(SeekBar) findViewById(R.id.Questionnaire_seekBar2);  sb.add(sb2);   sb2.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb3=(SeekBar) findViewById(R.id.Questionnaire_seekBar3);  sb.add(sb3);   sb3.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb4=(SeekBar) findViewById(R.id.Questionnaire_seekBar4);  sb.add(sb4);   sb4.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb5=(SeekBar) findViewById(R.id.Questionnaire_seekBar5);  sb.add(sb5);   sb5.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb6=(SeekBar) findViewById(R.id.Questionnaire_seekBar6);  sb.add(sb6);   sb6.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb7=(SeekBar) findViewById(R.id.Questionnaire_seekBar7);  sb.add(sb7);   sb7.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb8=(SeekBar) findViewById(R.id.Questionnaire_seekBar8);  sb.add(sb8);   sb8.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb9=(SeekBar) findViewById(R.id.Questionnaire_seekBar9);  sb.add(sb9);   sb9.setOnSeekBarChangeListener(OnSeekBarProgress);
        sb10=(SeekBar) findViewById(R.id.Questionnaire_seekBar10); sb.add(sb10); sb10.setOnSeekBarChangeListener(OnSeekBarProgress);

        tvq1=(TextView) findViewById(R.id.Questionnaire_question1);  tvq.add(tvq1);
        tvq2=(TextView) findViewById(R.id.Questionnaire_question2);  tvq.add(tvq2);
        tvq3=(TextView) findViewById(R.id.Questionnaire_question3);  tvq.add(tvq3);
        tvq4=(TextView) findViewById(R.id.Questionnaire_question4);  tvq.add(tvq4);
        tvq5=(TextView) findViewById(R.id.Questionnaire_question5);  tvq.add(tvq5);
        tvq6=(TextView) findViewById(R.id.Questionnaire_question6);  tvq.add(tvq6);
        tvq7=(TextView) findViewById(R.id.Questionnaire_question7);  tvq.add(tvq7);
        tvq8=(TextView) findViewById(R.id.Questionnaire_question8);  tvq.add(tvq8);
        tvq9=(TextView) findViewById(R.id.Questionnaire_question9);  tvq.add(tvq9);
        tvq10=(TextView) findViewById(R.id.Questionnaire_question10);tvq.add(tvq10);


        tva1=(TextView) findViewById(R.id.Questionnaire_answer1);  tva.add(tva1);
        tva2=(TextView) findViewById(R.id.Questionnaire_answer2);  tva.add(tva2);
        tva3=(TextView) findViewById(R.id.Questionnaire_answer3);  tva.add(tva3);
        tva4=(TextView) findViewById(R.id.Questionnaire_answer4);  tva.add(tva4);
        tva5=(TextView) findViewById(R.id.Questionnaire_answer5);  tva.add(tva5);
        tva6=(TextView) findViewById(R.id.Questionnaire_answer6);  tva.add(tva6);
        tva7=(TextView) findViewById(R.id.Questionnaire_answer7);  tva.add(tva7);
        tva8=(TextView) findViewById(R.id.Questionnaire_answer8);  tva.add(tva8);
        tva9=(TextView) findViewById(R.id.Questionnaire_answer9);  tva.add(tva9);
        tva10=(TextView) findViewById(R.id.Questionnaire_answer10);tva.add(tva10);


        String[] hints1 = getResources().getStringArray(R.array.tipi_answers);
        hints = Arrays.asList(hints1);
        hintsNumber = hints.size();

        for(SeekBar s : sb) {
            s.setMax(MAX_SEEKBAR);
            s.setProgress(INIT_PROGRESS_SEEKBAR);
            s.setProgressDrawable(getResources().getDrawable(R.drawable.question_slider_progress));
            s.setThumb(getResources().getDrawable(R.drawable.question_slider_thumb));
        }


        for(TextView t : tva) {
            t.setText(hints.get(3));
        }


   //     ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
   //     FontUtils.setRobotoFont(this, godfatherView);

    }

    SeekBar.OnSeekBarChangeListener OnSeekBarProgress =
            new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                    Logger.v(TAG, "Touched item ID:" + Integer.toString(seekBar.getId()) + " - out of "+ Integer.toString(hintsNumber));

                    for(int i = 0; i < questionsNumber; i++) {


                        //Logger.v(TAG, "list item id: " + Integer.toString(sb.get(i).getId()));
                        if(seekBar.getId() == sb.get(i).getId()){

                            int index = (int)FloatMath.floor((progress / 101f) * hintsNumber);

                            tva.get(i).setText(hints.get(index));

                            Logger.v(TAG, "Touched item " + Integer.toString(i));


                        }
                    }


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked");


//              SharedPreferences.Editor editor = sharedPreferences.edit();

//        for(int i = 0; i < questionsNumber; i++) {
 //           int index = (int)FloatMath.floor((sb.get(i).getProgress() / 101f) * hintsNumber);
  //          editor.putInt("tipi_question"+Integer.toString(i),index );
    //    }
      //        editor.commit();


        launchNextActivity(FirstLaunch05MeasuresActivity2.class);
    }



    public void onClick_buttonSkip(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Skip button clicked ");
    }

}

