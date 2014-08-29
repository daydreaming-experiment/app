package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class PageDescription {

    private static String TAG = "PageDescription";

    private String name;
    private String position;
    private ArrayList<QuestionDescription> questions;

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
