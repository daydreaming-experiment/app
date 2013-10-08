package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

public class Profile {

    @Expose private String id;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String vk_pem;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String exp_id;

    public Profile(String vk_pem) {
        this.vk_pem = vk_pem;
        this.exp_id = ServerConfig.EXP_ID;
    }

    public synchronized String getId() {
        return id;
    }
}
