package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class MultipleChoiceAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "MultipleChoice";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashSet<String> choices = new HashSet<String>();

    public String getType() {

        // Verbose
        if(Config.LOGV) {
            Log.v(TAG, "[fn] getType");
        }

        return type;
    }

    public void addChoice(String choice) {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addChoice");
        }

        choices.add(choice);
    }

}
