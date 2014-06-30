package com.brainydroid.daydreaming.ui.firstlaunchsequence;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.ui.AlphaButton;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

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

    @InjectView(R.id.firstLaunchTerms_textConsent) protected TextView consent;
    @InjectView(R.id.firstLaunchTerms_moreConsent_button)
    protected TextView more_consent_button;
    @InjectView(R.id.firstLaunchTerms_moreConsent_text)
    protected TextView more_consent_text;
    @InjectView(R.id.firstLaunchTerms_Scrollview) ScrollViewExt sv;
    @InjectView(R.id.firstLaunchTerms_buttonAgree)
    protected AlphaButton agreeButton;
    @InjectView(R.id.firstLaunchTerms_buttonDisagree)
    protected AlphaButton disagreeButton;

    public boolean readtheterms = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        setButtonAndScrollViewListener();
        addAgreementButtonListener();
        addInfoButtonListener();
        consent.setText(Html.fromHtml(getString(R.string.terms_html)));
    }


    /**
     * called by ScrollView listener when ScrollView position changes.
     * Enables agreement when text is scrolled down
     */
    @TargetApi(11)
    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldX, int oldY) {
        // We take the last son in the ScrollView
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        // if diff is zero, then the bottom has been reached and agreement
        // button is set clickable.
        if (diff == 0) {
            agreeButton.setEnabled(true);
            TextView text = (TextView) findViewById(R.id.firstLaunchTerms_please_scroll);
            text.setVisibility(View.INVISIBLE); // Clear TextView asking to scroll down
            // Lint erroneously catches this as a call that requires API >= 11
            // (which is exactly why AlphaButton exists),
            // hence the @TargetApi(11) above.
            agreeButton.setAlpha(1f);
            agreeButton.setClickable(true);

            readtheterms = true;


        }
    }

    public void onClick_buttonAgree(@SuppressWarnings("UnusedParameters") View view) {
        Logger.v(TAG, "Agree button clicked, launching next activity");



        if (readtheterms == false){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Agreement");

            // set dialog message
            alertDialogBuilder
                    .setMessage("You haven't read the terms. Are you sure you want to carry on?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            launchNextActivity(FirstLaunch03ProfileActivity.class);
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();        }
        else {
            launchNextActivity(FirstLaunch03ProfileActivity.class);
        }


    }

    /**
     * Clicking 'disagree'
     */
    public void onClick_buttonDisagree(@SuppressWarnings("UnusedParameters") View view) {
        Toast.makeText(this, "We require your agreement to proceed further. If you disagree with the terms, you should uninstall the app. No connection to the internet will be made.", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    /**
     * Loads terms res/raw/terms into adequate TextView
     */

    public void addInfoButtonListener(){

        //agreeButton.setAlpha(0.5f);
        //agreeButton.setClickable(false);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonAgree(view);
            }
        });
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonDisagree(view);
            }
        });
        more_consent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        more_consent_text.setText(Html.fromHtml(getString(R.string.more_terms_html)));
        Linkify.addLinks(more_consent_text, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);


            }
        });

    }

    public void addAgreementButtonListener(){
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonAgree(view);
            }
        });
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_buttonDisagree(view);
            }
        });
    }

    public  void setButtonAndScrollViewListener(){
        //agreeButton.setEnabled(false);
        disagreeButton.setEnabled(true);
        sv.setScrollViewListener(this);

    }


    // Overriding parent method
    @Override
    public boolean shouldFinishIfTipiQuestionnaireCompleted() {
        return true;
    }

}
