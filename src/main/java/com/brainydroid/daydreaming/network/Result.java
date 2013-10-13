package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

public class Result<T> {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String profile_id;
    @Expose private T result_data;

    public Result(String profile_id, T result_data) {
        this.profile_id = profile_id;
        this.result_data = result_data;
    }

    public synchronized T getData() {
        return result_data;
    }

}
