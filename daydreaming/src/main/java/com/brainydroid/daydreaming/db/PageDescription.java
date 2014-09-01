package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.AbstractPage;

import java.util.ArrayList;

public class PageDescription extends AbstractPage {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PageDescription";

    private String name = null;
    private String position = null;
    private ArrayList<QuestionDescription> questions = new ArrayList<QuestionDescription>();

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public ArrayList<QuestionDescription> getQuestions() {
        return questions;
    }

}
