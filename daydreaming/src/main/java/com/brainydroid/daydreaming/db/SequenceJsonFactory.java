package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

import org.json.JSONException;

public class SequenceJsonFactory extends ModelJsonFactory<Sequence, SequencesStorage, SequenceJsonFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceJsonFactory";

    @Inject Json json;
    @Inject ErrorHandler errorHandler;

    @Override
    public Sequence createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating sequence from json");
        try {
            return json.fromJson(jsonContent, Sequence.class);
        } catch (JSONException e) {
            errorHandler.handleBaseJsonError(jsonContent, e);
            throw new RuntimeException(e);
        }
    }
}
