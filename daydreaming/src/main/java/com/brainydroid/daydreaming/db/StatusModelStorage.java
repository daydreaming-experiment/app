package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class StatusModelStorage<M extends StatusModel<M,S>,
        S extends StatusModelStorage<M,S>> extends ModelStorage<M,S> {

    private static String TAG = "StatusModelStorage";

    protected static final String COL_STATUS = "status";

    @Inject
    public StatusModelStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized ArrayList<String> getTableCreationElements() {
        ArrayList<String> elements = super.getTableCreationElements();
        Logger.v(TAG, "Adding status to table creation elements");
        elements.add(COL_STATUS + " TEXT NOT NULL");
        return elements;
    }

    @Override
    protected synchronized ContentValues getModelValues(M model) {
        ContentValues modelValues = super.getModelValues(model);
        Logger.d(TAG, "Adding status to model");
        modelValues.put(COL_STATUS, model.getStatus());
        return modelValues;
    }

    private synchronized ArrayList<Integer> getModelIdsByStatuses(
            String[] statuses) {
        Logger.d(TAG, "Getting model ids by statuses");
        return getModelIdsByStatuses(statuses, null);
    }

    private synchronized ArrayList<Integer> getModelIdsByStatuses(
            String[] statuses, String limit) {
        Logger.d(TAG, "Getting model ids by statuses (with limit " +
                "argument)");

        String query = Util.multiplyString(COL_STATUS + "=?",
                statuses.length, " OR ");
        Cursor res = getDb().query(getTableName(),
                new String[]{COL_ID}, query, statuses, null, null,
                null, limit);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<Integer> statusModelIds = new ArrayList<Integer>();
        do {
            statusModelIds.add(res.getInt(res.getColumnIndex(COL_ID)));
        } while (res.moveToNext());

        return statusModelIds;
    }

    protected synchronized ArrayList<M> getModelsByStatuses(
            String[] statuses) {
        String logStatuses = Util.joinStrings(statuses, ", ");
        Logger.d(TAG, "Getting models with statuses {0}", logStatuses);

        ArrayList<Integer> statusModelIds = getModelIdsByStatuses(statuses);
        if (statusModelIds == null) {
            Logger.v(TAG, "No models found with statuses {0}", logStatuses);
            return null;
        } else {
            Logger.d(TAG, "Found {0} models with statuses {1}",
                    statusModelIds.size(), logStatuses);
        }

        ArrayList<M> statusModels = new ArrayList<M>();
        for (int modelId : statusModelIds) {
            statusModels.add(get(modelId));
        }

        return statusModels;
    }

}
