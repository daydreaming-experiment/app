package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class MultipleChoiceQuestionDetails implements IQuestionDetails {

    private static String TAG = "MultipleChoiceQuestionDetails";

    private String type;
    private String text;
    private ArrayList<String> choices;

    @Override
    public String getType() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getType");
        }

        return type;
    }

    public String getText() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getText");
        }

        return text;
    }

    public ArrayList<String> getChoices() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getChoices");
        }

        return choices;
    }

}
