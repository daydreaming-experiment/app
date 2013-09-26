package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class JWS {

    @Expose private String payload;
    @Expose private ArrayList<Signature> signatures;

    public JWS(String payload, ArrayList<Signature> signatures) {
        this.payload = payload;
        this.signatures = signatures;
    }
}
