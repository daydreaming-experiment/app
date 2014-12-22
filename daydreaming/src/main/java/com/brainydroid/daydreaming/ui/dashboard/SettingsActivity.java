package com.brainydroid.daydreaming.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.Json;
import com.brainydroid.daydreaming.db.ProfileStorage;
import com.brainydroid.daydreaming.db.Util;
import com.brainydroid.daydreaming.ui.FontUtils;
import com.brainydroid.daydreaming.ui.TimePickerFragment;
import com.brainydroid.daydreaming.ui.firstlaunchsequence.FirstLaunch00WelcomeActivity;
import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_appsettings_layout)
public class SettingsActivity extends RoboFragmentActivity {

    public static String TIME_FROM = "time_from";
    public static String TIME_UNTIL = "time_until";


    private static String TAG = "SettingsActivity";
    @SuppressWarnings("FieldCanBeLocal") private static int MIN_WINDOW_HOURS = 5; // 5 hours (in hours)

    public static String NOTIF_VIBRATION = "notification_vibrator_key";
    public static String NOTIF_BLINK = "notification_blink_key";
    public static String NOTIF_SOUND = "notification_sound_key";

//    @InjectView(R.id.appsettings_erase_data_layout)  LinearLayout eraseDataLayout;

    @InjectView(R.id.settings_time_text_from_layout)   LinearLayout fromLayout;
    @InjectView(R.id.settings_time_text_until_layout) LinearLayout untilLayout;

    @InjectView(R.id.settings_time_text_from) TextView tv_time_from;
    @InjectView(R.id.settings_time_text_until) TextView tv_time_until;

    @InjectView(R.id.appsettings_allow_blink_check) CheckBox blinkCheck;
    @InjectView(R.id.appsettings_allow_sound_check) CheckBox soundCheck;
    @InjectView(R.id.appsettings_allow_vibrations_check) CheckBox vibrationsCheck;

    @InjectView(R.id.appsettings_testmode_button) ImageButton testButton;

    @Inject SharedPreferences sharedPreferences;
    @Inject StatusManager statusManager;
    @Inject ProfileStorage profileStorage;


    @InjectResource(R.string.settings_time_window_lb_default) String defaultTimePreferenceMin;
    @InjectResource(R.string.settings_time_window_ub_default) String defaultTimePreferenceMax;

    private boolean testModeThemeActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "Creating");

        checkTestMode();
        super.onCreate(savedInstanceState);

        ViewGroup godfatherView = (ViewGroup)getWindow().getDecorView();
        FontUtils.setRobotoFont(this, godfatherView);

        initVars();
        addListenerOnButton();
        loadCheckBoxPreferences();
    }


    @Override
    public void onStart() {
        Logger.v(TAG, "Starting");
        checkTestModeActivatedDirty();
        super.onStart();
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "Resuming");
        checkTestModeActivatedDirty();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Logger.v(TAG, "Stopping");
        super.onStop();
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initVars() {
        Logger.d(TAG, "Initializing variables");
        updateTimeViews();
    }

    private String getNowString() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatted = format.format(cal.getTime());
        return formatted;
    }


