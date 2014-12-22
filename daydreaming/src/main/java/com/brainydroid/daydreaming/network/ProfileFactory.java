package com.brainydroid.daydreaming.network;

import com.google.inject.assistedinject.Assisted;

public interface ProfileFactory {

    public Profile create(@Assisted("expId") String expId,
                          @Assisted("vkPem") String vkPem);
    public Profile create(@Assisted("expId") String expId,
                          @Assisted("age") String age,
                          @Assisted("gender") String gender,
                          @Assisted("education") String education,
                          @Assisted("motherTongue") String motherTongue,
                          @Assisted("parametersVersion") String parametersVersion,
                          @Assisted("appVersionName") String appVersionName,
                          @Assisted("appVersionCode") int appVersionCode,
                          @Assisted("mode") String mode,
                          @Assisted("botherWindowMapJson") String botherWindowMapJson);
}
