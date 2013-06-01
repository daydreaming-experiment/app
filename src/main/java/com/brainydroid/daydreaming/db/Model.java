package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

// FIXME: adapt doc imported from LocationPoint
public abstract class Model<M extends Model<M,S>,
        S extends ModelStorage<M,S>> {

    private static String TAG = "Model";

    // These members don't need to be serialized
    private transient int id = -1;

    // Fields used for saving a Model to a database
    public static final String COL_ID = "id";

    /**
     * Set the {@code LocationPoint}'s id, used for database ordering and
     * indexing.
     * <p/>
     * {@code id} is different from {@code -1} if and only if it is
     * persisted in the database. In that case all subsequent modifications
     * of the object will also save the modifications to the persisted
     * record in the database. So all the setters of this class (with the
     * exception of this one, {@code setId()} will save their modifications
     * to the database if {@code id} is different from {@code -1}. Setting
     * the {@code id} back to {@code -1} amounts to disconnecting the
     * instance from its record in the database.
     * <p/>
     * The {@code id} is set either when saving an instance to the
     * database or when loading one from the database,
     * and shouldn't be interfered with at any other moment.
     *
     * @param id Id to set
     */
    public void setId(int id) {
        Logger.v(TAG, "Setting id to {0}", id);
        this.id = id;
    }

    /**
     * Get the {@code LocationPoint}'s id.
     * <p/>
     * See {@code setId()} for details on the meaning of this id.
     *
     * @return Id of the {@code LocationPoint}
     */
    public int getId() {
        return id;
    }

    /**
     * Save the instance to the database if the {@code id} is different from
     * {@code -1}. (In which case it's in fact an update of an existing
     * record in the database.)
     */
    protected void saveIfSync() {
        if (id != -1) {
            Logger.d(TAG, "Model has an id, syncing to db");
            save();
        } else {
            Logger.v(TAG, "Model has no id, not syncing to db");
        }
    }

    protected abstract M self();

    protected abstract S getStorage();

    /**
     * Save the instance to the database, regardless of the value of its
     * {@code id}.
     */
    public void save() {
        if (id != -1) {
            Logger.d(TAG, "Updating model in db");
            getStorage().update(self());
        } else {
            Logger.d(TAG, "Storing model in db");
            getStorage().store(self());
        }
    }

}
