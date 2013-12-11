package com.brainydroid.daydreaming.ui.Dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.LocationPointsStorage;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.network.CryptoStorage;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.TimePickerFragment;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_appsettings_layout)
public class SettingsActivity extends RoboFragmentActivity {

    public static String TIME_FROM = "time_from";
    public static String TIME_UNTIL = "time_until";

    private static String TAG = "SettingsActivity";
    private static int MIN_WINDOW_HOURS = 5; // 5 hours (in hours)

    private static String NOTIF_VIBRATION = "notification_vibrator_key";
    private static String NOTIF_BLINK = "notification_blink_key";
    private static String NOTIF_SOUND = "notification_sound_key";

    @InjectView(R.id.settings_time_text_from_layout)   LinearLayout layout_time_from;
    @InjectView(R.id.settings_time_text_until_layout) LinearLayout layout_time_until;

    @InjectView(R.id.settings_time_text_from) TextView tv_time_from;
    @InjectView(R.id.settings_time_text_until) TextView tv_time_until;

    @InjectView(R.id.appsettings_allow_blink_check) CheckBox blink_check;
    @InjectView(R.id.appsettings_allow_sound_check) CheckBox sound_check;
    @InjectView(R.id.appsettings_allow_vibrations_check) CheckBox vibrations_check;

    @Inject SharedPreferences sharedPreferences;
    @Inject StatusManager statusManager;
    @Inject ProfileStorage profileStorage;
    @Inject CryptoStorage cryptoStorage;
    @Inject PollsStorage pollsStorage;
    @Inject LocationPointsStorage locationPointsStorage;

