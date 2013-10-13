package com.brainydroid.daydreaming.ui;

import android.app.Application;
import com.brainydroid.daydreaming.background.Logger;
import roboguice.RoboGuice;

public class App extends Application {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "App";

    @Override
    public void onCreate() {
        Logger.d(TAG, "Creating Application");

        super.onCreate();

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new AppModule());
    }

}
