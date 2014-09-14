package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ServerErrorWrapper {

    private static String TAG = "ServerErrorWrapper";

    @JsonView(Views.Public.class)
    private ServerError error;

    public ServerError getError() {
        return error;
    }
}
