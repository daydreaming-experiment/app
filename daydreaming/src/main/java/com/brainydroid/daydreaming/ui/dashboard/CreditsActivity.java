package com.brainydroid.daydreaming.ui.dashboard;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.ui.FontUtils;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_credits)
public class CreditsActivity extends RoboFragmentActivity {

    private static String TAG = "CreditsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        checkTestMode();
        ViewGroup godfatherView = (ViewGroup) this.getWindow().getDecorView();

        super.onCreate(savedInstanceState);
        FontUtils.setRobotoFont(this, godfatherView);

        TextView credits_website_link = (TextView)findViewById(R.id.credits_website_link);
        Linkify.addLinks(credits_website_link, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        credits_website_link.setLinkTextColor(getResources().getColor(R.color.ui_dark_blue_color));
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backToDashboard(@SuppressWarnings("UnusedParameters") View v) {
        Logger.v(TAG, "Back to dashboard button clicked");
        onBackPressed();
    }

    public void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.daydreamingTheme);
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.daydreamingTestTheme);
        }
    }

}