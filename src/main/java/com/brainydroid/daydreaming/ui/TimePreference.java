package com.brainydroid.daydreaming.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.brainydroid.daydreaming.db.Util;

// Extends DialogPreference, a preference class that pops out in a dialogbox
public class TimePreference extends DialogPreference {

	private static String TAG = "TimePreference";

	private int lastHour = 0;
	private int lastMinute = 0;
	private TimePicker picker = null;

	// constructor from context
	public TimePreference(Context ctxt) {

		super(ctxt, null); // constructor from superclass DialogPreference

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] TimePreference (argset 1: small)");
		}
	}

	// constructor from context and attributes
	public TimePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs, 0);

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] TimePreference (argset 2: medium)");
		}
	}

	// constructor from context and attributes and style
	public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
		super(ctxt, attrs, defStyle);

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
	protected void onBindDialogView(View v) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onBindDialogView");
		}

		super.onBindDialogView(v);
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
	protected Object onGetDefaultValue(TypedArray a, int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onGetDefaultValue");
		}

		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onSetInitialValue");
		}

		String time = null;

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

		//        lastHour = picker.getCurrentHour();
		//        lastMinute = picker.getCurrentMinute();
		String time = pad(lastHour) + ":" + pad(lastMinute);
		return time;
	}

	public void setTime(String time) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setTime");
		}

		String[] pieces = time.split(":");
		lastHour = Integer.parseInt(pieces[0]);
		lastMinute = Integer.parseInt(pieces[1]);

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
