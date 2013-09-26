package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Poll;
import com.google.gson.annotations.Expose;

public class Result {

    @Expose final String profile_id;
    @Expose final Poll data;

    public Result(String profile_id, Poll data) {
        this.profile_id = profile_id;
        this.data = data;
    }

    public synchronized Poll getPoll() {
        return data;
    }

}
