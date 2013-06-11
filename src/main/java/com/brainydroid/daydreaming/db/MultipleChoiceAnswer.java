package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class MultipleChoiceAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "MultipleChoice";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashSet<String> choices = new HashSet<String>();

    public synchronized String getType() {
        return type;
    }

    public synchronized void addChoice(String choice) {
        Logger.v(TAG, "Adding choice {0}", choice);
        choices.add(choice);
    }

}
