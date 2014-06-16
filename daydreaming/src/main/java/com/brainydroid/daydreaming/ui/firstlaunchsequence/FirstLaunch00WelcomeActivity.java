package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;

/**
 * Activity at first launch
 * Display of animation, timer to next activity
 *
 * In first launch sequence of apps
 *
 * Previous activity :  none
 * This activity     :  FirstLaunch00WelcomeActivity
 * Next activity     :  FirstLaunch01DescriptionActivity
 *
 */
@ContentView(R.layout.activity_first_launch_welcome)
public class FirstLaunch00WelcomeActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch00WelcomeActivity";
    private static CountDownTimer Animation_Timer;

    /**
     * onCreate
     * Launching description activity at the end of animation timer
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);


        launchAnimation();
            }

    public void launchAnimation(){
        ImageView MyImageView = (ImageView)findViewById(R.id.firstLaunchWelcome_helice);
        MyImageView.setBackgroundResource(R.drawable.animated_helix);
        AnimationDrawable AniFrame = (AnimationDrawable) MyImageView.getBackground();
        AniFrame.start();

        int duration =  getResources().getInteger(R.integer.welcome_animation_duration);
        Animation_Timer =  new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                if (statusManager.isTipiQuestionnaireCompleted()) {
                    launchNextActivity(FirstLaunch05MeasuresActivity.class);
                } else {
                    launchNextActivity(FirstLaunch01DescriptionActivity.class);
                }
                Animation_Timer.cancel();
            }
        }.start();
    }

    /**
     * Launching next activity in first launch sequence with Fade in/Fade out transition
     * @param activity
     */
    @Override
    protected void launchNextActivity(Class activity) {
        Logger.v(TAG, "Launching next activity, custom transition");
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
    }

}
