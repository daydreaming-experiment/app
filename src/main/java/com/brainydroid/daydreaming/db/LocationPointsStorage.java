package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

// TODO: factor most of this into Storage
@Singleton
public final class LocationPointsStorage extends
        StatusModelStorage<LocationPoint,LocationPointsStorage> {

    private static String TAG = "LocationPointsStorage";

    private static final String TABLE_LOCATION_POINTS = "locationPoints";

    private static final String SQL_CREATE_TABLE_LOCATIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_POINTS + " (" +
                    LocationPoint.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

    protected String[] getTableCreationStrings() {
        return new String[] {SQL_CREATE_TABLE_LOCATIONS};
    }

    protected String getMainTable() {
        return TABLE_LOCATION_POINTS;
    }

    @Override
    protected ContentValues getModelValues(LocationPoint locationPoint) {
        Logger.v(TAG, "Building LocationPoint values");

        ContentValues locationPointValues =
                super.getModelValues(locationPoint);
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
        locationPoint.setLocationLatitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LATITUDE)));
        locationPoint.setLocationLongitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LONGITUDE)));
        locationPoint.setLocationAltitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ALTITUDE)));
        locationPoint.setLocationAccuracy(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ACCURACY)));
        locationPoint.setTimestamp(res.getLong(res.getColumnIndex(LocationPoint.COL_TIMESTAMP)));
    }

    public ArrayList<LocationPoint> getUploadableLocationPoints() {
        Logger.d(TAG, "Getting uploadable LocationPoints");
        return getModelsWithStatuses(
                new String[] {LocationPoint.STATUS_COMPLETED});
    }

    public ArrayList<LocationPoint> getCollectingLocationPoints() {
        Logger.d(TAG, "Getting collecting LocationPoints");
        return getModelsWithStatuses(
                new String[] {LocationPoint.STATUS_COLLECTING});
    }

}
