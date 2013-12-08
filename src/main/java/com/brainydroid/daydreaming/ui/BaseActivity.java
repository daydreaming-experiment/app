package com.brainydroid.daydreaming.ui;

import android.os.Bundle;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.StatusManager;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;

/**
 * Class is only useful as a way to change theme globally
 */
public class BaseActivity extends RoboFragmentActivity {

    @Inject StatusManager statusManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (statusManager.iscurrentmodetest()){
            setTheme(R.style.MyCustomTheme_test);
        };
        super.onCreate(savedInstanceState);
    }

}
