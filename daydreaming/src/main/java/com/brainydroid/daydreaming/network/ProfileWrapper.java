package com.brainydroid.daydreaming.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProfileWrapper {

    @JsonProperty private Profile profile;

    @Inject
    public ProfileWrapper(@Assisted Profile profile) {
        this.profile = profile;
    }

    public synchronized Profile getProfile() {
        return profile;
    }

}
