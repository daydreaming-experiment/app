package com.brainydroid.daydreaming.db;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// Class to create SQLite database for both polls and questions
@Singleton
public class Storage extends SQLiteOpenHelper {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "Storage";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Storage";

    @Inject
    public Storage(Application application) {
        super(application, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {}

}
