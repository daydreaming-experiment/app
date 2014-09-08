package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class JWSSignature {

    @JsonProperty("protected")
    @JsonView(Views.Public.class)
    private String protected_;
    @JsonView(Views.Public.class)
    private String signature;

    public JWSSignature() {}

    public JWSSignature(String protected_, String signature) {
        this.protected_ = protected_;
        this.signature = signature;
    }

    public synchronized String getProtected() {
        return protected_;
    }

    public synchronized String getSignature() {
        return signature;
    }
}
