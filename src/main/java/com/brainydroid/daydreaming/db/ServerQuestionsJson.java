package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;

import java.util.ArrayList;

public class ServerQuestionsJson {

    private static String TAG = "ServerQuestionsJson";

    public int version = -1;
    ArrayList<Question> questions = new ArrayList<Question>();

    public ArrayList<Question> getQuestionsArrayList() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getQuestionsArrayList");
        }

        return questions;
    }

    public int getVersion() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getVersion");
        }

        return version;
    }

}
