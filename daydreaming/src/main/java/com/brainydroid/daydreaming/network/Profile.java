package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.HashMap;

public class Profile {

    private static String TAG = "Profile";

    @JsonProperty private String id;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonProperty private String vk_pem;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonProperty private String exp_id;

    @SuppressWarnings("FieldCanBeLocal")
    @JsonProperty private ProfileData profile_data;

    @Inject @JsonIgnore ProfileWrapperFactory profileWrapperFactory;

    public Profile() {}

    @AssistedInject
    public Profile(@Assisted("expId") String expId,
                   @Assisted("vkPem") String vkPem) {
        Logger.v(TAG, "Creating a Profile instance with only expId and vkPem");
        exp_id = expId;
        vk_pem = vkPem;
    }

    @AssistedInject
    public Profile(ProfileDataFactory profileDataFactory,
                   @Assisted("expId") String expId,
                   @Assisted("age") String age,
                   @Assisted("gender") String gender,
                   @Assisted("education") String education,
                   @Assisted HashMap<String, Integer> tipiAnswers,
                   @Assisted("parametersVersion") String parametersVersion,
                   @Assisted("appVersionName") String appVersionName,
                   @Assisted("appVersionCode") int appVersionCode,
                   @Assisted("mode") String mode) {
        Logger.v(TAG, "Creating a Profile instance with detailed data");
        this.exp_id = expId;
        this.profile_data = profileDataFactory.create(age, gender, education,
                tipiAnswers, parametersVersion, appVersionName, appVersionCode, mode);
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized ProfileWrapper buildWrapper() {
        return profileWrapperFactory.create(this);
    }
}
