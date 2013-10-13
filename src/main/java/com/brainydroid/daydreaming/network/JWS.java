package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class JWS {

    @Expose private String payload;
    @Expose private ArrayList<JWSSignature> signatures;

    public JWS(String payload, ArrayList<JWSSignature> jwsSignatures) {
        this.payload = payload;
        this.signatures = jwsSignatures;
    }
}
