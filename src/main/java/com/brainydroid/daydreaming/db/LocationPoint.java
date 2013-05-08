package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

public class LocationPoint {

    private static String TAG = "LocationPoint";

    private transient int id = -1;
    private transient String status;

    @Expose private double locationLatitude = -1;
    @Expose private double locationLongitude = -1;
    @Expose private double locationAltitude = -1;
    @Expose private double locationAccuracy = -1;
    @Expose private long timestamp = -1;

    public static final String COL_ID = "locationId";
    public static final String COL_STATUS = "locationStatus";
    public static final String COL_LOCATION_LATITUDE = "locationLocationLatitude";
    public static final String COL_LOCATION_LONGITUDE = "locationLocationLongitude";
    public static final String COL_LOCATION_ALTITUDE = "locationLocationAltitude";
    public static final String COL_LOCATION_ACCURACY = "locationLocationAccuracy";
    public static final String COL_TIMESTAMP = "locationTimestamp";

    public static final String STATUS_COLLECTING = "statusCollecting";
    public static final String STATUS_COMPLETED = "statusCompleted";

    @Inject transient LocationPointsStorage locationPointsStorage;

    public void setId(int id) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setId");
        }

        // This method is called either from LocationPointsStorage.storeLocationPointSetId(...) or
        // from LocationPointsStorage.getLocationPoint(...), and in both cases calling saveIfSync() would
        // trigger an unnecessary save. So we don't call it, contrary to other setters below.
        this.id = id;
    }

    public int getId() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getId");
        }

        return id;
    }

    public void setStatus(String status) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setStatus");
        }

        this.status = status;
        saveIfSync();
    }

    public String getStatus() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getStatus");
        }

        return status;
    }

    public double getLocationLatitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationLatitude");
        }

        return locationLatitude;
    }

    public double getLocationLongitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationLongitude");
        }

        return locationLongitude;
    }

    public double getLocationAltitude() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationAltitude");
        }

        return locationAltitude;
    }

    public double getLocationAccuracy() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocationAccuracy");
        }

        return locationAccuracy;
    }

    public void setLocationLatitude(double locationLatitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationLatitude");
        }

        this.locationLatitude = locationLatitude;
        saveIfSync();
    }

    public void setLocationLongitude(double locationLongitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationLongitude");
        }

        this.locationLongitude = locationLongitude;
        saveIfSync();
    }

    public void setLocationAltitude(double locationAltitude) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationAltitude");
        }

        this.locationAltitude = locationAltitude;
        saveIfSync();
    }

    public void setLocationAccuracy(double locationAccuracy) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocationAccuracy");
        }

        this.locationAccuracy = locationAccuracy;
        saveIfSync();
    }

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

    public long getTimestamp() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getTimestamp");
        }

        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setTimestamp");
        }

        this.timestamp = timestamp;
        saveIfSync();
    }

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

    private void saveIfSync() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveIfSync");
        }

        if (id != -1) {
            save();
        }
    }

    public void save() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] save");
        }

        if (id != -1) {
            locationPointsStorage.updateLocationPoint(this);
        } else {
            locationPointsStorage.storeLocationPointSetId(this);
        }
    }

}
