package com.brainydroid.daydreaming.ui.Dashboard;

import android.view.View;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.ui.FirstLaunchSequence.FirstLaunch01DescriptionActivity;
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
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

}
