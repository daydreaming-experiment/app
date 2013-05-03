package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PollArray {

    private static String TAG = "PollArray";

    @Expose private ArrayList<Poll> polls;

    public PollArray(ArrayList<Poll> polls) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] PollArray");
        }

        this.polls = polls;
    }

    public ArrayList<Poll> getPolls() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getPolls");
        }

        return polls;
    }

}
