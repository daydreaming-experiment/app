package com.brainydroid.daydreaming.ui.Dashboard;

import android.text.Html;
import android.view.View;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.ui.FirstLaunchSequence.FirstLaunch02TermsActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_first_launch_terms)

public class TermsActivity extends FirstLaunch02TermsActivity {

    private static String TAG = "TermsActivity";

    @Override
    public  void setButtonAndScrollViewListener() {
        agreeButton.setEnabled(false);
        disagreeButton.setEnabled(false);
    }

    @Override
    public void addInfoButtonListener(){
        more_consent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_consent_text.setText(Html.fromHtml(getString(R.string.more_terms_html)));  }
        });
    }

    @Override
    public void addAgreementButtonListener() { }


    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    @Override
    public void checkFirstLaunch() { }

}