package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class SequencesStorage
        extends TypedStatusModelStorage<Sequence,SequencesStorage,SequenceJsonFactory> {

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

        String[] uploadableStatuses;
        if (type.equals(Sequence.TYPE_PROBE)) {
            Logger.v(TAG, "Type is probe, so uploadable means either STATUS_COMPLETED " +
                    "or STATUS_PARTIALLY_COMPLETED");
            uploadableStatuses = new String[] {Sequence.STATUS_COMPLETED,
                    Sequence.STATUS_PARTIALLY_COMPLETED};
        } else {
            Logger.v(TAG, "Type is NOT probe, so uploadable means only STATUS_COMPLETED");
            uploadableStatuses = new String[] {Sequence.STATUS_COMPLETED};
        }

        return getModelsByStatusesAndTypes(uploadableStatuses, new String[]{type});
    }

    public synchronized ArrayList<Sequence> getSequencesByType(String type) {
        Logger.v(TAG, "Getting sequences by Type");
        return getModelsByType(type);
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
        ArrayList<Sequence> uploadableSequences = getUploadableSequences(type);
        if (uploadableSequences != null) {
            Logger.d(TAG, "Removing {} uploadable sequences", uploadableSequences.size());
            remove(uploadableSequences);
        }
    }

    public synchronized void removeAllSequences(String type) {
        Logger.d(TAG, "Removing all sequences of type {}", type);
        ArrayList<Sequence> sequences = getModelsByType(type);
        if (sequences != null) {
            Logger.d(TAG, "Removing {}  sequences", sequences.size());
            remove(sequences);
        }
    }
}
