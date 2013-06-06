package com.brainydroid.daydreaming.db;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
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
        StatusModel<LocationPoint,LocationPointsStorage> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationPoint";

    // These should always be serialized
    @Expose private double locationLatitude = -1;
    @Expose private double locationLongitude = -1;
    @Expose private double locationAltitude = -1;
    @Expose private double locationAccuracy = -1;
    @Expose private long timestamp = -1;

    /** Column name for location latitude in the database */
    public static final String COL_LOCATION_LATITUDE =
            "locationLocationLatitude";
    /** Column name for location longitude in the database */
    public static final String COL_LOCATION_LONGITUDE =
            "locationLocationLongitude";
    /** Column name for location altitude in the database */
    public static final String COL_LOCATION_ALTITUDE =
            "locationLocationAltitude";
    /** Column name for location accuracy in the database */
    public static final String COL_LOCATION_ACCURACY =
            "locationLocationAccuracy";
    /** Column name for location timestamp in the database */
    public static final String COL_TIMESTAMP = "locationTimestamp";

    /** Status string if the {@link LocationPoint} is collecting location
     * data
     */
    public static final String STATUS_COLLECTING = "statusCollecting";
    /** Status string if the {@link LocationPoint} is done collecting
     * location data
     */
    public static final String STATUS_COMPLETED = "statusCompleted";

    // Our database for LocationPoints
    @Inject transient LocationPointsStorage locationPointsStorage;

    @Override
    protected LocationPoint self() {
        return this;
    }

    @Override
    protected LocationPointsStorage getStorage() {
        return locationPointsStorage;
    }

    /**
     * Get the latitude of the {@link LocationPoint}.
     *
     * @return Latitude of the {@link LocationPoint}
     */
    public double getLocationLatitude() {
        return locationLatitude;
    }

    /**
     * Get the longitude of the {@link LocationPoint}.
     *
     * @return Longitude of the {@link LocationPoint}
     */
    public double getLocationLongitude() {
        return locationLongitude;
    }

    /**
     * Get the altitude of the {@link LocationPoint}.
     *
     * @return Altitude of the {@link LocationPoint} in meters
     */
    public double getLocationAltitude() {
        return locationAltitude;
    }

    /**
     * Get the accuracy of the {@link LocationPoint}.
     *
     * @return Accuracy of the {@link LocationPoint} in meters
     */
    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    /**
     * Set the latitude of the {@link LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationLatitude Latitude to set
     */
    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
        saveIfSync();
    }

    /**
     * Set the longitude of the {@link LocationPoint},
     * and persist to database if necessary.
     *
     * @param locationLongitude Longitude to set
     */
    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
        saveIfSync();
    }

    /**
     * Set the altitude of the {@link LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationAltitude Altitude to set, in meters
     */
    public void setLocationAltitude(double locationAltitude) {
        this.locationAltitude = locationAltitude;
        saveIfSync();
    }

    /**
     * Set the accuracy of the {@link LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationAccuracy Accuracy to set, in meters
     */
    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
        saveIfSync();
    }

    /**
     * Set the location values for the {@link LocationPoint},
     * and persist to database if necessary. This includes latitude,
     * longitude, altitude, and accuracy.
     *
     * @param location {@link Location} instance from which to take the data
     */
    public void setLocation(Location location) {
        if (location != null) {
            locationLatitude = location.getLatitude();
            locationLongitude = location.getLongitude();
            locationAltitude = location.getAltitude();
            locationAccuracy = location.getAccuracy();
        }
        saveIfSync();
    }

    /**
     * Get the {@link LocationPoint}'s timestamp.
     *
     * @return Timestamp, usually in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set the {@link LocationPoint}'s timestamp, and persist to database
     * if necessary.
     *
     * @param timestamp Timestamp to set, usually in milliseconds since epoch
     */
    public void setTimestamp(long timestamp) {
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
    public boolean isComplete() {
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
