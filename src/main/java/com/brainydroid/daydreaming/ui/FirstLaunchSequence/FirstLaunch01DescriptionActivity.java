package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Activity at first launch
 * Description of aims and goals of activity
 *
 * In first launch sequence of apps
 *
 * Previous activity :  FirstLaunch00WelcomeActivity
 * This activity     :  FirstLaunch01DescriptionActivity
 * Next activity     :  FirstLaunch02TermsActivity
 *
 */
@ContentView(R.layout.activity_first_launch_description)
public class FirstLaunch01DescriptionActivity extends FirstLaunchActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FirstLaunch01DescriptionActivity";
    @InjectView(R.id.firstLaunchDescription_textDescription) TextView description;

    protected ImageButton nextButton;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        nextButton = (ImageButton)findViewById(R.id.firstLaunchDescription_buttonNext);

        populateDescription();
        setButton();


        setRobotoFont(this);
        Linkify.addLinks(description,Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);

    }

    /**
     * OnClick Listener, launches next activity in sequence when next button of view is clicked on
     * @param view
     */
    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Next button clicked -> launching consent dialog");
        launchNextActivity(FirstLaunch02TermsActivity.class);
    }


    /**
     * Populating scroll view from raw text files
     */
    private void populateDescription() {
        try {
            InputStream termsInputStream = getResources().openRawResource(R.raw.description);
            description.setText(Util.convertStreamToString(termsInputStream));
            termsInputStream.close();
            description.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (IOException e) {
            Logger.e(TAG, "Error reading description file");
            e.printStackTrace();
        }
    }


    public void setButton(){
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setClickable(true);
    }

    /**
     * Terms activity separated in sequence.
     * Exiting at this point completely leaves the app.
     * @param activity
     */
    @Override
    protected void launchNextActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("nextClass", activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

}