//    public class BotherWindowMapWrapper {
//        private HashMap<String, String> botherWindowMap;
//
//        public BotherWindowMapWrapper() { }
//
//        public void setBotherWindowMap(HashMap<String, String> botherWindowMap_) {
//            this.botherWindowMap = botherWindowMap_;
//        }
//        public HashMap<String, String> getBotherWindowMap() {
//            return this.botherWindowMap;
//        }
//
//    }




    public void updateBotherWindowMap() {
        // get hashmap from shared preferences through profile
        HashMap<String, String> botherWindowMap = profileStorage.getBotherWindowMap();
        // update hashmap
        String timeFrom = sharedPreferences.getString(TIME_FROM, defaultTimePreferenceMin);
        String[] fromParts = timeFrom.split(":");
        final int minuteFrom = Integer.parseInt(fromParts[1]);
        final int hourFrom = Integer.parseInt(fromParts[0]);
        String timeUntil = sharedPreferences.getString(TIME_UNTIL, defaultTimePreferenceMax);
        String[] untilParts = timeUntil.split(":");
        final int minuteUntil = Integer.parseInt(untilParts[1]);
        final int hourUntil = Integer.parseInt(untilParts[0]);

        String botherwindow = "{'from':" + hourFrom + ":" + pad(minuteFrom) +
                ", 'until':" + hourUntil + ":" + pad(minuteUntil) + "}";
        botherWindowMap.put(getNowString(), botherwindow);
        // save hashmap into shared preferences and profile
        profileStorage.setBotherWindowMap(botherWindowMap);
    }

    public void addListenerOnButton() {

        Logger.v(TAG, "Creating Listeners");

//        eraseDataLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
//
//                // set title
//                alertDialogBuilder.setTitle("Erase Data");
//
//                // set dialog message
//                alertDialogBuilder
//                        .setMessage(getString(R.string.appsettings_warning_data_erase))
//                        .setCancelable(false)
//                        .setPositiveButton(getString(R.string.appsettings_dialog_button_erase_data),new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                eraseMyData();
//                            }
//                        })
//                        .setNegativeButton(getString(R.string.appsettings_dialog_button_keep_data),new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                // show it
//                alertDialog.show();
//                FontUtils.setRobotoToAlertDialog(alertDialog,SettingsActivity.this);
//            }
//        });

        fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeFrom = sharedPreferences.getString(TIME_FROM, defaultTimePreferenceMin);
                String[] fromParts = timeFrom.split(":");
                final int minuteFrom = Integer.parseInt(fromParts[1]);
                final int hourFrom = Integer.parseInt(fromParts[0]);

                DialogFragment fromPicker = new TimePickerFragment() {

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hourFrom, minuteFrom,
                                DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update shared preference
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TIME_FROM, hourOfDay + ":" + pad(minute));
                        editor.apply();

                        // Listener can correct and update the view here
                        correctTimeWindow();
                        updateTimeViews();
                        statusManager.launchNotifyingServices();
                        updateBotherWindowMap();

                    }

                };

                fromPicker.show(getSupportFragmentManager(), "timePicker_from");
            }
        });

        untilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeUntil = sharedPreferences.getString(TIME_UNTIL, defaultTimePreferenceMax);
                String[] untilParts = timeUntil.split(":");
                final int minuteUntil = Integer.parseInt(untilParts[1]);
                final int hourUntil = Integer.parseInt(untilParts[0]);

                DialogFragment untilPicker = new TimePickerFragment() {

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new TimePickerDialog(getActivity(), this, hourUntil, minuteUntil,
                                DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update shared preference
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TIME_UNTIL, hourOfDay + ":" + pad(minute));
                        editor.apply();

                        // Listener can correct and update the view here
                        correctTimeWindow();
                        updateTimeViews();
                        statusManager.launchNotifyingServices();
                        updateBotherWindowMap();
                    }

                };

                untilPicker.show(getSupportFragmentManager(), "timePicker_until");
            }
        });

        blinkCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_BLINK, blinkCheck.isChecked()); // value to store
                editor.apply();
            }

        });

        vibrationsCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_VIBRATION, vibrationsCheck.isChecked()); // value to store
                editor.apply();
            }

        });

        soundCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIF_SOUND, soundCheck.isChecked()); // value to store
                editor.apply();
            }

        });

        testButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Logger.d(TAG, "Mode switch button clicked");

                if (statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
                    Logger.v(TAG, "We're in test mode, initiating switch to prod");
                    testToProdDialog();
                } else if (statusManager.getCurrentMode() == StatusManager.MODE_PROD) {
                    Logger.v(TAG, "We're in prod mode, initiating switch to test");
                    prodToTestDialog();
                }
                return true;
            }
        });

    }

