package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d(TAG, "OptionsItem selected");

        switch (item.getItemId()) {
        case R.id.menu_settings:
            Logger.d(TAG, "Launching settings");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            break;

        // No other cases for now
        }

        return super.onOptionsItemSelected(item);
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
//        // Read Start date string from preferences
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

    @Override
    protected void checkFirstLaunch() {
        if (!statusManager.isFirstLaunchCompleted()) {
            Logger.i(TAG, "First launch not completed -> starting first " +
                    "launch sequence and finishing this activity");
            Intent intent = new Intent(this, FirstLaunchWelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            finish();
        } else {
            Logger.v(TAG, "First launch completed");
        }
    }

    public void runPollNow(@SuppressWarnings("UnusedParameters") View view) {
        Logger.d(TAG, "Launching a debug poll");

        Intent pollIntent = new Intent(this, SchedulerService.class);
        pollIntent.putExtra(SchedulerService.SCHEDULER_DEBUGGING, true);
        startService(pollIntent);

        Toast.makeText(this, "Now wait for 5 secs", Toast.LENGTH_SHORT).show();
    }

}
