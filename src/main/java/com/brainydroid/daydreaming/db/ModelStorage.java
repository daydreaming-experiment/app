package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class ModelStorage {

    private static String TAG = "ModelStorage";

    private final SQLiteDatabase rDb;
    private final SQLiteDatabase wDb;

    protected abstract String[] getTableCreationStrings();

    @Inject
    public ModelStorage(Storage storage) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] ModelStorage");
        }

        rDb = storage.getWritableDatabase();
        wDb = storage.getWritableDatabase();
        for (String tableCreationString : getTableCreationStrings()) {
            wDb.execSQL(tableCreationString); // creates db fields
        }
    }

    protected abstract ContentValues getModelValues(Model model);

    private ContentValues getModelValuesWithId(Model model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelValuesWithId");
        }

        ContentValues modelValues = getModelValues(model);
        modelValues.put(Model.COL_ID, model.getId());
        return modelValues;
    }

    protected abstract String getMainTable();

    public void store(Model model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] store");
        }

        ContentValues modelValues = getModelValues(model);
        wDb.insert(getMainTable(), null, modelValues);

        Cursor res = rDb.query(getMainTable(), new String[] {Model.COL_ID},
                null, null, null, null, Model.COL_ID + " DESC", "1");
        res.moveToFirst();
        int modelId = res.getInt(res.getColumnIndex(Model.COL_ID));
        res.close();

        model.setId(modelId);
    }


    public void update(Model model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] update");
        }

        ContentValues modelValues = getModelValuesWithId(model);
        int modelId = model.getId();
        wDb.update(getMainTable(), modelValues, Model.COL_ID + "=?",
                new String[] {Integer.toString(modelId)});
    }

    private ArrayList<Integer> getModelIdsWithStatuses(String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelIdsWithStatuses (from String[])");
        }

        return getModelIdsWithStatuses(statuses, null);
    }

    private ArrayList<Integer> getModelIdsWithStatuses(String[] statuses,
                                                       String limit) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelIdsWithStatuses (from String[], " +
                    "String)");
        }

        String query = Util.multiplyString(Model.COL_STATUS + "=?",
                statuses.length, " OR ");
        Cursor res = rDb.query(getMainTable(), new String[] {Model.COL_ID},
                query, statuses, null, null, null, limit);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<Integer> statusModelIds = new ArrayList<Integer>();
        do {
            statusModelIds.add(res.getInt(res.getColumnIndex(Model.COL_ID)));
        } while (res.moveToNext());

        return statusModelIds;
    }
}
