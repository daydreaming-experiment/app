package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class StatusModelStorage<M extends StatusModel<M,S>,
        S extends StatusModelStorage<M,S>>
        extends ModelStorage<M,S> {

    private static String TAG = "StatusModelStorage";

    @Inject
    public StatusModelStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected void populateModel(int modelId, M model, Cursor res) {
        Logger.v(TAG, "Populating model {0} with status", modelId);
        model.setStatus(res.getString(
                res.getColumnIndex(StatusModel.COL_STATUS)));
    }

    @Override
    protected ContentValues getModelValues(M model) {
        Logger.d(TAG, "Getting model values (status only)");
        ContentValues modelValues = new ContentValues();
        modelValues.put(StatusModel.COL_STATUS, model.getStatus());
        return modelValues;
    }

    private ArrayList<Integer> getModelIdsWithStatuses(String[] statuses) {
        Logger.d(TAG, "Getting model ids with statuses");
        return getModelIdsWithStatuses(statuses, null);
    }

    protected ArrayList<Integer> getModelIdsWithStatuses(String[] statuses,
                                                         String limit) {
        Logger.d(TAG, "Getting model ids with statuses (with limit " +
                "argument)");

        String query = Util.multiplyString(StatusModel.COL_STATUS + "=?",
                statuses.length, " OR ");
        Cursor res = getDb().query(getMainTable(),
                new String[]{Model.COL_ID}, query, statuses, null, null,
                null, limit);
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

    protected ArrayList<M> getModelsWithStatuses(
            String[] statuses) {
        String logStatuses = Util.joinStrings(statuses, ", ");
        Logger.d(TAG, "Getting models with statuses {0}", logStatuses);

        ArrayList<Integer> statusModelIds = getModelIdsWithStatuses(statuses);
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
