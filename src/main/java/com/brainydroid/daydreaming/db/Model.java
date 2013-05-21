package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

public abstract class Model {

    private static String TAG = "Model";

    // These members don't need to be serialized
    private transient int id = -1;
    private transient String status;

    // Fields used for saving a Model to a database
    public static final String COL_ID = "id";
    public static final String COL_STATUS = "status";

    public void setId(int id) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setId");
        }

        this.id = id;
    }

    public int getId() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getId");
        }

        return id;
    }

    private void saveIfSync() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveIfSync");
        }

        if (id != -1) {
            save();
        }
    }

    protected abstract ModelStorage getStorage();

    public void save() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] save");
        }

        if (id != -1) {
            getStorage().update(this);
        } else {
            getStorage().store(this);
        }
    }
}
