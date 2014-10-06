package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.HashMap;

public class ManySlidersAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "ManySlidersAnswer";

    // Json-marked members cannot be injected, or else the json-deserialized value is overridden
    @JsonView(Views.Public.class)
    HashMap<String, Integer> sliders = new HashMap<String, Integer>();

    public synchronized void addSlider(String sliderText, int sliderValue) {
        Logger.v(TAG, "Adding slider {0} with value {1}", sliderText, sliderValue);
        sliders.put(sliderText, sliderValue);
    }
}
