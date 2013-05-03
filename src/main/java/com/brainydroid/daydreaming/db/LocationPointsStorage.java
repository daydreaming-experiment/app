package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

// TODO: factor most of this into Storage
@Singleton
public class LocationPointsStorage {

    private static String TAG = "LocationPointsStorage";

    private static final String TABLE_LOCATIONS = "locations";

    private static final String SQL_CREATE_TABLE_LOCATIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONS + " (" +
                    LocationPoint.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LocationPoint.COL_STATUS + " TEXT NOT NULL, " +
                    LocationPoint.COL_LOCATION_LATITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_LONGITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ALTITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ACCURACY + " REAL, " +
                    LocationPoint.COL_TIMESTAMP + " REAL" +
                    ");";

    private final SQLiteDatabase rDb;
    private final SQLiteDatabase wDb;

    // Constructor from context
    @Inject
    public LocationPointsStorage(Storage storage) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] LocationPointsStorage");
        }

        rDb = storage.getWritableDatabase();
        wDb = storage.getWritableDatabase();
        wDb.execSQL(SQL_CREATE_TABLE_LOCATIONS); // creates db fields
    }

    private ContentValues getLocationPointContentValues(LocationPoint locationPoint) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPointContentValues");
        }

        ContentValues locationPointValues = new ContentValues();
        locationPointValues.put(LocationPoint.COL_STATUS, locationPoint.getStatus());
        locationPointValues.put(LocationPoint.COL_LOCATION_LATITUDE, locationPoint.getLocationLatitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_LONGITUDE, locationPoint.getLocationLongitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_ALTITUDE, locationPoint.getLocationAltitude());
        locationPointValues.put(LocationPoint.COL_LOCATION_ACCURACY, locationPoint.getLocationAccuracy());
        locationPointValues.put(LocationPoint.COL_TIMESTAMP, locationPoint.getTimestamp());
        return locationPointValues;
    }

    private ContentValues getLocationPointContentValuesWithId(LocationPoint locationPoint) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPointContentValuesWithId");
        }

        ContentValues locationPointValues = getLocationPointContentValues(locationPoint);
        locationPointValues.put(LocationPoint.COL_ID, locationPoint.getId());
        return locationPointValues;
    }

    public void storeLocationPointSetId(LocationPoint locationPoint) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] storeLocationPointSetId");
        }

        ContentValues locationPointValues = getLocationPointContentValues(locationPoint);
        wDb.insert(TABLE_LOCATIONS, null, locationPointValues);

        Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationPoint.COL_ID}, null,
                null, null, null, LocationPoint.COL_ID + " DESC", "1");
        res.moveToFirst();
        int locationPointId = res.getInt(res.getColumnIndex(LocationPoint.COL_ID));
        res.close();

        locationPoint.setId(locationPointId);
    }

    public void updateLocationPoint(LocationPoint locationPoint) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updateLocationPoint");
        }

        ContentValues locationItemValues = getLocationPointContentValuesWithId(locationPoint);
        int locationItemId = locationPoint.getId();
        wDb.update(TABLE_LOCATIONS, locationItemValues, LocationPoint.COL_ID + "=?",
                new String[] {Integer.toString(locationItemId)});
    }

    public LocationPoint getLocationPoint(int locationPointId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPoint");
        }

        Cursor res = rDb.query(TABLE_LOCATIONS, null, LocationPoint.COL_ID + "=?",
                new String[] {Integer.toString(locationPointId)}, null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        LocationPoint locationPoint = new LocationPoint();
        locationPoint.setStatus(res.getString(res.getColumnIndex(LocationPoint.COL_STATUS)));
        locationPoint.setLocationLatitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LATITUDE)));
        locationPoint.setLocationLongitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LONGITUDE)));
        locationPoint.setLocationAltitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ALTITUDE)));
        locationPoint.setLocationAccuracy(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ACCURACY)));
        locationPoint.setTimestamp(res.getLong(res.getColumnIndex(LocationPoint.COL_TIMESTAMP)));
        // Setting the id at the end ensures we don't save the LocationPoint to DB again
        locationPoint.setId(res.getInt(res.getColumnIndex(LocationPoint.COL_ID)));
        res.close();

        return locationPoint;
    }

    public ArrayList<LocationPoint> getUploadableLocationPoints() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getUploadableLocationPoints");
        }

        return getLocationPointsWithStatuses(
                new String[] {LocationPoint.STATUS_COMPLETED});
    }

    public ArrayList<LocationPoint> getCollectingLocationPoints() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getCollectingLocationPoints");
        }

        return getLocationPointsWithStatuses(
                new String[] {LocationPoint.STATUS_COLLECTING});
    }

    private ArrayList<Integer> getLocationPointIdsWithStatuses(
            String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPointIdsWithStatuses " +
                    "(from String[])");
        }

        return getLocationPointIdsWithStatuses(statuses, null);
    }

    private ArrayList<Integer> getLocationPointIdsWithStatuses(
            String[] statuses, String limit) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPointIdsWithStatuses (from " +
                    "String[], String)");
        }

        String query = Util.multiplyString(LocationPoint.COL_STATUS + "=?",
                statuses.length, " OR ");
        Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationPoint.COL_ID},
                query, statuses, null, null, null, limit);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<Integer> statusLocationPointIds = new ArrayList<Integer>();
        do {
            statusLocationPointIds.add(res.getInt(res.getColumnIndex(LocationPoint.COL_ID)));
        } while (res.moveToNext());

        return statusLocationPointIds;
    }

    private ArrayList<LocationPoint> getLocationPointsWithStatuses(
            String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationPointsWithStatuses");
        }

        ArrayList<Integer> statusLocationPointIds =
                getLocationPointIdsWithStatuses(statuses);

        if (statusLocationPointIds == null) {
            return null;
        }

        ArrayList<LocationPoint> statusLocationPoints =
                new ArrayList<LocationPoint>();

        for (int locationPointId : statusLocationPointIds) {
            statusLocationPoints.add(getLocationPoint(locationPointId));
        }

        return statusLocationPoints;
    }

    public void removeLocationPoint(int locationPointId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removeLocationPoint");
        }

        wDb.delete(TABLE_LOCATIONS, LocationPoint.COL_ID + "=?", new String[] {Integer.toString(locationPointId)});
    }

    public void removeLocationPoints(
            ArrayList<LocationPoint> locationPoints) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removeLocationPoints");
        }

        for (LocationPoint locationPoint : locationPoints) {
            removeLocationPoint(locationPoint.getId());
        }
    }

}