    @InjectResource(R.pref.settings_time_window_lb_default) String defaultTimePreferenceMin;
    @InjectResource(R.pref.settings_time_window_ub_default) String defaultTimePreferenceMax;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");
        super.onCreate(savedInstanceState);

        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);

        initVars();
        addListenerOnButton();
        loadcheckboxfrompreference();
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "Resuming");
        super.onResume();

        // Set up a listener for whenever a key changes
        // sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        updateTimeViews();
    }

    public void addListenerOnButton() {

        Logger.v(TAG, "Creating Listeners");

        //TODO: deal with loading defautls when no shared pref and sharedpref otherwise

        Logger.v(TAG, "Creating Listeners");

        layout_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Time_from = sharedPreferences.getString(TIME_FROM,defaultTimePreferenceMin);
                final int minute_from = Integer.parseInt(Time_from.substring(3, 5));
                final int hour_from = Integer.parseInt(Time_from.substring(0, 2));

                DialogFragment newFragment = new TimePickerFragment() {

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hour_from, minute_from, DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //TODO Set text and save preference and relaunch scheduler if conflict

                        StringBuilder sb = new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TIME_FROM, sb.toString()); // value to store
                        editor.commit();

                        // listener will here eventually correct and update the view
                        correctTimeWindow();
                        updateTimeViews();
                        startSchedulerService();

                    }
                };
                newFragment.show(getSupportFragmentManager(),
                        "timePicker_from");
            }
        });

        layout_time_until.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Time_until = sharedPreferences.getString(TIME_UNTIL,defaultTimePreferenceMax);
                final int minute_until = Integer.parseInt(Time_until.substring(3, 5));
                final int hour_until = Integer.parseInt(Time_until.substring(0, 2));

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hour_until, minute_until, DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //TODO Set text and save preference and relaunch scheduler if conflict

                        // update sharedpreference
                        StringBuilder sb = new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TIME_UNTIL, sb.toString()); // value to store
                        editor.commit();

                        // listener will here eventually correct and update the view
                        correctTimeWindow();
                        updateTimeViews();
                        startSchedulerService();


                    }
                };
                newFragment.show(getSupportFragmentManager(),
                        "timePicker_until");
            }
        });

        blink_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_BLINK, blink_check.isChecked()); // value to store
                editor.commit();     }
        });

        vibrations_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_VIBRATION, vibrations_check.isChecked()); // value to store
                editor.commit();        }
        });

        sound_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_SOUND, sound_check.isChecked()); // value to store
                editor.commit();        }
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
            updateTimeViews();
            startSchedulerService();
        }
    }
             */

    //TODO: redo the checks
    private void correctTimeWindow() {
        if (!checkTimeWindow()) {
            Logger.d(TAG, "Time window set by user is not allowed, " +
                    "correcting");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TIME_FROM, defaultTimePreferenceMin); // value to store
            editor.putString(TIME_UNTIL, defaultTimePreferenceMax); // value to store
            editor.commit();

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


        String timeFirst = sharedPreferences.getString(TIME_FROM,defaultTimePreferenceMin);
        String timeLast = sharedPreferences.getString(TIME_UNTIL,defaultTimePreferenceMax);

        int firstHour = Util.getHour(timeFirst);
        int firstMinute = Util.getMinute(timeFirst);
        int first = firstHour * 60 + firstMinute;

        int lastHour = Util.getHour(timeLast);
        int lastMinute = Util.getMinute(timeLast);
        int last = lastHour * 60 + lastMinute;

        if (last < first) {
            // The time window goes through midnight
            last += 24 * 60;
        }

        return (first + MIN_WINDOW_HOURS * 60) <= last;

    }

    /**
     * Update the view from sharedpreferences
     */
    private void updateTimeViews(){
        String Time_from = sharedPreferences.getString(TIME_FROM,defaultTimePreferenceMin);
        String Time_until = sharedPreferences.getString(TIME_UNTIL,defaultTimePreferenceMax);
        tv_time_from.setText(Time_from);
        tv_time_until.setText(Time_until);
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
        //overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);   // restart to launch oncreate
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backtodashboard(View v) {
        onBackPressed();
    }

    public void onClick_switchmode(View v) {
        // TODO
        // high level description
        if (statusManager.isCurrentModeTest()){
            // simple dialog proposing to switch back to prod mode
            testtoprod_dialog();
        } else if (statusManager.isCurrentModeProd()){
            prodtotest_dialog();
        }

        // -- propose switch to standard mode
        // if prod
        // -- propose to switch to test mode
        // ---- ask for password
        // ---- if correct
        // ------ setcurrentmode('prod')
        // ------ create new profile
        // ------ switch profile
        // endif
    }


    public void testtoprod_dialog(){
        Logger.v(TAG, "Proposing to switch back to prod mode");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Mode change");

        // set dialog message
        alertDialogBuilder
                .setMessage("Back to production mode?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // removing pending
                        pollsStorage.removeUploadablePolls();
                        locationPointsStorage.removeUploadablePolls();
                        // switch to prod
                        statusManager.setCurrentModeToProd();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();        }

    public void prodtotest_dialog(){
        Logger.v(TAG, "Proposing to switch from prod to test mode");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Mode Switch");
        alert.setMessage("Insert pass to switch to test mode");

        // Set an EditText view to get user input
        final EditText input_pass = new EditText(this);
        alert.setView(input_pass);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String entered_pass = input_pass.getText().toString();
                String true_pass = getResources().getString(R.string.mode_switch_pass);
                if (entered_pass.equals(true_pass)){

                    // remove pending
                    pollsStorage.removeUploadablePolls();
                    locationPointsStorage.removeUploadablePolls();
                    // switch to test
                    statusManager.setCurrentModeToTest();
                    // clear test
                    profileStorage.clearProfile();
                    cryptoStorage.clearStore();
                    Toast.makeText(getApplicationContext(),"mode set to test",Toast.LENGTH_SHORT).show();
                    // set firstlaunch sequence to not complete and reset
                    statusManager.setFirstLaunchNotCompleted();
                    statusManager.setQuestionsNotUpdated();
                    statusManager.setTipiQuestionnaireNotCompleted(); //Todo : share the questionnaires when creating test profile. redoing is a pain
                    onBackPressed();

                } else {
                    Toast.makeText(getApplicationContext(),"wrong pass",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();  }




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


    public void loadcheckboxfrompreference(){

        blink_check.setChecked(sharedPreferences.getBoolean(NOTIF_BLINK,true));
        sound_check.setChecked(sharedPreferences.getBoolean(NOTIF_SOUND,true));
        vibrations_check.setChecked(sharedPreferences.getBoolean(NOTIF_VIBRATION,true));


    }


}
