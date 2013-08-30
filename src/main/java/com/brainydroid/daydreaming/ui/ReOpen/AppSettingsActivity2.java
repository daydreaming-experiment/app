package com.brainydroid.daydreaming.ui.ReOpen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.FirstLaunchSequence.DashboardActivity;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.TimePreference;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;

public class AppSettingsActivity2 extends Activity
        implements OnSharedPreferenceChangeListener {

    private static String TAG = "AppSettingsActivity";
    private static int MIN_WINDOW_HOURS = 5; // 5 hours (in hours)

    private TimePreference timePreferenceMax;
    private TimePreference timePreferenceMin;

    public static final String PREFS_FILE = "prefs";

    SharedPreferences sharedPreferences;


    @InjectResource(R.pref.settings_time_window_lb_default) String defaultTimePreferenceMin;
    @InjectResource(R.pref.settings_time_window_ub_default) String defaultTimePreferenceMax;
    //@InjectResource(R.string.settings_time_corrected_1) String timeCorrectedText1;
    //@InjectResource(R.string.settings_time_corrected_2) String timeCorrectedText2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettings_layout);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);
        initVars();
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();

        // Set up a listener for whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        Logger.v(TAG, "Stopping");
        super.onStop();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }



    @SuppressWarnings("deprecation")
    private void initVars() {
        Logger.d(TAG, "Initializing variables");

        // Load the XML preferences_appsettings file
        //addPreferencesFromResource(R.layout.preferences_appsettings);

        //PreferenceScreen preferenceScreen = getPreferenceScreen();
        //sharedPreferences = preferenceScreen.getSharedPreferences();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get a reference to the preferences_appsettings
        //timePreferenceMin = (TimePreference)preferenceScreen.findPreference("time_window_lb_key");
        //timePreferenceMax = (TimePreference)preferenceScreen.findPreference("time_window_ub_key");

        timePreferenceMin = new TimePreference(getApplicationContext());
        //timePreferenceMin.setTime(sharedPreferences.getString("time_window_lb_key","00:00"));
        timePreferenceMax = new TimePreference(getApplicationContext());
        //timePreferenceMax.setTime(sharedPreferences.getString("time_window_ub_key","00:00"));

        //timePreferenceMax = (TimePreference)preferenceScreen.findPreference("time_window_ub_key");


        timePreferenceMin.setSummary(timePreferenceMin.getTimeString());
        timePreferenceMax.setSummary(timePreferenceMax.getTimeString());
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Logger.d(TAG, "Preferences changed");

        if (key.equals("time_window_ub_key") || key.equals("time_window_lb_key")) {
            Logger.d(TAG, "Time window preferences_appsettings changed -> correcting " +
                    "it, updating summaries, and starting scheduler " +
                    "service");
            correctTimeWindow();
            timePreferenceMax.setSummary(timePreferenceMax.getTimeString());
            timePreferenceMin.setSummary(timePreferenceMin.getTimeString());
            startSchedulerService();
        }
    }

    private void correctTimeWindow() {
        if (!checkTimeWindow()) {
            Logger.d(TAG, "Time window set by user is not allowed, " +
                    "correcting");
            timePreferenceMin.setTime(defaultTimePreferenceMin);
            timePreferenceMax.setTime(defaultTimePreferenceMax);
           // Toast.makeText(this, timeCorrectedText1 + " " + MIN_WINDOW_HOURS +
            //        " " + timeCorrectedText2, Toast.LENGTH_LONG).show();
        } else {
            Logger.w(TAG, "Time window set by user is OK");
        }
    }

    private boolean checkTimeWindow() {
        String timeFirst = timePreferenceMin.getTimeString();
        int firstHour = Util.getHour(timeFirst);
        int firstMinute = Util.getMinute(timeFirst);
        int first = firstHour * 60 + firstMinute;

        String timeLast = timePreferenceMax.getTimeString();
        int lastHour = Util.getHour(timeLast);
        int lastMinute = Util.getMinute(timeLast);
        int last = lastHour * 60 + lastMinute;

        if (last < first) {
            // The time window goes through midnight
            last += 24 * 60;
        }

        return (first + MIN_WINDOW_HOURS * 60) <= last;
    }

    private void startSchedulerService() {
        Logger.d(TAG, "Starting SchedulerService");
        Intent schedulerIntent = new Intent(this, SchedulerService.class);
        startService(schedulerIntent);
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
    }
    public void onClick_backtodashboard(View v) {
        onBackPressed();

    }


    // see http://www.mkyong.com/android/android-time-picker-example/ for timepicker example.. we don't need time preference anymore
    public void onClick_time_from(View v){

    }

    public void onClick_time_until(View v){

    }



}
