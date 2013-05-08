package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class SliderQuestionDetails implements IQuestionDetails {

    private static String TAG = "SliderQuestionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "Slider";
    @SuppressWarnings("UnusedDeclaration")
    private ArrayList<SliderSubQuestion> subQuestions =
            new ArrayList<SliderSubQuestion>();

    @Override
    public String getType() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getType");
        }

        return type;
    }

    public ArrayList<SliderSubQuestion> getSubQuestions() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getSubQuestions");
        }

        return subQuestions;
    }

}
