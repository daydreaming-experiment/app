package com.brainydroid.daydreaming.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;

// Class to create SQlite database for both polls and questions
public class Storage extends SQLiteOpenHelper {

	private static String TAG = "Storage";

	private static Storage sInstance = null;

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "Storage";

	public static synchronized Storage getInstance(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

		if (sInstance == null) {
			sInstance = new Storage(context);
		}
		return sInstance;
	}

	private Storage(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Storage");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onCreate");
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] onUpgrade");
		}

	}
}
