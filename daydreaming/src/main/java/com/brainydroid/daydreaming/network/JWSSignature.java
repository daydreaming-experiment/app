package com.brainydroid.daydreaming.network;


import com.fasterxml.jackson.annotation.JsonProperty;

public class JWSSignature {

    @JsonProperty private String protected_;
    @JsonProperty private String signature;

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
