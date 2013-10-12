package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.text.Html;
import android.view.View;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_first_launch_terms)

public class ReOpenTermsActivity extends FirstLaunch02TermsActivity {

    private static String TAG = "FirstLaunch02TermsActivity";

    @Override
    public void Ext_Checkfirstlaunch(){}

    @Override
    public  void setbuttons_and_scrollviewlistener(){
        Agree_button.setEnabled(false);
        Disagree_button.setEnabled(false);
    }

    @Override
    public void addinfobuttonlistener(){
        more_consent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_consent_text.setText(Html.fromHtml(getString(R.string.more_terms_html)));  }
        });
    }

    @Override
    public void addagreementbuttonlistener(){ }


    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }


}