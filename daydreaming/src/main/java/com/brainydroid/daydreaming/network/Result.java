package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class Result<T> {

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonView(Views.Public.class)
    private String profile_id;
    @JsonView(Views.Public.class)
    private T result_data;

    public Result() {}

    public Result(String profile_id, T result_data) {
        this.profile_id = profile_id;
        this.result_data = result_data;
    }

    public synchronized T getData() {
        return result_data;
    }

}
