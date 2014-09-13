package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class TypedStatusModelStorage<M extends TypedStatusModel<M,S,F>,
        S extends TypedStatusModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>>
        extends StatusModelStorage<M,S,F> {

    private static String TAG = "TypedStatusModelStorage";

    protected static final String COL_TYPE = "type";

    @Inject
    public TypedStatusModelStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized ArrayList<String> getTableCreationElements() {
        ArrayList<String> elements = super.getTableCreationElements();
        Logger.v(TAG, "Adding type to table creation elements");
        elements.add(COL_TYPE + " TEXT NOT NULL");
        return elements;
    }
    @Override
    protected synchronized ContentValues getModelValues(M model) {
        ContentValues modelValues = super.getModelValues(model);
        Logger.d(TAG, "Adding type to model");
        modelValues.put(COL_TYPE, model.getType());
        return modelValues;
    }

    private synchronized ArrayList<Integer> getModelIdsByType(String type) {
        Logger.d(TAG, "Getting model ids by type");
        return getModelIdsByType(type, null);
    }

    private synchronized ArrayList<Integer> getModelIdsByType(String type, String limit) {
        Logger.d(TAG, "Getting model ids by type (with limit argument)");

        String query = COL_TYPE + "=?";
        Cursor res = getDb().query(getTableName(),
                new String[]{COL_ID}, query, new String[] {type}, null, null,
                null, limit);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<Integer> typeModelIds = new ArrayList<Integer>();
        do {
            typeModelIds.add(res.getInt(res.getColumnIndex(COL_ID)));
        } while (res.moveToNext());

        return typeModelIds;
    }

    protected synchronized ArrayList<M> getModelsByType(String type) {
        Logger.d(TAG, "Getting models with type {0}", type);

        ArrayList<Integer> typeModelIds = getModelIdsByType(type);
        if (typeModelIds == null) {
            Logger.v(TAG, "No models found with type {0}", type);
            return null;
        } else {
            Logger.d(TAG, "Found {0} models with types {1}",
                    typeModelIds.size(), type);
        }

        ArrayList<M> typeModels = new ArrayList<M>();
        for (int modelId : typeModelIds) {
            typeModels.add(get(modelId));
        }

        return typeModels;
    }

    private synchronized ArrayList<Integer> getModelIdsByStatusesAndTypes(String[] statuses,
                                                                       String[] types) {
        Logger.d(TAG, "Getting models ids by statuses and types");
        return getModelIdsByStatusesAndTypes(statuses, types, null);
    }

    private synchronized ArrayList<Integer> getModelIdsByStatusesAndTypes(String[] statuses,
                                                                       String[] types,
                                                                       String limit) {
        Logger.d(TAG, "Getting models ids by statuses and types (with limit argument)");

        String query = "("
                + Util.multiplyString(COL_STATUS + "=?", statuses.length, " OR ")
                + ") AND ("
                + Util.multiplyString(COL_TYPE + "=?", types.length, " OR ")
                + ")";
        Cursor res = getDb().query(getTableName(),
                new String[]{COL_ID}, query,
                Util.concatenateStringArrays(statuses, types),
                null, null, null, limit);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<Integer> statusTypeModelIds = new ArrayList<Integer>();
        do {
            statusTypeModelIds.add(res.getInt(res.getColumnIndex(COL_ID)));
        } while (res.moveToNext());

        return statusTypeModelIds;
    }

    protected synchronized ArrayList<M> getModelsByStatusesAndTypes(String[] statuses,
                                                                 String[] types) {
        String logStatuses = Util.joinStrings(statuses, ", ");
        String logTypes = Util.joinStrings(types, ", ");
        Logger.d(TAG, "Getting models with statuses {0} and types {1}", logStatuses, logTypes);

        ArrayList<Integer> statusTypeModelIds = getModelIdsByStatusesAndTypes(statuses, types);
        if (statusTypeModelIds == null) {
            Logger.v(TAG, "No models found with statuses {0} and types {0}", logStatuses, logTypes);
            return null;
        } else {
            Logger.d(TAG, "Found {0} models with statuses {1} and types {2}",
                    statusTypeModelIds.size(), logStatuses, logTypes);
        }

        ArrayList<M> statusTypeModels = new ArrayList<M>();
        for (int modelId : statusTypeModelIds) {
            statusTypeModels.add(get(modelId));
        }

        return statusTypeModels;

    }

}
