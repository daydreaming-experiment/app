package com.brainydroid.daydreaming.ui.dashboard;

import android.os.Bundle;
import android.view.ViewGroup;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_about)
public class AboutActivity extends RoboFragmentActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AboutActivity";

    @Inject StatusManager statusManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        checkTestMode();
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        super.onCreate(savedInstanceState);
    }

    public void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.MyCustomTheme);
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.MyCustomTheme_test);
        }
    }

}
