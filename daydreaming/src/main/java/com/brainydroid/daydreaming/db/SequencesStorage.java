package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

import java.util.ArrayList;

public class SequencesStorage
        extends TypedStatusModelStorage<Sequence,SequencesStorage,SequenceFactory> {

    private static String TAG = "SequencesStorage";

    private static final String TABLE_SEQUENCES = "sequences";

    @Inject
    public SequencesStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected String getTableName() {
        return TABLE_SEQUENCES;
    }

    public synchronized ArrayList<Sequence> getUploadableSequences(String type) {
        Logger.v(TAG, "Getting uploadable sequences");
        return getModelsByStatusesAndTypes(new String[]{Sequence.STATUS_COMPLETED},
                new String[] {type});
    }

    public synchronized ArrayList<Sequence> getUploadableSequences() {
        Logger.v(TAG, "Getting uploadable sequences");
        return getModelsByStatusesAndTypes(new String[]{Sequence.STATUS_COMPLETED},
                Sequence.AVAILABLE_TYPES);
    }

    public synchronized ArrayList<Sequence> getPendingSequences(String type) {
        Logger.d(TAG, "Getting pending sequences");
        return getModelsByStatusesAndTypes(new String[]{Sequence.STATUS_PENDING},
                new String[]{type});
    }

    public synchronized void removeUploadableSequences(String type) {
        Logger.d(TAG, "Removing uploadable sequences of type {}", type);
        remove(getUploadableSequences(type));
    }
}
