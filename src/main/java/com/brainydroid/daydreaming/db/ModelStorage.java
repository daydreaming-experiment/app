package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class ModelStorage<M extends Model<M,S>,
        S extends ModelStorage<M,S>> {

    private static String TAG = "ModelStorage";

    private final SQLiteDatabase db;

    protected abstract String[] getTableCreationStrings();

    @Inject
    public ModelStorage(Storage storage) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] ModelStorage");
        }

        db = storage.getWritableDatabase();
        for (String tableCreationString : getTableCreationStrings()) {
            db.execSQL(tableCreationString); // creates db fields
        }
    }

    protected SQLiteDatabase getDb() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getDb");
        }

        return db;
    }

    protected abstract ContentValues getModelValues(M model);

    private ContentValues getModelValuesWithId(M model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelValuesWithId");
        }

        ContentValues modelValues = getModelValues(model);
        modelValues.put(Model.COL_ID, model.getId());
        return modelValues;
    }

    protected abstract String getMainTable();

    public void store(M model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] store");
        }

        ContentValues modelValues = getModelValues(model);
        db.insert(getMainTable(), null, modelValues);

        Cursor res = db.query(getMainTable(), new String[] {Model.COL_ID},
                null, null, null, null, Model.COL_ID + " DESC", "1");
        res.moveToFirst();
        int modelId = res.getInt(res.getColumnIndex(Model.COL_ID));
        res.close();

        model.setId(modelId);
    }

    public void update(M model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] update");
        }

        ContentValues modelValues = getModelValuesWithId(model);
        int modelId = model.getId();
        db.update(getMainTable(), modelValues, Model.COL_ID + "=?",
                new String[] {Integer.toString(modelId)});
    }

    protected abstract M create();

    protected abstract void populateModel(int modelId, M model, Cursor res);

    public M get(int modelId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] get");
        }

        Cursor res = db.query(getMainTable(), null, Model.COL_ID + "=?",
                new String[] {Integer.toString(modelId)}, null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        M model = create();
        populateModel(modelId, model, res);
        model.setId(res.getInt(res.getColumnIndex(Model.COL_ID)));
        res.close();

        return model;
    }

    public void remove(int modelId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] remove");
        }

        db.delete(getMainTable(), LocationPoint.COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    public void remove(ArrayList<? extends Model<M,S>> models) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removeLocationPoints");
        }

        for (Model model : models) {
            remove(model.getId());
        }
    }

}
