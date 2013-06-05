package com.brainydroid.daydreaming.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

@ContentView(R.layout.activity_first_launch_terms)

public class FirstLaunchTermsActivity extends FirstLaunchActivity implements ScrollViewListener{

    private static String TAG = "FirstLaunchTermsActivity";

    public ScrollViewExt sv;
    public Button Agree_button;
    public Button Disagree_button;



    @InjectView(R.id.firstLaunchTerms_textConsent) TextView consent;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        Agree_button = (Button) findViewById(R.id.firstLaunchTerms_buttonAgree);
        Disagree_button = (Button) findViewById(R.id.firstLaunchTerms_buttonDisagree);
        Agree_button.setEnabled(false);
        Disagree_button.setEnabled(true);

        sv = (ScrollViewExt) findViewById(R.id.firstLaunchTerms_Scrollview);
        sv.setScrollViewListener(this);

        //Disagree_button.setClickable(true);
        //textView = (TextView) findViewById(R.id.firstLaunchTerms_textConsent);

        Agree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonAgree(view);
            }
        });


        Disagree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonDisagree(view);
            }
        });




        populateConsent();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        //Toast.makeText(this, Integer.toString(diff), 0.01).show();
        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            Agree_button.setEnabled(true);

            TextView text = (TextView) findViewById(R.id.firstLaunchTerms_please_scroll);
            text.setText(" ");
      //      Toast.makeText(this, "Now clickable", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_buttonAgree(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Agree button clicked, launching next activity");
        launchNextActivity(FirstLaunchProfileActivity.class);
    }

    public void onClick_buttonDisagree(@SuppressWarnings("UnusedParameters") View view) {
        Toast.makeText(this, "Too Bad", Toast.LENGTH_SHORT).show();
    }


    private void populateConsent() {
        try {
            InputStream termsInputStream = getResources().openRawResource(R.raw.terms);
            consent.setText(Util.convertStreamToString(termsInputStream));
            termsInputStream.close();
        } catch (IOException e) {
            Logger.e(TAG, "Error reading consent file");
            e.printStackTrace();
        }
    }

}
