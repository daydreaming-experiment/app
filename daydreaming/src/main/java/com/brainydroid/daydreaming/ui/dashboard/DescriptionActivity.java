package com.brainydroid.daydreaming.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch01DescriptionActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_first_launch_description)
public class DescriptionActivity extends FirstLaunch01DescriptionActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "DescriptionActivity";

    @Override
    public void checkFirstLaunch() { }

    @Override
    public void setButton(){
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setClickable(false);
        ViewGroup parent = (ViewGroup)findViewById(R.id.firstLaunchDescription_main_layout);
        LayoutInflater.from(this).inflate(R.layout.return_to_dashboard_button, parent, true);
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backToDashboard(View v) {
        onBackPressed();
    }

}
