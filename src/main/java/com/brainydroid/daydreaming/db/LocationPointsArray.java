package com.brainydroid.daydreaming.db;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LocationPointsArray {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PollsArray";

    @Expose private ArrayList<LocationPoint> locationPoints;

    public LocationPointsArray(ArrayList<LocationPoint> locationPoints) {
        this.locationPoints = locationPoints;
    }

    public ArrayList<LocationPoint> getLocationPoints() {
        return locationPoints;
    }

}
