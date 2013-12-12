package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

/**
 * Store and retrieve {@link LocationPoint} items stored in an SQLite database.
 * This class inherits most of its logic from {@link StatusModelStorage},
 * and you should read its documentation to understand how this class works.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Model
 * @see StatusModel
 * @see ModelStorage
 * @see StatusModelStorage
 * @see LocationPoint
 */
@Singleton
public final class LocationPointsStorage extends
        StatusModelStorage<LocationPoint,LocationPointsStorage> {

    private static String TAG = "LocationPointsStorage";

    /** Column name for {@link LocationPoint} latitude in the database */
    public static final String COL_LOCATION_LATITUDE =
            "locationLocationLatitude";
    /** Column name for {@link LocationPoint} longitude in the database */
    public static final String COL_LOCATION_LONGITUDE =
            "locationLocationLongitude";
    /** Column name for {@link LocationPoint} altitude in the database */
    public static final String COL_LOCATION_ALTITUDE =
            "locationLocationAltitude";
    /** Column name for {@link LocationPoint} accuracy in the database */
    public static final String COL_LOCATION_ACCURACY =
            "locationLocationAccuracy";
    /** Column name for {@link LocationPoint} timestamp in the database */
    public static final String COL_TIMESTAMP = "locationTimestamp";

    // Table name for our location points
    private static final String TABLE_LOCATION_POINTS = "locationPoints";

    // SQL command to create the table
    private static final String SQL_CREATE_TABLE_LOCATIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_POINTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_STATUS + " TEXT NOT NULL, " +
                    COL_LOCATION_LATITUDE + " REAL, " +
                    COL_LOCATION_LONGITUDE + " REAL, " +
                    COL_LOCATION_ALTITUDE + " REAL, " +
                    COL_LOCATION_ACCURACY + " REAL, " +
                    COL_TIMESTAMP + " REAL" +
                    ");";

    @Inject LocationPointFactory locationPointFactory;

    @Inject
    public LocationPointsStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized String[] getTableCreationStrings() {
        return new String[] {SQL_CREATE_TABLE_LOCATIONS};
    }

    @Override
    protected synchronized String getMainTable() {
        return TABLE_LOCATION_POINTS;
    }

    @Override
    protected synchronized ContentValues getModelValues(
            LocationPoint locationPoint) {
        Logger.v(TAG, "Building LocationPoint values");

        ContentValues locationPointValues =
                super.getModelValues(locationPoint);

        // Only add values relative to this level. I.e. id and status are set
        // in the parent classes.
        locationPointValues.put(COL_LOCATION_LATITUDE,
                locationPoint.getLocationLatitude());
        locationPointValues.put(COL_LOCATION_LONGITUDE,
                locationPoint.getLocationLongitude());
        locationPointValues.put(COL_LOCATION_ALTITUDE,
                locationPoint.getLocationAltitude());
        locationPointValues.put(COL_LOCATION_ACCURACY,
                locationPoint.getLocationAccuracy());
        locationPointValues.put(COL_TIMESTAMP,
                locationPoint.getTimestamp());
        return locationPointValues;
    }

    @Override
    protected synchronized LocationPoint create() {
        return locationPointFactory.create();
    }

    @Override
    protected synchronized void populateModel(int locationPointId,
                                              LocationPoint locationPoint,
                                              Cursor res) {
        Logger.d(TAG, "Populating LocationPoint model from db");

        super.populateModel(locationPointId, locationPoint, res);

        // Only populate with values relative to this level. As in
        // getModelValues(), id and status are set in the parent classes.
        locationPoint.setLocationLatitude(res.getDouble(
                res.getColumnIndex(COL_LOCATION_LATITUDE)));
        locationPoint.setLocationLongitude(res.getDouble(
                res.getColumnIndex(COL_LOCATION_LONGITUDE)));
        locationPoint.setLocationAltitude(res.getDouble(
                res.getColumnIndex(COL_LOCATION_ALTITUDE)));
        locationPoint.setLocationAccuracy(res.getDouble(
                res.getColumnIndex(COL_LOCATION_ACCURACY)));
        locationPoint.setTimestamp(res.getLong(
                res.getColumnIndex(COL_TIMESTAMP)));
    }

    /**
     * Retrieve uploadable {@link LocationPoint}s,
     * that is {@link LocationPoint}s that have a status of {@link
     * LocationPoint#STATUS_COMPLETED}.
     *
     * @return An {@link ArrayList} of completed {@link LocationPoint}s
     */
    public synchronized ArrayList<LocationPoint>
    getUploadableLocationPoints() {
        Logger.d(TAG, "Getting uploadable LocationPoints");
        return getModelsWithStatuses(
                new String[] {LocationPoint.STATUS_COMPLETED});
    }

    /**
     * Retrieve {@link LocationPoint}s marked as currently collecting
     * location data. Those are the {@link LocationPoint}s that have a {@link
     * LocationPoint#STATUS_COLLECTING} status.
     *
     * @return An {@link ArrayList} of currently collecting {@link
     *         LocationPoint}s
     */
    public synchronized ArrayList<LocationPoint>
    getCollectingLocationPoints() {
        Logger.d(TAG, "Getting collecting LocationPoints");
        return getModelsWithStatuses(
                new String[] {LocationPoint.STATUS_COLLECTING});
    }

    public synchronized void removeLocationPoints(ArrayList<LocationPoint> locationPoints) {
        Logger.d(TAG, "Removing multiple LocationPoints");

        if (locationPoints != null){
            for (LocationPoint locationPoint : locationPoints) {
                remove(locationPoint.getId());
            }
        }
    }

    public synchronized void removeUploadableLocationPoints() {
        Logger.d(TAG, "Removing uploadable LocationPoints");
        removeLocationPoints(getUploadableLocationPoints());
    }

}
