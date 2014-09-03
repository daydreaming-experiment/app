package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

public class SequenceJsonFactory extends ModelJsonFactory<Sequence, SequencesStorage, SequenceJsonFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceJsonFactory";

    @Inject Json json;

    @Override
    public Sequence createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating sequence from json");
        return json.fromJson(jsonContent, Sequence.class);
    }
}
