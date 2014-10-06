package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    // Json-marked members cannot be injected, or else the json-deserialized value is overridden
    @JsonView(Views.Public.class)
    HashMap<String, Integer> sliders = new HashMap<String, Integer>();

    public synchronized void addAnswer(String text, int position) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, position);
        sliders.put(text, position);
    }

}
