package com.brainydroid.daydreaming.network;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.HashMap;

public class ProfileData {

    private static String TAG = "ProfileData";

    @Expose private String age;
    @Expose private String gender;
    @Expose private String education;
    @Expose private HashMap<String, Integer> tipi_answers;

    @Inject
    public ProfileData(@Assisted("age") String age,
                       @Assisted("gender") String gender,
                       @Assisted("education") String education,
                       @Assisted HashMap<String, Integer> tipiAnswers) {
        Logger.v(TAG, "Creating a ProfileData instance");
        this.age = age;
        this.gender = gender;
        this.education = education;
        tipi_answers = tipiAnswers;
    }
}
