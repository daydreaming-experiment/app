package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Calendar;

public class AuthContent {

    private static String TAG = "AuthContent";

    @JsonView(Views.Public.class)
    private String id = null;
    @JsonView(Views.Public.class)
    private int timestamp = -1;

    public AuthContent(String id, int timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }
}
