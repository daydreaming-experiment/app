package com.brainydroid.daydreaming.ui;

import android.app.Application;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.network.PRNGFixes;

import org.acra.ACRA;
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
        formUriBasicAuthPassword="faiRoh4geiphaPh4",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class App extends Application {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "App";

    @Override
    public void onCreate() {
        Logger.d(TAG, "Creating Application");

        super.onCreate();

        PRNGFixes.apply();
        ACRA.init(this);
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new AppModule());
    }

}
