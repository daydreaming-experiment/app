package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class ServerParametersJson {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ServerParametersJson";

    public int version = -1;
    public int nSlotsPerPoll = -1;
    ArrayList<Question> questions = new ArrayList<Question>();

    public synchronized ArrayList<Question> getQuestionsArrayList() {
        return questions;
    }

    public synchronized int getVersion() {
        return version;
    }

    public synchronized int getNSlotsPerPoll() {
        return nSlotsPerPoll;
    }

}
