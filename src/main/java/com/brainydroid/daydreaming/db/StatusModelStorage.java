package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class StatusModelStorage<M extends StatusModel<M,S>,
        S extends StatusModelStorage<M,S>>
        extends ModelStorage<M,S> {

    private static String TAG = "StatusModelStorage";

    @Inject
    public StatusModelStorage(Storage storage) {

        super(storage);

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] StatusModelStorage");
        }
    }

    @Override
    protected void populateModel(int modelId, M model, Cursor res) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] populateModel");
        }

        model.setStatus(res.getString(
                res.getColumnIndex(StatusModel.COL_STATUS)));
    }

    @Override
    protected ContentValues getModelValues(M model) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelValues");
        }

        ContentValues modelValues = new ContentValues();
        modelValues.put(StatusModel.COL_STATUS, model.getStatus());
        return modelValues;
    }

    private ArrayList<Integer> getModelIdsWithStatuses(String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelIdsWithStatuses (from String[])");
        }

        return getModelIdsWithStatuses(statuses, null);
    }

    protected ArrayList<Integer> getModelIdsWithStatuses(String[] statuses,
                                                         String limit) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelIdsWithStatuses (from String[], " +
                    "String)");
        }

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

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelsWithStatuses");
        }

        ArrayList<Integer> statusModelIds = getModelIdsWithStatuses(statuses);

        if (statusModelIds == null) {
            return null;
        }

        ArrayList<M> statusModels = new ArrayList<M>();

        for (int modelId : statusModelIds) {
            statusModels.add(get(modelId));
        }

        return statusModels;
    }

}
