package com.brainydroid.daydreaming.network;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result<T> {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonProperty private String profile_id;
    @JsonProperty private T result_data;

    public Result(String profile_id, T result_data) {
        this.profile_id = profile_id;
        this.result_data = result_data;
    }

    public synchronized T getData() {
        return result_data;
    }

}
