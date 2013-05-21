package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class ModelStorage<T extends Model<T,S>,
        S extends ModelStorage<T,S>> {

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

    protected abstract <Z extends Model<T,S>> ContentValues getModelValues(Z model);

    private <Z extends Model<T,S>> ContentValues getModelValuesWithId(Z model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelValuesWithId");
        }

        ContentValues modelValues = getModelValues(model);
        modelValues.put(Model.COL_ID, model.getId());
        return modelValues;
    }

    protected abstract String getMainTable();

    public <Z extends Model<T,S>> void store(Z model) {

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

    public <Z extends Model<T,S>> void update(Z model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] update");
        }

        ContentValues modelValues = getModelValuesWithId(model);
        int modelId = model.getId();
        db.update(getMainTable(), modelValues, Model.COL_ID + "=?",
                new String[] {Integer.toString(modelId)});
    }

    protected abstract IModelFactory<T> getModelFactory();

    protected abstract void populateModel(T model, Cursor res);

    public T get(int modelId) {

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

        T model = getModelFactory().create();
        populateModel(model, res);
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

    public void remove(ArrayList<? extends Model<T,S>> models) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removeLocationPoints");
        }

        for (Model model : models) {
            remove(model.getId());
        }
    }

}
