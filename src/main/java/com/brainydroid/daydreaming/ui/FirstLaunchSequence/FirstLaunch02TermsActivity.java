package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.Questions.ScrollViewExt;
import com.brainydroid.daydreaming.ui.Questions.ScrollViewListener;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;
/**
 * Activity at first launch
 * Display of terms of consent + requirement of consent
 *
 * In first launch sequence of apps
 *
 * Previous activity :  FirstLaunch01DescriptionActivity
 * This activity     :  FirstLaunch02TermsActivity
 * Next activity     :  FirstLaunch03ProfileActivity
 *
 */
@ContentView(R.layout.activity_first_launch_terms)

public class FirstLaunch02TermsActivity extends FirstLaunchActivity implements ScrollViewListener {

    private static String TAG = "FirstLaunch02TermsActivity";


    @InjectView(R.id.firstLaunchTerms_textConsent) TextView consent;
    @InjectView(R.id.firstLaunchTerms_moreConsent_button) TextView more_consent_button;
    @InjectView(R.id.firstLaunchTerms_moreConsent_text) TextView more_consent_text;

    @InjectView(R.id.firstLaunchTerms_Scrollview) ScrollViewExt sv;
    @InjectView(R.id.firstLaunchTerms_buttonAgree) Button Agree_button;
    @InjectView(R.id.firstLaunchTerms_buttonDisagree) Button Disagree_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        Ext_Checkfirstlaunch();

        setbuttons_and_scrollviewlistener();
        addagreementbuttonlistener();
        addinfobuttonlistener();
        consent.setText(Html.fromHtml(getString(R.string.terms_html)));
    }


    /**
     * called by scrollview listerner when scrollview position changes.
     * Enables agreement when text is scrolled down
     * @param scrollView
     * @param x
     * @param y
     * @param oldx
     * @param oldy
     */
    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        // if diff is zero, then the bottom has been reached and agreement button is set clickable.
        if (diff == 0) {
            Agree_button.setEnabled(true);
            TextView text = (TextView) findViewById(R.id.firstLaunchTerms_please_scroll);
            text.setText(" "); // Clear textview asking to scrolldown
        }
    }

    public void onClick_buttonAgree(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Agree button clicked, launching next activity");
        launchNextActivity(FirstLaunch03ProfileActivity.class);
    }

    /**
     * Clicking 'disagree'
     * @param view
     */
    public void onClick_buttonDisagree(@SuppressWarnings("UnusedParameters") View view) {
        Toast.makeText(this, "We require your agreement to proceed further. If you disagree with the terms, you should uninstall the app. No connection to the internet will be made.", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    /**
     * Loads terms res/raw/terms into adequate textview
     */

    public void Ext_Checkfirstlaunch(){
        checkFirstLaunch();
    }

    public void addinfobuttonlistener(){
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
        more_consent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        more_consent_text.setText(Html.fromHtml(getString(R.string.more_terms_html)));  }
        });

    }

    public void addagreementbuttonlistener(){
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
    }

    public  void setbuttons_and_scrollviewlistener(){
        Agree_button.setEnabled(false);
        Disagree_button.setEnabled(true);
        sv.setScrollViewListener(this);

    }

}
