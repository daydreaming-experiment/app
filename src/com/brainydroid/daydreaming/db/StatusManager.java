package com.brainydroid.daydreaming.db;

import android.content.Context;
import android.content.SharedPreferences;


// Class to create, maintain and update data (SharedPreferences) associated to the app
public class StatusManager {

	private static StatusManager smInstance = null;

	private static final String EXP_STATUS = "expStatus"; // status of experiment
	private static final String FL_STARTED = "flStarted"; // first launch started
	private static final String FL_COMPLETED = "flCompleted"; // first launch completed
	private static final String IS_CLEARING = "isClearing";

	private final SharedPreferences expStatus; // file containing status of exp
	private final SharedPreferences.Editor eExpStatus; // editor of expStatus

	private final Context _context; // application environment

	public static StatusManager getInstance(Context context) {
		if (smInstance == null) {
			smInstance = new StatusManager(context);
		}
		return smInstance;
	}

	/*
	 * Constructor.
	 * loads context, assign initial preferences
	 */
	private StatusManager(Context context) {
		_context = context.getApplicationContext();
		expStatus = _context.getSharedPreferences(EXP_STATUS, Context.MODE_PRIVATE);
		eExpStatus = expStatus.edit();
	}

	/*
	 * Check if activity was already launched
	 */
	public boolean isFirstLaunchStarted() {
		return expStatus.getBoolean(FL_STARTED, false);
	}


	public void setFirstLaunchStarted() {
		eExpStatus.putBoolean(FL_STARTED, true);
		eExpStatus.commit();
	}

	/*
	 * Check if first launch is completed
	 */
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

	// Clearing expstatus
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