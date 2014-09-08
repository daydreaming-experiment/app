package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Abstract base class for objects that are stored in an SQLite database.
 * <p/>
 * This model implements an {@link #id} for the model. If the {@link #id} is
 * not set (equals {@code -1}), it means the model instance is not saved to
 * the database. If the {@link #id} is different from {@code -1},
 * it means the model is persisted to database, and any change on the model's
 * values should be saved.
 * <p/>
 * The parameterization of the class is a bit intricate,
 * but that is to provide a way of calling {@link Model#save} directly on
 * the model (instead of having to call {@link ModelStorage#store}@{code
 * (model)}). See
 * <a href="http://stackoverflow.com/questions/16689810/casting-a-generic-superclass-to-a-subclass">
 * my StackOverflow question</a> about that for more details.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @param <M> Final type of model for concrete classes,
 *            see {@link LocationPoint} for an example
 * @param <S> Final type of model storage for concrete classes,
 *            see {@link LocationPoint} for an example
 * @see ModelStorage
 * @see StatusModel
 * @see StatusModelStorage
 * @see LocationPoint
 * @see com.brainydroid.daydreaming.sequence.Sequence
 */
public abstract class Model<M extends Model<M,S,F>,
        S extends ModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>> {

    private static String TAG = "Model";

    @JsonView(Views.Internal.class)
    private int id = -1;

    private boolean retainSaves = false;
    private boolean hasRetainedSaves = false;

    /**
     * Set the {@link Model}'s id, used for database ordering and indexing.
     * <p/>
     * {@link #id} is different from {@code -1} if and only if this model
     * instance is persisted in the database. In that case all subsequent
     * modifications of the object should also save the modifications to the
     * persisted record in the database, using {@link #saveIfSync}. So all
     * the setters of subclasses of {@link Model} (with the exception of this
     * one, {@code #setId} will save their modifications to the database if
     * {@link #id} is different from {@code -1}. Setting the {@link #id} back
     * to {@code -1} amounts to disconnecting the instance from its record in
     * the database.
     * <p/>
     * The {@link #id} is set either when saving an instance to the database
     * or when loading one from the database, and shouldn't be interfered
     * with at any other moment.
     *
     * @param id Id to set
     */
    public synchronized void setId(int id) {
        Logger.v(TAG, "Setting id to {0}", id);
        this.id = id;
        // No need to saveIfSync() here: this is called only from the
        // storage's store() method, or when loading the model from the
        // database. In both cases the save has no effect..
    }

    /**
     * Get the {@link Model}'s id.
     * <p/>
     * See {@link #setId} for details on the meaning of this id.
     *
     * @return Id of the {@link Model}
     */
    public synchronized int getId() {
        return id;
    }

    /**
     * Save the instance to the database if the {@link #id} is different from
     * {@code -1}. (In which case it's in fact an update of an existing
     * record in the database.) Otherwise do nothing.
     */
    public synchronized void saveIfSync() {
        if (id != -1) {
            Logger.d(TAG, "Model has an id, syncing to db");
            save();
        } else {
            Logger.v(TAG, "Model has no id, not syncing to db");
        }
    }

    /**
     * Concrete classes should return {@code this}. It lets us avoid an
     * unchecked cast in {@link #save}. See
     * <a href="http://stackoverflow.com/questions/16689810/casting-a-generic-superclass-to-a-subclass">
     * my StackOverflow question</a> about that for more details.
     *
     * @return {@code this} (of type {@code M}, i.e. the concrete class
     *         inheriting from {@link Model}
     */
    protected abstract M self();

    /**
     * Concrete classes should return an instance to the storage in which the
     * model will be saved.
     *
     * @return {@link ModelStorage} to which to save the model
     */
    protected abstract S getStorage();

    /**
     * Save the instance to the database, regardless of the value of its
     * {@link #id}.
     */
    public synchronized void save() {
        if (retainSaves) {
            Logger.d(TAG, "Saves are to be retained, not saving to storage");
            hasRetainedSaves = true;
            return;
        }
        if (id != -1) {
            Logger.d(TAG, "Updating model in db");
            getStorage().update(self());
        } else {
            Logger.d(TAG, "Storing model in db");
            getStorage().store(self());
        }
    }

    public synchronized void retainSaves() {
        Logger.v(TAG, "Retaining saves from now on");
        retainSaves = true;
    }

    public synchronized void flushSaves() {
        Logger.v(TAG, "Flushing saves now");
        // Save if we have retained saves, or we weren't retaining saves at all
        if (hasRetainedSaves || !retainSaves) {
            retainSaves = false;
            save();
        } else {
            Logger.v(TAG, "No saves retained, no need to save");
            retainSaves = false;
        }
        hasRetainedSaves = false;
    }

}
