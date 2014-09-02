package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

public class SequenceFactory extends ModelFactory<Sequence, SequencesStorage, SequenceFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceFactory";

    @Inject Json json;

    @Override
    public Sequence createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating sequence from json");
        return json.fromJson(jsonContent, Sequence.class);
    }
}
