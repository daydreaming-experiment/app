package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LocationPointsArray {

    private static String TAG = "PollsArray";

    @Expose private ArrayList<LocationPoint> locationPoints;

    public LocationPointsArray(ArrayList<LocationPoint> locationPoints) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] LocationPointsArray");
        }

        this.locationPoints = locationPoints;
    }

    public ArrayList<LocationPoint> getLocationPoints() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationPoints");
        }

        return locationPoints;
    }

}
