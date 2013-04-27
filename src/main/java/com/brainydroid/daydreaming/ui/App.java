package com.brainydroid.daydreaming.ui;

import android.app.Application;
import android.util.Log;
import roboguice.RoboGuice;

public class App extends Application {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "App";

    @Override
    public void onCreate() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreate");
        }

        super.onCreate();

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new AppModule());
    }

}