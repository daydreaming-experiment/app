package com.brainydroid.daydreaming.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionsStorage;
import com.brainydroid.daydreaming.db.Util;
import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.io.InputStream;

// FIXME: all this activity should go in the background
@ContentView(R.layout.activity_first_launch_pull2)
public class FirstLaunch06PullActivity2 extends FirstLaunchActivity {

    private static String TAG = "FirstLaunch06PullActivity2";

    @InjectView(R.id.firstLaunchPull_text_downloading) TextView textDownloading;
   // @InjectView(R.id.firstLaunchPull_text_data_enabled) TextView dataEnabled;
   // @InjectView(R.id.firstLaunchPull_text_change_settings) TextView textSettings;
    @InjectView(R.id.firstLaunchPull_buttonNext) ImageButton buttonNext;

    @Inject QuestionsStorage questionsStorage;

    private boolean areQuestionsDownloaded = false;
    private static CountDownTimer Timer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        ImageView MyImageView = (ImageView)findViewById(R.id.firstLaunchPull_loading_image);
        MyImageView.setBackgroundResource(R.drawable.animated_loading);
        AnimationDrawable AniFrame = (AnimationDrawable) MyImageView.getBackground();
        AniFrame.start();


        Timer =  new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                //launchNextActivity(FirstLaunch01DescriptionActivity.class);

//                launchNextActivity(FirstLaunch04PersonalityQuestionnaireActivity.class);

                updateView();

                Timer.cancel();
                //finish();
            }
        }.start();

    }

    @Override
    public void onStart() {
        super.onStart();
//        updateView();

    }

    // FIXME : what does this do?
    private void updateView() {

     //   dataEnabled.setCompoundDrawablesWithIntrinsicBounds(
      //          (statusManager.isDataEnabled() | Build.FINGERPRINT.startsWith("generic")) ?
       //                 R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

        textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                areQuestionsDownloaded ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);

        if (Build.FINGERPRINT.startsWith("generic")) {

            if (!areQuestionsDownloaded) {
                loadQuestionsFromRes();
            }

        } else {

            if (areQuestionsDownloaded) {
                Toast.makeText(this, "Questions already downloaded", Toast.LENGTH_LONG).show();
            } else {
                if (statusManager.isDataEnabled()){
                    Toast.makeText(this, "Downloading questions", Toast.LENGTH_LONG).show();

                    loadQuestionsFromRes();
                    updateRequestAdjustSettings();
                }
            }

        }
    }

    private void updateRequestAdjustSettings() {

        if (Build.FINGERPRINT.startsWith("generic")){
                setAdjustSettingsOff();
        } else {
            if  (statusManager.isDataEnabled()) {
                setAdjustSettingsOff();
            } else {
                setAdjustSettingsNecessary();
            }
        }
    }

    @TargetApi(11)
    private void setAdjustSettingsNecessary() {

//        textSettings.setText(R.string.firstLaunchPull_text_settings_necessary);
//        textSettings.setVisibility(View.VISIBLE);
//        buttonSettings.setVisibility(View.VISIBLE);
//        buttonSettings.setClickable(true);
        forbidNextButton();
    }

    @TargetApi(11)
    private void forbidNextButton() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(0.3f);
        } else {
            buttonNext.setVisibility(View.INVISIBLE);
        }

        buttonNext.setClickable(false);
    }

    @TargetApi(11)
    private void allowNextButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            buttonNext.setAlpha(1f);
        } else {
            buttonNext.setVisibility(View.VISIBLE);
        }

        buttonNext.setClickable(true);

        Toast.makeText(this, "Allowing next button", Toast.LENGTH_LONG).show();
    }

    @TargetApi(11)
    private void setAdjustSettingsOff() {

  //      textSettings.setVisibility(View.INVISIBLE);
  //      buttonSettings.setVisibility(View.INVISIBLE);
  //      buttonSettings.setClickable(false);
    }

    public void onClick_buttonSettings(@SuppressWarnings("UnusedParameters") View view) {
        launchSettings();
    }

    private void launchSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(settingsIntent);
    }

    public void onClick_buttonNext(@SuppressWarnings("UnusedParameters") View view) {
        statusManager.setFirstLaunchCompleted();
        launchNextActivity(DashboardActivity.class);
    }

    // FIXME: this is already in QuestionsStorage
    private void loadQuestionsFromRes() {
        textDownloading.setText("Downloading...");

        if (Build.FINGERPRINT.startsWith("generic") ) {
            Toast.makeText(this, "skipping download : emulator", Toast.LENGTH_LONG).show();

            areQuestionsDownloaded = true;
            textDownloading.setText("Questions loaded from resource");
            textDownloading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);

            allowNextButton();
        } else {
            try {
                // TODO: change this to download from the Internet
                InputStream questionsIS = getResources().openRawResource(R.raw.questions);
                questionsStorage.importQuestions(Util.convertStreamToString(questionsIS));
                questionsIS.close();

                areQuestionsDownloaded = true;
                textDownloading.setText("Questions loaded from internal resource");
                allowNextButton();

                textDownloading.setCompoundDrawablesWithIntrinsicBounds(
                        areQuestionsDownloaded ? R.drawable.ic_check : R.drawable.ic_cross, 0, 0, 0);
            } catch (IOException e) {
                // Error
                Log.e(TAG, "Error importing questions from local resource", e);
                e.printStackTrace();
                textDownloading.setText("Oops, there was an error loading questions. Can you talk to the developers?");
            }
        }
    }

}
