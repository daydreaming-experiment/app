package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.HashMap;

public class Profile {

    private static String TAG = "Profile";

    @Expose private String id;
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String vk_pem;
    // TODO[seb]: exp_id field must be assigned before profile is posted to server
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Expose private String exp_id;

    @SuppressWarnings("FieldCanBeLocal")
    @Expose private ProfileData profile_data;

    @Inject ProfileWrapperFactory profileWrapperFactory;

    @AssistedInject
    public Profile(@Assisted String vkPem) {
        Logger.v(TAG, "Creating a Profile instance with only vkPem");
        vk_pem = vkPem;
    }

    @AssistedInject
    public Profile(ProfileDataFactory profileDataFactory,
                   @Assisted("age") String age,
                   @Assisted("gender") String gender,
                   @Assisted("education") String education,
                   @Assisted HashMap<String, Integer> tipiAnswers,
                   @Assisted("parametersVersion") String parametersVersion,
                   @Assisted("appVersionName") String appVersionName,
                   @Assisted("appVersionCode") int appVersionCode,
                   @Assisted("mode") String mode) {
        Logger.v(TAG, "Creating a Profile instance with detailed data");
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
