package com.brainydroid.daydreaming;

import android.content.Context;
import android.content.SharedPreferences;

public class StatusManager {

	private static StatusManager smInstance = null;

	private final String EXP_STATUS = "expStatus";
	private final String FL_STARTED = "flStarted";
	private final String FL_COMPLETED = "flCompleted";
	private final String IS_CLEARING = "isClearing";

	private final SharedPreferences expStatus;
	private final SharedPreferences.Editor eExpStatus;

	private final Context context;

	public static StatusManager getInstance(Context c) {
		if (smInstance == null) {
			smInstance = new StatusManager(c);
		}
		return smInstance;
	}

	private StatusManager(Context c) {
		context = c.getApplicationContext();
		expStatus = context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
	}

	public boolean isFirstLaunchStarted() {
		return expStatus.getBoolean(FL_STARTED, false);
	}

	public void setFirstLaunchStarted() {
		eExpStatus.putBoolean(FL_STARTED, true);
		eExpStatus.commit();
	}

	public boolean isFirstLaunchCompleted() {
		return expStatus.getBoolean(FL_COMPLETED, false);
	}

	public void setFirstLaunchCompleted() {
		eExpStatus.putBoolean(FL_COMPLETED, true);
		eExpStatus.commit();
	}

	public boolean isClearing() {
		return expStatus.getBoolean(IS_CLEARING, false);
	}

	public void startClear() {
		eExpStatus.clear();
		eExpStatus.putBoolean(IS_CLEARING, true);
		eExpStatus.commit();
	}

	public void finishClear() {
		eExpStatus.clear();
		eExpStatus.commit();
	}
}