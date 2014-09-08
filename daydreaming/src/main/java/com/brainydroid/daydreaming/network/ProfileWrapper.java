package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProfileWrapper {

    @JsonView(Views.Public.class)
    private Profile profile;

    public ProfileWrapper() {}

    @Inject
    public ProfileWrapper(@Assisted Profile profile) {
        this.profile = profile;
    }

    public synchronized Profile getProfile() {
        return profile;
    }

}
