package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class SliderQuestionDetails implements IQuestionDetails {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SliderQuestionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "Slider";
    @SuppressWarnings("UnusedDeclaration")
    private ArrayList<SliderSubQuestion> subQuestions =
            new ArrayList<SliderSubQuestion>();

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized ArrayList<SliderSubQuestion> getSubQuestions() {
        return subQuestions;
    }

}
