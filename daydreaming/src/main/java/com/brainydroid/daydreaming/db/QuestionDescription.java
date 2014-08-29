package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.AbstractQuestion;

public class QuestionDescription extends AbstractQuestion {

    private static String TAG = "QuestionDescription";

    private String name;
    private String position;
    private IQuestionDetails details;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public IQuestionDetails getDetails() {
        return details;
    }
}
