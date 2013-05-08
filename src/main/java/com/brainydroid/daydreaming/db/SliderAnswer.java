package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    @SuppressWarnings("UnusedDeclaration")
    private String type = "Slider";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashMap<String,Integer> sliders = new HashMap<String, Integer>();

    public void addAnswer(String text, int position) {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addAnswer");
        }

        sliders.put(text, position);
    }

}
