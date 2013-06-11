package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ModelStorage<M extends Model<M,S>,
        S extends ModelStorage<M,S>> {

    private static String TAG = "ModelStorage";

    /** Column name for the {@link Model#id} in the database */
    public static final String COL_ID = "id";

    @Inject HashMap<Integer,M> modelsCache;
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

    protected synchronized SQLiteDatabase getDb() {
        return db;
    }

    protected abstract ContentValues getModelValues(M model);

    private synchronized ContentValues getModelValuesWithId(M model) {
        Logger.v(TAG, "Getting model values with id");

        ContentValues modelValues = getModelValues(model);
        modelValues.put(COL_ID, model.getId());
        return modelValues;
    }

    protected abstract String getMainTable();

    public synchronized void store(M model) {
        Logger.d(TAG, "Storing model to db (obtaining an id)");

        ContentValues modelValues = getModelValues(model);
        db.insert(getMainTable(), null, modelValues);

        Cursor res = db.query(getMainTable(), new String[] {COL_ID},
                null, null, null, null, COL_ID + " DESC", "1");
        res.moveToFirst();
        int modelId = res.getInt(res.getColumnIndex(COL_ID));
        res.close();

        Logger.v(TAG, "New model id is {0}", modelId);
        model.setId(modelId);

        Logger.d(TAG, "Saving new model {0} to cache", modelId);
        modelsCache.put(modelId, model);
    }

    public synchronized void update(M model) {
        int modelId = model.getId();
        Logger.d(TAG, "Updating model {0} in db", modelId);
        ContentValues modelValues = getModelValuesWithId(model);
        db.update(getMainTable(), modelValues, COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    protected abstract M create();

    protected abstract void populateModel(int modelId, M model, Cursor res);

    public synchronized M get(int modelId) {

        // If we already retrieved the model, return the cached instance
        M cachedModel = modelsCache.get(modelId);
        if (cachedModel!= null) {
            Logger.d(TAG, "Retrieving model {0} from cache", modelId);
            return cachedModel;
        }

        Logger.d(TAG, "Retrieving model {0} from db", modelId);

        Cursor res = db.query(getMainTable(), null, COL_ID + "=?",
                new String[]{Integer.toString(modelId)}, null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        M model = create();
        populateModel(modelId, model, res);
        model.setId(res.getInt(res.getColumnIndex(COL_ID)));
        res.close();

        Logger.d(TAG, "Saving model {0} to cache", modelId);
        modelsCache.put(modelId, model);
        return model;
    }

    public synchronized void remove(int modelId) {
        Logger.d(TAG, "Removing model {0} from cache and db", modelId);
        modelsCache.remove(modelId);
        db.delete(getMainTable(), COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    public synchronized void remove(ArrayList<? extends Model<M,S>> models) {
        Logger.d(TAG, "Removing an array of models from cache and db");
        for (Model model : models) {
            remove(model.getId());
        }
    }

}
