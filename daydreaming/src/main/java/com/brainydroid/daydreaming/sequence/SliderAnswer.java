package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonProperty private String type = "Slider";
    @Inject @JsonProperty HashMap<String, Integer> sliders;

    public synchronized String getType() {
        return type;
    }

    public synchronized void addAnswer(String text, int position) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, position);
        sliders.put(text, position);
    }

}
