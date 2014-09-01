package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.Probe;
import com.google.inject.Inject;

public class ProbeFactory extends ModelFactory<Probe, ProbesStorage, ProbeFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "ProbeFactory";

    @Inject Json json;

    @Override
    public Probe createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating probe from json");
        return json.fromJson(jsonContent, Probe.class);
    }

}
