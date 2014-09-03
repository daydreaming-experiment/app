package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    @Expose private String type = "Slider";
    @Inject @Expose HashMap<String, Integer> sliders = new HashMap<String, Integer>();

    public synchronized String getType() {
        return type;
    }

    public synchronized void addAnswer(String text, int position) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, position);
        sliders.put(text, position);
    }

}
