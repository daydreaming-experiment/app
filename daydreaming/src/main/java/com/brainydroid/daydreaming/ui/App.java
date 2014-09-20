package com.brainydroid.daydreaming.ui;

import android.app.Application;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.network.PRNGFixes;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import roboguice.RoboGuice;

@ReportsCrashes(
        formKey = "",
        formUri = "http://crash-reports.daydreaming-the-app.net/acra-daydreaming/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
        formUriBasicAuthLogin="daydreaming-reporter",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class App extends Application {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "App";

    @Override
    public void onCreate() {
        Logger.d(TAG, "Creating Application");

        super.onCreate();

        // Fix SecureRandom vulnerability
        PRNGFixes.apply();

        // Initialize ACRA with the right password
        ACRA.init(this);
        ACRAConfiguration acraConfig = ACRA.getNewDefaultConfig(this);
        acraConfig.setFormUriBasicAuthPassword(getResources().getString(R.string.crash_pass));
        ACRA.setConfig(acraConfig);

        // Initialize RoboGuice
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ObjectMapperModule(),
                new AppModule());
    }

}
