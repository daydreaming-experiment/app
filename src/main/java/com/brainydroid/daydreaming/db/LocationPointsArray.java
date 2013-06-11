package com.brainydroid.daydreaming.db;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Hold an array of {@link LocationPoint}s for send in one serialized block
 * to the server. That way we're not doing 5 POST requests for 5 {@link
 * LocationPoint}s, but only one.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see LocationPoint
 */
public class LocationPointsArray {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PollsArray";

    @Expose private ArrayList<LocationPoint> locationPoints;

    /**
     * Constructor from an {@link ArrayList} of {@link LocationPoint}s.
     *
     * @param locationPoints Array of {@link LocationPoint}s to set
     */
    public LocationPointsArray(ArrayList<LocationPoint> locationPoints) {
        this.locationPoints = locationPoints;
    }

    /**
     * Get the stored array of {@link LocationPoint}s.
     *
     * @return The stored array of {@link LocationPoint}s
     */
    public synchronized ArrayList<LocationPoint> getLocationPoints() {
        return locationPoints;
    }

}
