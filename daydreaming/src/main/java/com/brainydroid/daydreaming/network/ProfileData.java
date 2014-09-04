package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.HashMap;

public class ProfileData {

    private static String TAG = "ProfileData";

    @JsonProperty private String age;
    @JsonProperty private String gender;
    @JsonProperty private String education;
    @JsonProperty private HashMap<String, Integer> tipi_answers;
    @JsonProperty private String parametersVersion;
    @JsonProperty private String appVersionName;
    @JsonProperty private int appVersionCode;
    @JsonProperty private String mode;

    @Inject
    public ProfileData(@Assisted("age") String age,
                       @Assisted("gender") String gender,
                       @Assisted("education") String education,
                       @Assisted HashMap<String, Integer> tipiAnswers,
                       @Assisted("parametersVersion") String parametersVersion,
                       @Assisted("appVersionName") String appVersionName,
                       @Assisted("appVersionCode") int appVersionCode,
                       @Assisted("mode") String mode) {
        Logger.v(TAG, "Creating a ProfileData instance");
        this.age = age;
        this.gender = gender;
        this.education = education;
        tipi_answers = tipiAnswers;
        this.parametersVersion = parametersVersion;
        this.appVersionName = appVersionName;
        this.appVersionCode = appVersionCode;
        this.mode = mode;
    }
}
