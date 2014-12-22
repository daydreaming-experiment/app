package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProfileData {

    private static String TAG = "ProfileData";

    @JsonView(Views.Public.class)
    private String age;
    @JsonView(Views.Public.class)
    private String gender;
    @JsonView(Views.Public.class)
    private String education;
    @JsonView(Views.Public.class)
    private String motherTongue;
    @JsonView(Views.Public.class)
    private String parametersVersion;
    @JsonView(Views.Public.class)
    private String appVersionName;
    @JsonView(Views.Public.class)
    private int appVersionCode;
    @JsonView(Views.Public.class)
    private String mode;
    @JsonView(Views.Public.class)
    private String botherWindowMapJson;

    public ProfileData() {}

    @Inject
    public ProfileData(@Assisted("age") String age,
                       @Assisted("gender") String gender,
                       @Assisted("education") String education,
                       @Assisted("motherTongue") String motherTongue,
                       @Assisted("parametersVersion") String parametersVersion,
                       @Assisted("appVersionName") String appVersionName,
                       @Assisted("appVersionCode") int appVersionCode,
                       @Assisted("mode") String mode,
                       @Assisted("botherWindowMapJson") String botherWindowMapJson) {
        Logger.v(TAG, "Creating a ProfileData instance");
        this.age = age;
        this.gender = gender;
        this.education = education;
        this.motherTongue = motherTongue;
        this.parametersVersion = parametersVersion;
        this.appVersionName = appVersionName;
        this.appVersionCode = appVersionCode;
        this.mode = mode;
        this.botherWindowMapJson = botherWindowMapJson;
    }
}
