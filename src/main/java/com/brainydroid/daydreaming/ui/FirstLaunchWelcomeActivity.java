package com.brainydroid.daydreaming.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.brainydroid.daydreaming.R;
import roboguice.inject.ContentView;

/**
 * Activity at first launch
 *
 * In first launch sequence of apps
 *
 * Previous activity :  none
 * This activity     :  FirstLaunchWelcomeActivity
 * Next activity     :  FirstLaunchDescriptionActivity
 *
 */
@ContentView(R.layout.activity_first_launch_welcome)
public class FirstLaunchWelcomeActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunchWelcomeActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }
        super.onCreate(savedInstanceState);
        ImageView MyImageView = (ImageView)findViewById(R.id.myImageView);
        MyImageView.setBackgroundResource(R.drawable.animated_background);
        AnimationDrawable AniFrame = (AnimationDrawable) MyImageView.getBackground();
        AniFrame.start();

    }

    public void onClick_start(@SuppressWarnings("UnusedParameters") View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onClick_start");
        }

        launchNextActivity(FirstLaunchDescriptionActivity.class);
    }

}
