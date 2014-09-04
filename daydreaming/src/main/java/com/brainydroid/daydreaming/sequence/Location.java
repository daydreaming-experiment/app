package com.brainydroid.daydreaming.sequence;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location extends android.location.Location {

    private static String TAG = "Location";

    public Location(String provider) {
        super(provider);
    }

    public Location(android.location.Location location) {
        super(location);
    }

    @JsonProperty
    @Override
    public double getLatitude() {
        return super.getLatitude();
    }

    @JsonProperty
    @Override
    public double getLongitude() {
        return super.getLongitude();
    }

    @JsonProperty
    @Override
    public double getAltitude() {
        return super.getAltitude();
    }

    @JsonProperty
    @Override
    public float getAccuracy() {
        return super.getAccuracy();
    }

}
