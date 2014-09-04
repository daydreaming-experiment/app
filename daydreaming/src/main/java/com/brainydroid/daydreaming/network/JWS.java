package com.brainydroid.daydreaming.network;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class JWS {

    @JsonProperty private String payload;
    @JsonProperty private ArrayList<JWSSignature> signatures;

    public JWS(String payload, ArrayList<JWSSignature> jwsSignatures) {
        this.payload = payload;
        this.signatures = jwsSignatures;
    }
}
