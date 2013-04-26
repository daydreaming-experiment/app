package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class LocationsStorage {

	private static String TAG = "LocationsStorage";

	private static final String TABLE_LOCATIONS = "locations";

	private static final String SQL_CREATE_TABLE_LOCATIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONS + " (" +
                    LocationPoint.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					LocationPoint.COL_LOCATION_LATITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_LONGITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ALTITUDE + " REAL, " +
                    LocationPoint.COL_LOCATION_ACCURACY + " REAL, " +
                    LocationPoint.COL_TIMESTAMP + " REAL" +
					");";

    @Inject Storage storage;

	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;

	// Constructor from context
	public LocationsStorage() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] LocationsStorage");
		}

		rDb = storage.getWritableDatabase();
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_LOCATIONS); // creates db fields
	}

	private ContentValues getLocationItemContentValues(LocationPoint locationPoint) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItemContentValues");
		}

		ContentValues locationItemValues = new ContentValues();
        locationItemValues.put(LocationPoint.COL_LOCATION_LATITUDE, locationPoint.getLocationLatitude());
        locationItemValues.put(LocationPoint.COL_LOCATION_LONGITUDE, locationPoint.getLocationLongitude());
        locationItemValues.put(LocationPoint.COL_LOCATION_ALTITUDE, locationPoint.getLocationAltitude());
        locationItemValues.put(LocationPoint.COL_LOCATION_ACCURACY, locationPoint.getLocationAccuracy());
        locationItemValues.put(LocationPoint.COL_TIMESTAMP, locationPoint.getTimestamp());
		return locationItemValues;
	}

	private ContentValues getLocationItemContentValuesWithId(LocationPoint locationPoint) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItemContentValuesWithId");
		}

		ContentValues locationItemValues = getLocationItemContentValues(locationPoint);
		locationItemValues.put(LocationPoint.COL_ID, locationPoint.getId());
		return locationItemValues;
	}

	public void storeLocationItemGetId(LocationPoint locationPoint) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] storeLocationItemGetId");
		}

		ContentValues locationItemValues = getLocationItemContentValues(locationPoint);
		wDb.insert(TABLE_LOCATIONS, null, locationItemValues);
		Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationPoint.COL_ID}, null,
				null, null, null, LocationPoint.COL_ID + " DESC", "1");
		res.moveToFirst();
		int locationItemId = res.getInt(res.getColumnIndex(LocationPoint.COL_ID));
		res.close();
		locationPoint.setId(locationItemId);
	}

	public void updateLocationItem(LocationPoint locationPoint) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] updateLocationItem");
		}

		ContentValues locationItemValues = getLocationItemContentValuesWithId(locationPoint);
		int locationItemId = locationPoint.getId();
		wDb.update(TABLE_LOCATIONS, locationItemValues, LocationPoint.COL_ID + "=?",
				new String[] {Integer.toString(locationItemId)});
	}

	public LocationPoint getLocationItem(int locationItemId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItem");
		}

		Cursor res = rDb.query(TABLE_LOCATIONS, null, LocationPoint.COL_ID + "=?",
				new String[] {Integer.toString(locationItemId)}, null, null, null);
		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		LocationPoint locationPoint = new LocationPoint();
		locationPoint.setId(res.getInt(res.getColumnIndex(LocationPoint.COL_ID)));
        locationPoint.setLocationLatitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LATITUDE)));
        locationPoint.setLocationLongitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_LONGITUDE)));
        locationPoint.setLocationAltitude(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ALTITUDE)));
        locationPoint.setLocationAccuracy(res.getDouble(res.getColumnIndex(LocationPoint.COL_LOCATION_ACCURACY)));
        locationPoint.setTimestamp(res.getLong(res.getColumnIndex(LocationPoint.COL_TIMESTAMP)));
		res.close();

		return locationPoint;
	}

	public ArrayList<LocationPoint> getUploadableLocationItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getUploadableLocationItems");
		}

		return getAllLocationItems();
	}

	private ArrayList<Integer> getAllLocationItemIds() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAllLocationItemIds");
		}

		Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationPoint.COL_ID}, null, null,
				null, null, null);

		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		ArrayList<Integer> locationItemIds = new ArrayList<Integer>();
		do {
			locationItemIds.add(res.getInt(res.getColumnIndex(LocationPoint.COL_ID)));
		} while (res.moveToNext());

		return locationItemIds;
	}

	private ArrayList<LocationPoint> getAllLocationItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAllLocationItems");
		}

		ArrayList<Integer> locationItemIds = getAllLocationItemIds();

		if (locationItemIds == null) {
			return null;
		}

		ArrayList<LocationPoint> locationPoints = new ArrayList<LocationPoint>();

		for (int locationItemId : locationItemIds) {
			locationPoints.add(getLocationItem(locationItemId));
		}

		return locationPoints;
	}

	public void removeLocationItem(int locationItemId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeLocationItem");
		}

		wDb.delete(TABLE_LOCATIONS, LocationPoint.COL_ID + "=?", new String[] {Integer.toString(locationItemId)});
	}
}
