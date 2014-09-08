package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonView;

public class Location extends android.location.Location {

    private static String TAG = "Location";

    public Location(String provider) {
        super(provider);
    }

    public Location(android.location.Location location) {
        super(location);
    }

    @JsonView(Views.Public.class)
    @Override
    public double getLatitude() {
        return super.getLatitude();
    }

    @JsonView(Views.Public.class)
    @Override
    public double getLongitude() {
        return super.getLongitude();
    }

    @JsonView(Views.Public.class)
    @Override
    public double getAltitude() {
        return super.getAltitude();
    }

    @JsonView(Views.Public.class)
    @Override
    public float getAccuracy() {
        return super.getAccuracy();
    }

}
