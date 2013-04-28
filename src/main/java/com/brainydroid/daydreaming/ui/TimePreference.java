package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import com.brainydroid.daydreaming.db.Util;

// Extends DialogPreference, a preference class that pops out in a dialog box
public class TimePreference extends DialogPreference {

    private static String TAG = "TimePreference";

    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;

    // constructor from context
    @SuppressWarnings("UnusedDeclaration")
    public TimePreference(Context context) {

        super(context, null); // constructor from superclass DialogPreference

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] TimePreference (argset 1: small)");
        }
    }

    // constructor from context and attributes
    @SuppressWarnings("UnusedDeclaration")
    public TimePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] TimePreference (argset 2: medium)");
        }
    }

    // constructor from context and attributes and style
    @SuppressWarnings("UnusedDeclaration")
    public TimePreference(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] TimePreference (argset 3: full)");
        }
    }

    @Override
    protected View onCreateDialogView() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onCreateDialogView");
        }

        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    // bind dialog to current view
    @Override
    protected void onBindDialogView(View view) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onBindDialogView");
        }

        super.onBindDialogView(view);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onDialogClosed");
        }

        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();
            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
            saveTime(time);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray typedArray, int index) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onGetDefaultValue");
        }

        return typedArray.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] onSetInitialValue");
        }

        String time;

        if (restoreValue) {

            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }

        } else {
            time = defaultValue.toString();
        }

        lastHour = Util.getHour(time);
        lastMinute = Util.getMinute(time);
    }

    private void saveTime(String time) {

        // Debug
        if (Config.LOGD){
            Log.d(TAG, "[fn] saveTime");
        }

        if (callChangeListener(time)) {
            persistString(time);
        }
    }

    public String getTimeString() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getTimeString");
        }

        return pad(lastHour) + ":" + pad(lastMinute);
    }

    public void setTime(String time) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setTime");
        }

        lastHour = Util.getHour(time);
        lastMinute = Util.getMinute(time);

        saveTime(time);
    }

    private static String pad(int c) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] pad");
        }

        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + String.valueOf(c);
        }
    }

}
