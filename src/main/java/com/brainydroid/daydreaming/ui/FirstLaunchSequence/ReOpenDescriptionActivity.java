package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.FontUtils;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

@ContentView(R.layout.activity_first_launch_description)
public class ReOpenDescriptionActivity extends FirstLaunch01DescriptionActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch01DescriptionActivity";

    @Override
    public void Ext_Checkfirstlaunch(){
    }

    @Override
    public void setbutton(){
        next_button.setVisibility(View.INVISIBLE);
        next_button.setClickable(false);
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

}
