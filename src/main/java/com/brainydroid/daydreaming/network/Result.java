package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Poll;
import com.google.gson.annotations.Expose;

public class Result {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String profile_id;
    @Expose private Poll result_data;

    public Result(String profile_id, Poll result_data) {
        this.profile_id = profile_id;
        this.result_data = result_data;
    }

    public synchronized Poll getPoll() {
        return result_data;
    }

}
