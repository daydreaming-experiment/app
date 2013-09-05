package com.brainydroid.daydreaming.ui.ReOpen;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.TimePickerFragment;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_appsettings_layout)
public class AppSettingsActivity extends RoboActivity { //implements OnSharedPreferenceChangeListener {

    public static String TIME_FROM = "time_from";
    public static String TIME_UNTIL = "time_until";

    private static String TAG = "AppSettingsActivity";
    private static int MIN_WINDOW_HOURS = 5; // 5 hours (in hours)

   // private TimePreference timePreferenceMax;
   // private TimePreference timePreferenceMin;

    public static final String PREFS_FILE = "prefs";

   // public SharedPreferences sharedPreferences;

    @InjectView(R.id.settings_time_text_from) TextView textview_time_from;
    @InjectView(R.id.settings_time_text_until) TextView textview_time_until;

     // @InjectResource(R.pref.settings_time_window_lb_default) String defaultTimePreferenceMin;
   // @InjectResource(R.pref.settings_time_window_ub_default) String defaultTimePreferenceMax;

    //@InjectResource(R.string.settings_time_corrected_1) String timeCorrectedText1;
    //@InjectResource(R.string.settings_time_corrected_2) String timeCorrectedText2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);



       // setContentView(R.layout.activity_appsettings_layout);
         //  textview_time_from = (TextView)findViewById(R.id.settings_time_text_from);
         //  textview_time_until = (TextView)findViewById(R.id.settings_time_text_until);

        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);

        initVars();
        addListenerOnButton();
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();

        // Set up a listener for whenever a key changes
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        Logger.v(TAG, "Stopping");
        super.onStop();
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }



    @SuppressWarnings("deprecation")
    private void initVars() {
        Logger.d(TAG, "Initializing variables");


        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        timePreferenceMin = new TimePreference(getApplicationContext());
        //timePreferenceMin.setTime(sharedPreferences.getString("time_window_lb_key","00:00"));
//        timePreferenceMax = new TimePreference(getApplicationContext());
        //timePreferenceMax.setTime(sharedPreferences.getString("time_window_ub_key","00:00"));

        //timePreferenceMax = (TimePreference)preferenceScreen.findPreference("time_window_ub_key");


  //      timePreferenceMin.setSummary(timePreferenceMin.getTimeString());
  //      timePreferenceMax.setSummary(timePreferenceMax.getTimeString());
    }

    public void addListenerOnButton() {

        Logger.v(TAG, "Creating Listeners");

        //TODO: deal with loading defautls when no shared pref and sharedpref otherwise

        String Time_from = "09:00"; //sharedPreferences.getString(TIME_FROM,"00:00");
        String Time_until = "19:00"; // sharedPreferences.getString(TIME_UNTIL,"00:00");

        //textview_time_from.setText(Time_from);
        //textview_time_until.setText(Time_until);

        final int hour_from = Integer.parseInt(Time_from.substring(0, 2));
        final int minute_from = Integer.parseInt(Time_from.substring(3, 5));

        final int hour_until = Integer.parseInt(Time_until.substring(0, 2));
        final int minute_until = Integer.parseInt(Time_until.substring(3, 5));



        Logger.v(TAG, "Creating Listeners");

        textview_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hour_from, minute_from, DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //TODO Set text and save preference and relaunch scheduler if conflict
                        textview_time_from.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));
                    }
                };
                newFragment.show(getFragmentManager(), "timePicker_from");
            }
        });

        textview_time_until.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hour_until, minute_until, DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //TODO Set text and save preference and relaunch scheduler if conflict
                        textview_time_until.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));
                    }
                };
                newFragment.show(getFragmentManager(), "timePicker_until");
            }
        });


    }

    /*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Logger.d(TAG, "Preferences changed");

        if (key.equals("time_window_ub_key") || key.equals("time_window_lb_key")) {
            Logger.d(TAG, "Time window preferences_appsettings changed -> correcting " +
                    "it, updating summaries, and starting scheduler " +
                    "service");
            correctTimeWindow();
     //       timePreferenceMax.setSummary(timePreferenceMax.getTimeString());
     //       timePreferenceMin.setSummary(timePreferenceMin.getTimeString());
            startSchedulerService();
        }
    }
    */

    //TODO: redo the checks
    private void correctTimeWindow() {
        if (!checkTimeWindow()) {
            Logger.d(TAG, "Time window set by user is not allowed, " +
                    "correcting");
  //          timePreferenceMin.setTime(defaultTimePreferenceMin);
  //          timePreferenceMax.setTime(defaultTimePreferenceMax);
            // Toast.makeText(this, timeCorrectedText1 + " " + MIN_WINDOW_HOURS +
            //        " " + timeCorrectedText2, Toast.LENGTH_LONG).show();
        } else {
            Logger.w(TAG, "Time window set by user is OK");
        }
    }

    // TODO redo check time without time preference
    private boolean checkTimeWindow() {

        /**
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

        **/
        return true;
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

    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
    // see http://www.mkyong.com/android/android-time-picker-example/ for timepicker example.. we don't need time preference anymore
    public void onClick_time_from(View v){
        DialogFragment newFragment = new TimePickerFragment(){
            @Override
            public void onTimeSet(){
                //TODO Set text and save preference and relaunch scheduler if conflict
            }
        };
        newFragment.show(getFragmentManager(), "timePicker_from");
    }

    public void onClick_time_until(View v){
        DialogFragment newFragment = new TimePickerFragment(){
            @Override
            public void onTimeSet(){
                //TODO Set text and save preference and relaunch scheduler if conflict
            }
        };newFragment.show(getFragmentManager(), "timePicker_until");
    }
     */

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
