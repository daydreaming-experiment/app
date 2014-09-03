package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.HashSet;

public class MultipleChoiceAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    @Expose private String type = "MultipleChoice";
    @Inject @Expose HashSet<String> choices = new HashSet<String>();

    public synchronized String getType() {
        return type;
    }

    public synchronized void addChoice(String choice) {
        Logger.v(TAG, "Adding choice {0}", choice);
        choices.add(choice);
    }

}
