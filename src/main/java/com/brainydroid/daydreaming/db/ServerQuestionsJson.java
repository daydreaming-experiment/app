package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class ServerQuestionsJson {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ServerQuestionsJson";

    public int version = -1;
    ArrayList<Question> questions = new ArrayList<Question>();

    public synchronized ArrayList<Question> getQuestionsArrayList() {
        return questions;
    }

    public synchronized int getVersion() {
        return version;
    }

}
