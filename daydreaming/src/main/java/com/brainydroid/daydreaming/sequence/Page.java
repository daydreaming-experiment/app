package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    public static final String STATUS_ASKED = "pageAsked";
    public static final String STATUS_ASKED_DISMISSED = "pageAskedDismissed";
    public static final String STATUS_ANSWERED = "pageAnswered";

    private ArrayList<Question> questions;
    private String status;

    public Page(ArrayList<Question> questions) {
        Logger.v(TAG, "Creating page from list of questions");
        this.questions = questions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

}
