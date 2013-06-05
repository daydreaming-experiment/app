package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class ModelStorage<M extends Model<M,S>,
        S extends ModelStorage<M,S>> {

    private static String TAG = "ModelStorage";

    private final SQLiteDatabase db;

    protected abstract String[] getTableCreationStrings();

    @Inject
    public ModelStorage(Storage storage) {
        Logger.d(TAG, "Building ModelStorage: creating tables if they don't" +
                " exist");
        db = storage.getWritableDatabase();
        for (String tableCreationString : getTableCreationStrings()) {
            db.execSQL(tableCreationString); // creates db fields
        }
    }

    protected SQLiteDatabase getDb() {
        return db;
    }

    protected abstract ContentValues getModelValues(M model);

    private ContentValues getModelValuesWithId(M model) {
        Logger.v(TAG, "Getting model values with id");

        ContentValues modelValues = getModelValues(model);
        modelValues.put(Model.COL_ID, model.getId());
        return modelValues;
    }

    protected abstract String getMainTable();

    public void store(M model) {
        Logger.d(TAG, "Storing model to db (obtaining an id)");

        ContentValues modelValues = getModelValues(model);
        db.insert(getMainTable(), null, modelValues);

        Cursor res = db.query(getMainTable(), new String[] {Model.COL_ID},
                null, null, null, null, Model.COL_ID + " DESC", "1");
        res.moveToFirst();
        int modelId = res.getInt(res.getColumnIndex(Model.COL_ID));
        res.close();

        Logger.v(TAG, "New model id is {0}", modelId);
        model.setId(modelId);
    }

    public void update(M model) {
        int modelId = model.getId();
        Logger.d(TAG, "Updating model {0} in db", modelId);
        ContentValues modelValues = getModelValuesWithId(model);
        db.update(getMainTable(), modelValues, Model.COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    protected abstract M create();

    protected abstract void populateModel(int modelId, M model, Cursor res);

    public M get(int modelId) {
        Logger.d(TAG, "Retrieving model {0} from db", modelId);

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
        Logger.d(TAG, "Removing model {0} from db", modelId);
        db.delete(getMainTable(), LocationPoint.COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    public void remove(ArrayList<? extends Model<M,S>> models) {
        Logger.d(TAG, "Removing an array of models from db");
        for (Model model : models) {
            remove(model.getId());
        }
    }

}
