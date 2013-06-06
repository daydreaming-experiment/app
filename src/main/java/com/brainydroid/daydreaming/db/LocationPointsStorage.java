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

    // Table name for our location points
    private static final String TABLE_LOCATION_POINTS = "locationPoints";

    // SQL command to create the table
    private static final String SQL_CREATE_TABLE_LOCATIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_POINTS + " (" +
                    LocationPoint.COL_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LocationPoint.COL_STATUS + " TEXT NOT NULL, " +
                    LocationPoint.COL_LOCATION_LATITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_LONGITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ALTITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ACCURACY + " REAL, " +
                    LocationPoint.COL_TIMESTAMP + " REAL" +
                    ");";

    @Inject LocationPointFactory locationPointFactory;

    @Inject
    public LocationPointsStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected String[] getTableCreationStrings() {
        return new String[] {SQL_CREATE_TABLE_LOCATIONS};
    }

    @Override
    protected String getMainTable() {
        return TABLE_LOCATION_POINTS;
    }

    @Override
    protected ContentValues getModelValues(LocationPoint locationPoint) {
        Logger.v(TAG, "Building LocationPoint values");

        ContentValues locationPointValues =
                super.getModelValues(locationPoint);

        // Only add values relative to this level. I.e. id and status are set
        // in the parent classes.
        locationPointValues.put(LocationPoint.COL_LOCATION_LATITUDE,
                locationPoint.getLocationLatitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_LONGITUDE,
                locationPoint.getLocationLongitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_ALTITUDE,
                locationPoint.getLocationAltitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_ACCURACY,
                locationPoint.getLocationAccuracy());
        locationPointValues.put(LocationPoint.COL_TIMESTAMP,
                locationPoint.getTimestamp());
        return locationPointValues;
    }

    @Override
    protected LocationPoint create() {
        return locationPointFactory.create();
    }

    @Override
    protected void populateModel(int locationPointId, LocationPoint
                                 locationPoint, Cursor res) {
        Logger.d(TAG, "Populating LocationPoint model from db");

        super.populateModel(locationPointId, locationPoint, res);

        // Only populate with values relative to this level. As in
        // getModelValues(), id and status are set in the parent classes.
        locationPoint.setLocationLatitude(res.getDouble(
                res.getColumnIndex(LocationPoint.COL_LOCATION_LATITUDE)));
        locationPoint.setLocationLongitude(res.getDouble(
                res.getColumnIndex(LocationPoint.COL_LOCATION_LONGITUDE)));
        locationPoint.setLocationAltitude(res.getDouble(
                res.getColumnIndex(LocationPoint.COL_LOCATION_ALTITUDE)));
        locationPoint.setLocationAccuracy(res.getDouble(
                res.getColumnIndex(LocationPoint.COL_LOCATION_ACCURACY)));
        locationPoint.setTimestamp(res.getLong(
                res.getColumnIndex(LocationPoint.COL_TIMESTAMP)));
    }

    /**
     * Retrieve uploadable {@link LocationPoint}s,
     * that is {@link LocationPoint}s that have a status of {@link
     * LocationPoint#STATUS_COMPLETED}.
     *
     * @return An {@link ArrayList} of completed {@link LocationPoint}s
     */
    public ArrayList<LocationPoint> getUploadableLocationPoints() {
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
    public ArrayList<LocationPoint> getCollectingLocationPoints() {
        Logger.d(TAG, "Getting collecting LocationPoints");
        return getModelsWithStatuses(
                new String[] {LocationPoint.STATUS_COLLECTING});
    }

}
