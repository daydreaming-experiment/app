package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

public class RegistrationAnswer {

    @Expose private String id;

    public synchronized String getId() {
        return id;
    }

}
