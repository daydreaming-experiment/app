package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import com.brainydroid.daydreaming.background.Logger;
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
    }

    // constructor from context and attributes
    @SuppressWarnings("UnusedDeclaration")
    public TimePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    // constructor from context and attributes and style
    @SuppressWarnings("UnusedDeclaration")
    public TimePreference(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected View onCreateDialogView() {
        Logger.d(TAG, "Creating dialog view");

        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    // bind dialog to current view
    @Override
    protected void onBindDialogView(View view) {
        Logger.d(TAG, "Binding dialog view");

        super.onBindDialogView(view);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Logger.d(TAG, "Dialog closed");

        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            Logger.d(TAG, "Trying to save selected time window");
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();
            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
            saveTime(time);
        } else {
            Logger.v(TAG, "Discarding selected time window");
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray typedArray, int index) {
        Logger.v(TAG, "Getting default value");
        return typedArray.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Logger.v(TAG, "Setting initial value");

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

    public void setTime(String time) {
        lastHour = Util.getHour(time);
        lastMinute = Util.getMinute(time);

        saveTime(time);
    }

    private void saveTime(String time) {
        if (callChangeListener(time)) {
            Logger.i(TAG, "Persisting selected time window (possibly " +
                    "corrected)");
            persistString(time);
        } else {
            Logger.e(TAG, "Error while calling change listener");
        }
    }

    public String getTimeString() {
        return pad(lastHour) + ":" + pad(lastMinute);
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + String.valueOf(c);
        }
    }

}
