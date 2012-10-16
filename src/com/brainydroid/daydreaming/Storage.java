package com.brainydroid.daydreaming;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Class to create SQlite database for both polls and questions
public class Storage extends SQLiteOpenHelper {

	private static Storage sInstance = null;

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "Storage";

	public static Storage getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Storage(context);
		}
		return sInstance;
	}

	private Storage (Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
