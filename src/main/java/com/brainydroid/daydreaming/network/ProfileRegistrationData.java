package com.brainydroid.daydreaming.network;

import com.google.gson.annotations.Expose;

public class ProfileRegistrationData {

    @Expose private Profile profile;

    public ProfileRegistrationData(Profile profile) {
        this.profile = profile;
    }

    public synchronized Profile getProfile() {
        return profile;
    }

}
