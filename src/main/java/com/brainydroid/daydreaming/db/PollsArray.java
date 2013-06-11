package com.brainydroid.daydreaming.db;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PollsArray {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PollsArray";

    @Expose private ArrayList<Poll> polls;

    public PollsArray(ArrayList<Poll> polls) {
        this.polls = polls;
    }

    public synchronized ArrayList<Poll> getPolls() {
        return polls;
    }

}
