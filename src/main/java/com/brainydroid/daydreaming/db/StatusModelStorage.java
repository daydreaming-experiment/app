package com.brainydroid.daydreaming.db;

import android.database.Cursor;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public abstract class StatusModelStorage<T extends StatusModel<T,S>,
        S extends StatusModelStorage<T,S>>
        extends ModelStorage<T,S> {

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
    protected void populateModel(T model, Cursor res) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createModel");
        }

        model.setStatus(res.getString(
                res.getColumnIndex(StatusModel.COL_STATUS)));
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

    protected ArrayList<T> getModelsWithStatuses(
            String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getModelsWithStatuses");
        }

        ArrayList<Integer> statusModelIds = getModelIdsWithStatuses(statuses);

        if (statusModelIds == null) {
            return null;
        }

        ArrayList<T> statusModels = new ArrayList<T>();

        for (int modelId : statusModelIds) {
            statusModels.add(get(modelId));
        }

        return statusModels;
    }

}