//    private void eraseMyData() {
//        //TODO : call erase on server, nothing to do locally?
//    }

    private void correctTimeWindow() {
        if (!checkTimeWindow()) {
            Logger.d(TAG, "Time window set by user is too short, correcting");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TIME_FROM, defaultTimePreferenceMin);
            editor.putString(TIME_UNTIL, defaultTimePreferenceMax);
            editor.apply();
        } else {
            Logger.v(TAG, "Time window set by user is OK");
        }
    }

    private boolean checkTimeWindow() {
        Logger.v(TAG, "Checking time window");

        String timeFirst = sharedPreferences.getString(TIME_FROM, defaultTimePreferenceMin);
        String timeLast = sharedPreferences.getString(TIME_UNTIL, defaultTimePreferenceMax);

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
     * Update the view from shared preferences.
     */
    private void updateTimeViews(){
        Logger.d(TAG, "Updating view");
        String Time_from = sharedPreferences.getString(TIME_FROM, defaultTimePreferenceMin);
        String Time_until = sharedPreferences.getString(TIME_UNTIL, defaultTimePreferenceMax);
        tv_time_from.setText(Time_from);
        tv_time_until.setText(Time_until);
    }

    @Override
    public void onBackPressed() {
        Logger.v(TAG, "Back pressed, setting slide transition");
        super.onBackPressed();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public void onClick_backToDashboard(@SuppressWarnings("UnusedParameters") View v) {
        Logger.v(TAG, "Back to dashboard button clicked");
        onBackPressed();
    }

    public void onClick_switchMode(@SuppressWarnings("UnusedParameters") View v) {
        Logger.d(TAG, "Mode switch button clicked");

        if (statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
            Logger.v(TAG, "We're in test mode, initiating switch to prod");
            testToProdDialog();
        } else if (statusManager.getCurrentMode() == StatusManager.MODE_PROD) {
            Logger.v(TAG, "We're in prod mode, initiating switch to test");
            prodToTestDialog();
        }
    }

    public void testToProdDialog(){
        Logger.v(TAG, "Proposing to switch back to prod mode");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
        .setTitle("Production mode")
        .setMessage("Switch back to production mode?")
        .setCancelable(false)
        .setPositiveButton("Go into production", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Logger.d(TAG, "User accepted switch to prod mode -> switching");

                statusManager.switchToProdMode();
                Toast.makeText(getApplicationContext(), "Switched back to production mode", Toast.LENGTH_SHORT).show();

                // Relaunch scheduling to refresh notifications
                statusManager.launchNotifyingServices();

                // Restart Dashboard
                Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        })
        .setNegativeButton("Stay in test mode", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Logger.d(TAG, "User cancelled switch to prod mode");
                dialog.cancel();
            }

        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        FontUtils.setRobotoToAlertDialog(alertDialog,SettingsActivity.this);
    }

    public void prodToTestDialog(){
        Logger.v(TAG, "Proposing to switch from prod to test mode");

        // Set an EditText view to get user input
        final EditText inputPass = new EditText(this);
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog testAlert = new AlertDialog.Builder(this)
        .setTitle("Test mode")
        .setMessage("Type the password to switch to test mode")
        .setView(inputPass)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String enteredPass = inputPass.getText().toString();
                String truePass = getResources().getString(R.string.mode_switch_pass);

                if (enteredPass.equals(truePass)) {
                    Logger.d(TAG, "Good password to switch to test mode -> switching");

                    statusManager.switchToTestMode();
                    Toast.makeText(getApplicationContext(), "Switched to test mode", Toast.LENGTH_SHORT).show();

                    // Don't relaunch scheduling since we go through first launch again.

                    // Restart first launch
                    Intent intent = new Intent(SettingsActivity.this, FirstLaunch00WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Logger.d(TAG, "Wrong password to switch to test mode -> aborting");
                    Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                }

            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Logger.d(TAG, "User cancelled switch to test mode");
                dialog.cancel();
            }
        })
        .create();

        testAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        testAlert.show();
        FontUtils.setRobotoToAlertDialog(testAlert, this);
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + String.valueOf(c);
        }
    }

    public void loadCheckBoxPreferences() {
        Logger.v(TAG, "Loading checkBox preferences");
        blinkCheck.setChecked(sharedPreferences.getBoolean(NOTIF_BLINK, true));
        soundCheck.setChecked(sharedPreferences.getBoolean(NOTIF_SOUND, true));
        vibrationsCheck.setChecked(sharedPreferences.getBoolean(NOTIF_VIBRATION, true));
    }

    public void checkTestMode() {
        Logger.d(TAG, "Checking test mode status");
        if (StatusManager.getCurrentModeStatic(this) == StatusManager.MODE_PROD) {
            Logger.d(TAG, "Setting production theme");
            setTheme(R.style.daydreamingTheme);
            testModeThemeActivated = false;
        } else {
            Logger.d(TAG, "Setting test theme");
            setTheme(R.style.daydreamingTestTheme);
            testModeThemeActivated = true;
        }
    }

    private void checkTestModeActivatedDirty() {
        if ((statusManager.getCurrentMode() == StatusManager.MODE_TEST && !testModeThemeActivated)
            || (statusManager.getCurrentMode() == StatusManager.MODE_PROD && testModeThemeActivated)) {
            Logger.w(TAG, "Test/production mode theme discrepancy, " +
                    "meaning a vicious activity path didn't let us update");
            finish();
        } else {
            Logger.v(TAG, "No test mode theming discrepancy");
        }
    }




}
