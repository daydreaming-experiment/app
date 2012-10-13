package com.brainydroid.daydreaming;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class QuestionsStorage {

	private static QuestionsStorage qsInstance = null;

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;

	public static QuestionsStorage getInstance(Context context) {
		if (qsInstance == null) {
			qsInstance = new QuestionsStorage(context);
		}
		return qsInstance;
	}

	private QuestionsStorage(Context context) {
		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		rDb = storage.getWritableDatabase();
		wDb = storage.getWritableDatabase();
		// Create tables
	}

	public int getQuestionsVersion() {
		// TBD
		return 0;
	}
}