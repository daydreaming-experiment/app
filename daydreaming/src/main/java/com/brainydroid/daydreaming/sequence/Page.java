package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    private ArrayList<Question> questions;

    public Page(ArrayList<Question> questions) {
        Logger.v(TAG, "Creating page from list of questions");
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

}
