package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Poll;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;

public class ResultsArray {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ResultsArray";

    @Expose private ArrayList<Result> results;

    @Inject
    public ResultsArray(CryptoStorage cryptoStorage,
                        @Assisted ArrayList<Poll> polls) {
        String maiId = cryptoStorage.getMaiId();
        results = new ArrayList<Result>();
        for (Poll poll : polls) {
            results.add(new Result(maiId, poll));
        }
    }

    public synchronized ArrayList<Poll> getPolls() {
        ArrayList<Poll> polls = new ArrayList<Poll>();
        for (Result result : results) {
            polls.add(result.getPoll());
        }

        return polls;
    }

}
