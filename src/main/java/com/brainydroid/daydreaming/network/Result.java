package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Poll;
import com.google.gson.annotations.Expose;

public class Result {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String profile_id;
    @Expose private Poll data;

    public Result(String profile_id, Poll data) {
        this.profile_id = profile_id;
        this.data = data;
    }

    public synchronized Poll getPoll() {
        return data;
    }

}
