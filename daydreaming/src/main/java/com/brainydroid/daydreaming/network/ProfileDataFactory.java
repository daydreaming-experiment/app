package com.brainydroid.daydreaming.network;

import com.google.inject.assistedinject.Assisted;

import java.util.HashMap;

public interface ProfileDataFactory {

    public ProfileData create(@Assisted("age") String age,
                              @Assisted("gender") String gender,
                              @Assisted("education") String education,
                              HashMap<String, Integer> tipiAnswers,
                              @Assisted("parametersVersion") String parametersVersion,
                              @Assisted("appVersionName") String appVersionName,
                              @Assisted("appVersionCode") int appVersionCode,
                              @Assisted("mode") String mode);

}
