package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.util.HashSet;

public class MultipleChoiceAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonProperty private String type = "MultipleChoice";
    @Inject @JsonProperty HashSet<String> choices;

    public synchronized String getType() {
        return type;
    }

    public synchronized void addChoice(String choice) {
        Logger.v(TAG, "Adding choice {0}", choice);
        choices.add(choice);
    }

}
