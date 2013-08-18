package com.brainydroid.daydreaming.ui.ReOpen;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

@ContentView(R.layout.activity_first_launch_description)
public class ReOpenDescriptionActivity extends ReOpenActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch01DescriptionActivity";

    @InjectView(R.id.firstLaunchDescription_textDescription) TextView description;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        populateDescription();
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
//        Logger.d(TAG, "Next button clicked -> launching consent dialog");
//        launchNextActivity(FirstLaunch02TermsActivity.class);
    }



    private void populateDescription() {
        try {
            InputStream termsInputStream = getResources().openRawResource(R.raw.description);
            description.setText(Util.convertStreamToString(termsInputStream));
            termsInputStream.close();
        } catch (IOException e) {
            Logger.e(TAG, "Error reading consent file");
            e.printStackTrace();
        }
    }

}
