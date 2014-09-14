package com.brainydroid.daydreaming.network;

import android.content.Context;
import android.widget.Toast;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

public class ServerError {

    private static String TAG = "ServerError";

    @Inject Context context;

    @JsonView(Views.Internal.class)
    private int status_code = -1;
    @JsonView(Views.Internal.class)
    private String type = null;
    @JsonView(Views.Internal.class)
    private String message = null;

    public void debugToastError() {
        Logger.td(context, "Server returned error:\nstatus_code: {0}\ntype: {1}\nmessage: {2}",
                status_code, type, message);
    }
}
