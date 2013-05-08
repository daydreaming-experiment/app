package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class SliderAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "Slider";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashMap<String,Integer> sliders = new HashMap<String, Integer>();

    public String getType() {

        // Verbose
        if(Config.LOGV) {
            Log.v(TAG, "[fn] getType");
        }

        return type;
    }

    public void addAnswer(String text, int position) {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addAnswer");
        }

        sliders.put(text, position);
    }

}
