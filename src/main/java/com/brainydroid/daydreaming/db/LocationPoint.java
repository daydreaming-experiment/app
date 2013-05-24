package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

/**
 * Hold information about a collected location, including GPS coordinates and
 * timestamp.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public final class LocationPoint extends
        StatusModel<LocationPoint,LocationPointsStorage> {

    private static String TAG = "LocationPoint";

    // These should always be serialized
    @Expose private double locationLatitude = -1;
    @Expose private double locationLongitude = -1;
    @Expose private double locationAltitude = -1;
    @Expose private double locationAccuracy = -1;
    @Expose private long timestamp = -1;

    // Fields used for saving a LocationPoint to a database
    public static final String COL_LOCATION_LATITUDE =
            "locationLocationLatitude";
    public static final String COL_LOCATION_LONGITUDE =
            "locationLocationLongitude";
    public static final String COL_LOCATION_ALTITUDE =
            "locationLocationAltitude";
    public static final String COL_LOCATION_ACCURACY =
            "locationLocationAccuracy";
    public static final String COL_TIMESTAMP = "locationTimestamp";

    // Represents the current state of the LocationPoint: being collected,
    // or finished.
    public static final String STATUS_COLLECTING = "statusCollecting";
    public static final String STATUS_COMPLETED = "statusCompleted";

    // Our database for LocationPoints
    @Inject transient LocationPointsStorage locationPointsStorage;

    protected LocationPoint self() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] self");
        }

        return this;
    }

    protected LocationPointsStorage getStorage() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getStorage");
        }

        return locationPointsStorage;
    }

    /**
     * Get the latitude of the {@code LocationPoint}.
     *
     * @return Latitude of the {@code LocationPoint}
     */
    public double getLocationLatitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationLatitude");
        }

        return locationLatitude;
    }

    /**
     * Get the longitude of the {@code LocationPoint}.
     *
     * @return Longitude of the {@code LocationPoint}
     */
    public double getLocationLongitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationLongitude");
        }

        return locationLongitude;
    }

    /**
     * Get the altitude of the {@code LocationPoint}.
     *
     * @return Altitude of the {@code LocationPoint} in meters
     */
    public double getLocationAltitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationAltitude");
        }

        return locationAltitude;
    }

    /**
     * Get the accuracy of the {@code LocationPoint}.
     *
     * @return Accuracy of the {@code LocationPoint} in meters
     */
    public double getLocationAccuracy() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationAccuracy");
        }

        return locationAccuracy;
    }

    /**
     * Set the latitude of the {@code LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationLatitude Latitude to set
     */
    public void setLocationLatitude(double locationLatitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationLatitude");
        }

        this.locationLatitude = locationLatitude;
        saveIfSync();
    }

    /**
     * Set the longitude of the {@code LocationPoint},
     * and persist to database if necessary.
     *
     * @param locationLongitude Longitude to set
     */
    public void setLocationLongitude(double locationLongitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationLongitude");
        }

        this.locationLongitude = locationLongitude;
        saveIfSync();
    }

    /**
     * Set the altitude of the {@code LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationAltitude Altitude to set, in meters
     */
    public void setLocationAltitude(double locationAltitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationAltitude");
        }

        this.locationAltitude = locationAltitude;
        saveIfSync();
    }

    /**
     * Set the accuracy of the {@code LocationPoint}, and persist to database
     * if necessary.
     *
     * @param locationAccuracy Accuracy to set, in meters
     */
    public void setLocationAccuracy(double locationAccuracy) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationAccuracy");
        }

        this.locationAccuracy = locationAccuracy;
        saveIfSync();
    }

    /**
     * Set the location values for the {@code LocationPoint},
     * and persist to database if necessary. This includes latitude,
     * longitude, altitude, and accuracy.
     *
     * @param location {@code Location} instance from which to take the data
     */
    public void setLocation(Location location) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocation");
        }

        if (location != null) {
            locationLatitude = location.getLatitude();
            locationLongitude = location.getLongitude();
            locationAltitude = location.getAltitude();
            locationAccuracy = location.getAccuracy();
        }
        saveIfSync();
    }

    /**
     * Get the {@code LocationPoint}'s timestamp.
     *
     * @return Timestamp, usually in milliseconds since epoch
     */
    public long getTimestamp() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getTimestamp");
        }

        return timestamp;
    }

    /**
     * Set the {@code LocationPoint}'s timestamp, and persist to database
     * if necessary.
     *
     * @param timestamp Timestamp to set, usually in milliseconds since epoch
     */
    public void setTimestamp(long timestamp) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setTimestamp");
        }

        this.timestamp = timestamp;
        saveIfSync();
    }

    /**
     * Test whether a timestamp and a location have been set.
     * <p/>
     * Use this to determine if it is worth saving the {@code
     * LocationPoint} to the database yet or not: if there is either no
     * timestamp or no location, saving this instance to the database
     * should be used only if you need persistence between contexts or
     * through time ; otherwise it's potentially unusable incomplete data,
     * and you're better off waiting for the {@code LocationPoint} to be
     * complete before persisting to the database.
     *
     * @return {@code boolean} indicating if a timestamp and a location
     *         have been set (i.e. it's {@code false} if either one is
     *         missing)
     */
    public boolean isComplete() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] isComplete");
        }

        boolean hasLocation = locationLatitude != -1 &&
                locationLongitude !=-1 &&
                locationAltitude != -1 &&
                locationAccuracy != -1;
        return timestamp != -1 && hasLocation;
    }

}
