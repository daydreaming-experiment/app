package com.brainydroid.daydreaming.ui;

import android.view.View;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_first_launch_description)
public class FirstLaunchDescriptionActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunchDescriptionActivity";

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked -> launching consent dialog");
        launchNextActivity(FirstLaunchTermsActivity.class);
    }

}
