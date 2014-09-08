package com.brainydroid.daydreaming.db;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

/**
 * Hold information about a collected location, including GPS coordinates and
 * timestamp. This class inherits its model-oriented logic from {@link
 * StatusModel}.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Model
 * @see StatusModel
 * @see ModelStorage
 * @see StatusModelStorage
 * @see LocationPointsStorage
 * @see com.brainydroid.daydreaming.background.LocationPointService
 */
public final class LocationPoint extends
        StatusModel<LocationPoint,LocationPointsStorage,LocationPointJsonFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationPoint";

    /** Status string if the {@link LocationPoint} is collecting location
     * data
     */
    public static final String STATUS_COLLECTING = "statusCollecting";
    /** Status string if the {@link LocationPoint} is done collecting
     * location data
     */
    public static final String STATUS_COMPLETED = "statusCompleted";

    // These should always be serialized
    @JsonView(Views.Public.class)
    private double locationLatitude = -1;
    @JsonView(Views.Public.class)
    private double locationLongitude = -1;
    @JsonView(Views.Public.class)
    private double locationAltitude = -1;
    @JsonView(Views.Public.class)
    private double locationAccuracy = -1;
    @JsonView(Views.Public.class)
    private long timestamp = -1;

    // Our database for LocationPoints
    @Inject @JacksonInject LocationPointsStorage locationPointsStorage;

    @Override
    protected synchronized LocationPoint self() {
        return this;
    }

    @Override
    protected synchronized LocationPointsStorage getStorage() {
        return locationPointsStorage;
    }

    /**
     * Set the location values for the {@link LocationPoint},
     * and persist to database if necessary. This includes latitude,
     * longitude, altitude, and accuracy.
     *
     * @param location {@link Location} instance from which to take the data
     */
    public synchronized void setLocation(Location location) {
        if (location != null) {
            locationLatitude = location.getLatitude();
            locationLongitude = location.getLongitude();
            locationAltitude = location.getAltitude();
            locationAccuracy = location.getAccuracy();
        }
        saveIfSync();
    }

    /**
     * Set the {@link LocationPoint}'s timestamp, and persist to database
     * if necessary.
     *
     * @param timestamp Timestamp to set, usually in milliseconds since epoch
     */
    public synchronized void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        saveIfSync();
    }

    /**
     * Test whether a timestamp and a location have been set.
     * <p/>
     * Use this to determine if it is worth saving the {@link
     * LocationPoint} to the database yet or not: if there is either no
     * timestamp or no location, saving this instance to the database
     * should be used only if you need persistence between contexts or
     * through time ; otherwise it's potentially unusable incomplete data,
     * and you're better off waiting for the {@link LocationPoint} to be
     * complete before persisting to the database.
     *
     * @return {@code boolean} indicating if a timestamp and a location
     *         have been set (i.e. it's {@code false} if either one is
     *         missing)
     */
    public synchronized boolean isComplete() {
        boolean hasLocation = locationLatitude != -1 &&
                locationLongitude !=-1 &&
                locationAltitude != -1 &&
                locationAccuracy != -1;
        if (timestamp != -1 && hasLocation) {
            Logger.d(TAG, "LocationPoint is complete");
            return true;
        } else {
            Logger.d(TAG, "LocationPoint has missing location or " +
                    "timestamp");
            return false;
        }
    }

}
