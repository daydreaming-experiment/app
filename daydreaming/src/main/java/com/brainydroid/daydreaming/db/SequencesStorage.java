package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class SequencesStorage
        extends TypedStatusModelStorage<Sequence,SequencesStorage,SequenceJsonFactory> {

    private static String TAG = "SequencesStorage";

    private static final String TABLE_SEQUENCES = "sequences";

    @Inject public SequencesStorage(Storage storage) {
        super(storage);
    }
    @Inject SequenceBuilder sequenceBuilder;
    @Inject Provider<ParametersStorage> parametersStorageProvider;

    @Override protected String getTableName() {
        return TABLE_SEQUENCES;
    }

    public synchronized ArrayList<Sequence> getUploadableSequences(String type) {
        Logger.v(TAG, "Getting uploadable sequences of type {}", type);

        String[] uploadableStatuses;
        if (type.equals(Sequence.TYPE_PROBE)) {
            Logger.v(TAG, "Type is probe, so uploadable means either STATUS_COMPLETED " +
                    "or STATUS_MISSED_OR_DISMISSED_OR_INCOMPLETE");
            uploadableStatuses = new String[] {Sequence.STATUS_COMPLETED,
                    Sequence.STATUS_MISSED_OR_DISMISSED_OR_INCOMPLETE};
        } else {
            Logger.v(TAG, "Type is NOT probe, so uploadable means only STATUS_COMPLETED");
            uploadableStatuses = new String[] {Sequence.STATUS_COMPLETED};
        }

        return getModelsByStatusesAndTypes(uploadableStatuses, new String[]{type});
    }

    public synchronized ArrayList<Sequence> getCompletedSequences(String type) {
        Logger.v(TAG, "Getting completed sequences of type {}", type);
        return getModelsByStatusesAndTypes(
                new String[] {Sequence.STATUS_COMPLETED, Sequence.STATUS_UPLOADED_AND_KEEP},
                new String[] {type});
    }

    public synchronized ArrayList<Sequence> getSequencesByType(String type) {
        Logger.v(TAG, "Getting sequences of type {}", type);
        return getModelsByType(type);
    }

    public synchronized ArrayList<Sequence> getUploadableSequences() {
        Logger.v(TAG, "Getting uploadable sequences");
        ArrayList<Sequence> uploadableSequences = new ArrayList<Sequence>();
        for (String type : Sequence.AVAILABLE_REAL_TYPES) {
            uploadableSequences.addAll(getUploadableSequences(type));
        }
        return uploadableSequences;
    }

    public synchronized ArrayList<Sequence> getPendingSequences(String type) {
        Logger.d(TAG, "Getting pending sequences");
        return getModelsByStatusesAndTypes(new String[]{Sequence.STATUS_PENDING},
                new String[]{type});
    }

    public synchronized void removeAllSequences(String type) {
        Logger.d(TAG, "Removing all sequences of type {}", type);
        ArrayList<Sequence> sequences = getModelsByType(type);
        if (sequences != null) {
            Logger.d(TAG, "Removing {} sequences", sequences.size());
            remove(sequences);
        }
    }

    public synchronized void removeAllSequences(String[] types) {
        Logger.d(TAG, "Removing all sequences of types {}", types.toString());
        for (String type : types) {
            removeAllSequences(type);
        }
    }

    public synchronized void instantiateBeginEndQuestionnaires() {
        Logger.v(TAG, "Instantiating BeginEnd Questionnaires");
        ArrayList<SequenceDescription> allBeginQuestionnairesDescriptions =
                parametersStorageProvider.get().getSequencesByTypes(
                        new String[] {Sequence.TYPE_BEGIN_QUESTIONNAIRE,
                                Sequence.TYPE_END_QUESTIONNAIRE});
        for (SequenceDescription sd : allBeginQuestionnairesDescriptions) {
            Logger.v(TAG, "Instantiating questionnaire {}", sd.getName());
            sequenceBuilder.buildSave(sd.getName());
        }
    }
}
