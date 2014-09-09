package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

public class Location {

    private static String TAG = "Location";

    @JsonView(Views.Public.class)
    private double latitude;
    @JsonView(Views.Public.class)
    private double longitude;
    @JsonView(Views.Public.class)
    private double altitude;
    @JsonView(Views.Public.class)
    private float accuracy;

    public Location() {}

    public Location(android.location.Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        accuracy = location.getAccuracy();
    }

}
