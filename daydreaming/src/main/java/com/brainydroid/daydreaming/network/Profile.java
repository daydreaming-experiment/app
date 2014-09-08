package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.HashMap;

public class Profile {

    private static String TAG = "Profile";

    @JsonView(Views.Public.class)
    private String id;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonView(Views.Public.class)
    private String vk_pem;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @JsonView(Views.Public.class)
    private String exp_id;

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Public.class)
    private ProfileData profile_data;

    @Inject ProfileWrapperFactory profileWrapperFactory;

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
