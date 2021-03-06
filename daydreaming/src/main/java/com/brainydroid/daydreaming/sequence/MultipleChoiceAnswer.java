package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.HashSet;

public class MultipleChoiceAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswer";

    // Json-marked members cannot be injected, or else the json-deserialized value is overridden
    @JsonView(Views.Public.class)
    HashSet<String> choices = new HashSet<String>();

    public synchronized void addChoice(String choice) {
        Logger.v(TAG, "Adding choice {0}", choice);
        choices.add(choice);
    }

}
