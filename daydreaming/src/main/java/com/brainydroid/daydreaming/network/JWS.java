package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class JWS {

    @JsonView(Views.Public.class)
    private String payload;
    @JsonView(Views.Public.class)
    private ArrayList<JWSSignature> signatures;

    public JWS() {}

    public JWS(String payload, ArrayList<JWSSignature> jwsSignatures) {
        this.payload = payload;
        this.signatures = jwsSignatures;
    }
}
