package com.brainydroid.daydreaming.ui.FirstLaunchSequence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.ui.ReOpen.*;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_dashboard)
public class DashboardActivity extends FirstLaunchActivity {

    private static String TAG = "DashboardActivity";

    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        //updateRunningTime();
        super.onStart();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        CheckFirstLaunch();
//        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
//        FontUtils.setRobotoFont(this, godfatherView);
       // ImageView imgStartButton = (ImageView) findViewById(R.id.dashboard_ExperimentTimeElapsed);
       // imgStartButton.setBackgroundResource(R.drawable.timeelapsed);

    //    TextView textView = (TextView)findViewById(R.id.dashboard_textExperimentRunning);
    //    Spannable WordtoSpan = new SpannableString("EXPERIMENT IS RUNNING");
    //    WordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 14, 20,
    //            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    //    textView.setText(WordtoSpan);
    }


    /*
    // TODO: check this is ok with real ActionBar API
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logger.v(TAG, "Creating options menu");
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }
    */

    // We don't need this anymore since we do not work with action bar anylonger
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d(TAG, "OptionsItem selected");

        switch (item.getItemId()) {
        case R.id.menu_settings:
            Logger.d(TAG, "Launching settings");
            Intent intent = new Intent(this, AppSettingsActivity.class);
            startActivity(intent);
            break;

        // No other cases for now
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * Launching app settings activity
     * @param view
     */
    public void  onClick_openAppSettings(View view){

        Intent intent = new Intent(this, AppSettingsActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
       // overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);

    }

    /**
     * Launching term activity without enabling buttons
     * Only exit is pressing back button
     * @param view
     */
    public void  onClick_REopenTerms(View view){

        Intent intent = new Intent(this, ReOpenTermsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);

    }

    /**
     *
     * Launching a description activity without possibility to click next
     * Only exit is pressing back button.
     * @param view
     */
    public void  onClick_REopenDescription(View view){

        Intent intent = new Intent(this, ReOpenDescriptionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);

    }


    //TODO: Layout of an about activity
    public void  onClick_OpenAboutActivity(View view){

        Intent intent = new Intent(this, AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);

    }

    //TODO: Decide what should happen when resutls button is cliked on.
    public void  onClick_SeeResults(View view){
        Toast.makeText(this, "Not yet!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Completely leave the app when back button is pressed
     */
    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed");
        // Don't overridePendingTransition (not calling super)
        finish();
    }

//    // TODO: clean this up
//    private void updateRunningTime() {
//
//        // Transform string to Date object
//        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
//
//        // Get today just in case
//        String todayDateString = format.format(new Date());
//
//        // Read Start date string from preferences_appsettings
//        SharedPreferences sharedPreferences = getSharedPreferences("startDatePref", 0);
//        String startDateString = sharedPreferences.getString("startDateString", todayDateString);
//
//        try {
//            Date startDate = format.parse(startDateString);
//
//            // Compute time difference from Date objects:
//            // getTime : number of milliseconds since 1 January 1970 00:00:00 UTC
//            long dt =  (new Date()).getTime() - startDate.getTime() ;
//            dt /= 1000 * 60 * 60;
//            int hours = (int)(dt % 24);
//            dt /= 24;
//            int days = (int)(dt);
//
//            String elapsedTime =  Integer.toString(days) + " days " + Integer.toString(hours) + " hours";
//            TextView textView = (TextView)findViewById(R.id.dashboard_textDaysElapsedNumber);
//            textView.setText(elapsedTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Dashboard is main launcher. At start up of app, the whole firstlaunch sequence is ran.
     * If they were already ran, user directly end up on dashboard layout.
     */



    protected void CheckFirstLaunch() {
        if (!statusManager.isFirstLaunchCompleted()) {
            Logger.i(TAG, "First launch not completed -> starting first " +
                    "launch sequence and finishing this activity");
            Intent intent = new Intent(this, FirstLaunch00WelcomeActivity.class);
//           Intent intent = new Intent(this, AppSettingsActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            finish();
        } else {
            Logger.v(TAG, "First launch completed");
        }
    }


    /**                                          *
     * Launchinjg poll from dashboard (debug)
     * @param view
     */
    public void runPollNow(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Launching a debug poll");

        Intent pollIntent = new Intent(this, SchedulerService.class);
        pollIntent.putExtra(SchedulerService.SCHEDULER_DEBUGGING, true);
        startService(pollIntent);

        Toast.makeText(this, "Now wait for 5 secs", Toast.LENGTH_SHORT).show();
    }

}
