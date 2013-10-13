package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ProfileWrapper {

    @Expose private Profile profile;

    @Inject
    public ProfileWrapper(@Assisted Profile profile) {
        this.profile = profile;
    }

    public synchronized Profile getProfile() {
        return profile;
    }

}
