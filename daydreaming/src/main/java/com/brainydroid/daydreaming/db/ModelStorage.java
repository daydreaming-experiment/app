package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class ModelStorage<M extends Model<M,S,F>,
        S extends ModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>> {

    private static String TAG = "ModelStorage";

    protected static final String COL_ID = "id";
    protected static final String COL_CONTENT = "content";

    @Inject Json json;
    @Inject F modelFactory;
    @Inject HashMap<Integer,M> modelsCache;
    private final SQLiteDatabase db;

    protected synchronized String getTableCreationString() {
        Logger.v(TAG, "Creating table creation string");
        String body = Util.joinStrings(getTableCreationElements(), ", ");
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                body + ");";
    }

    protected synchronized ArrayList<String> getTableCreationElements() {
        Logger.v(TAG, "Creating table creation elements");
        return new ArrayList<String>(Arrays.asList(
                new String[] {COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                COL_CONTENT + " TEXT NOT NULL"}));
    }

    @Inject
    public ModelStorage(Storage storage) {
        Logger.d(TAG, "Building ModelStorage: creating tables if they don't" +
                " exist");
        db = storage.getWritableDatabase();
        db.execSQL(getTableCreationString()); // creates db fields
    }

    protected synchronized SQLiteDatabase getDb() {
        return db;
    }

    protected synchronized ContentValues getModelValues(M model) {
        Logger.d(TAG, "Getting model values");
        ContentValues modelValues = new ContentValues();

        String jsonModel = json.toJson(model);
        Logger.v(TAG, "Adding content to model");
        Logger.v(TAG, jsonModel.replace("{", "'{'").replace("}", "'}'"));
        modelValues.put(COL_CONTENT, jsonModel);

        return modelValues;
    }

    private synchronized ContentValues getModelValuesWithId(M model) {
        Logger.v(TAG, "Getting model values");
        ContentValues modelValues = getModelValues(model);

        Logger.v(TAG, "Adding id to model");
        modelValues.put(COL_ID, model.getId());

        return modelValues;
    }

    protected abstract String getTableName();

    public synchronized void store(M model) {
        Logger.d(TAG, "Storing model to db (obtaining an id)");

        ContentValues modelValues = getModelValues(model);
        db.insert(getTableName(), null, modelValues);

        Cursor res = db.query(getTableName(), new String[] {COL_ID},
                null, null, null, null, COL_ID + " DESC", "1");
        res.moveToFirst();
        int modelId = res.getInt(res.getColumnIndex(COL_ID));
        res.close();

        // The new id doesn't get saved to db, but is reloaded when we 'get' the model.
        // So this is transparent to the user, and on next save, the id is saved also inside
        // the content of the model. The only other solution to this problem is double-saving
        // each time.
        Logger.v(TAG, "New model id is {0}", modelId);
        model.setId(modelId);

        Logger.d(TAG, "Saving new model {0} to cache", modelId);
        modelsCache.put(modelId, model);
    }

    public synchronized void update(M model) {
        int modelId = model.getId();
        Logger.d(TAG, "Updating model {0} in db", modelId);
        ContentValues modelValues = getModelValuesWithId(model);
        db.update(getTableName(), modelValues, COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    public synchronized M get(int modelId) {

        // If we already retrieved the model, return the cached instance
        M cachedModel = modelsCache.get(modelId);
        if (cachedModel != null) {
            Logger.d(TAG, "Retrieving model {0} from cache", modelId);
            return cachedModel;
        }

        Logger.d(TAG, "Retrieving model {0} from db", modelId);

        Cursor res = db.query(getTableName(), null, COL_ID + "=?",
                new String[]{Integer.toString(modelId)}, null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            Logger.e(TAG, "Asked for model {} but could not be found", modelId);
            return null;
        }

        M model = modelFactory.createFromJson(res.getString(res.getColumnIndex(COL_CONTENT)));
        res.close();

        // Make sure the model id is set inside the content
        // (isn't the case if the model was saved only once)
        model.setId(modelId);

        Logger.d(TAG, "Saving model {0} to cache", modelId);
        modelsCache.put(modelId, model);
        return model;
    }

    public synchronized void remove(int modelId) {
        Logger.d(TAG, "Removing model {0} from cache and db", modelId);
        modelsCache.remove(modelId);
        db.delete(getTableName(), COL_ID + "=?",
                new String[]{Integer.toString(modelId)});
    }

    public synchronized void remove(ArrayList<? extends Model<M,S,F>> models) {
        Logger.d(TAG, "Removing an array of models from cache and db");
        if (models == null) {
            Logger.d(TAG, "No models to remove (received null)");
            return;
        }
        for (Model model : models) {
            remove(model.getId());
        }
    }

}
