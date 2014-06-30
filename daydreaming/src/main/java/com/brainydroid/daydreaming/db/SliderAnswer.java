package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "Slider";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashMap<String, Integer> sliders = new HashMap<String, Integer>();

    public synchronized String getType() {
        return type;
    }

    public synchronized void addAnswer(String text, int position) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, position);
        sliders.put(text, position);
    }

}
