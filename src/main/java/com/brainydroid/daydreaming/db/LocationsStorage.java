package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class LocationsStorage {

	private static String TAG = "LocationsStorage";

	private static LocationsStorage lsInstance = null;

	private static final String TABLE_LOCATIONS = "locations";

	private static final String SQL_CREATE_TABLE_LOCATIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONS + " (" +
                    LocationItem.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					LocationItem.COL_LOCATION_LATITUDE + " REAL, " +
                    LocationItem.COL_LOCATION_LONGITUDE + " REAL, " +
                    LocationItem.COL_LOCATION_ALTITUDE + " REAL, " +
                    LocationItem.COL_LOCATION_ACCURACY + " REAL, " +
                    LocationItem.COL_TIMESTAMP + " REAL" +
					");";

	private static final String SQL_DROP_TABLE_LOCATIONS =
			"DROP TABLE IF EXISTS " + TABLE_LOCATIONS + ";";

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;

	public static synchronized LocationsStorage getInstance(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

		if (lsInstance == null) {
			lsInstance = new LocationsStorage(context);
		}
		return lsInstance;
	}

	// Constructor from context
	private LocationsStorage(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] LocationsStorage");
		}

		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		rDb = storage.getWritableDatabase();
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_LOCATIONS); // creates db fields
	}

	private ContentValues getLocationItemContentValues(LocationItem locationItem) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItemContentValues");
		}

		ContentValues locationItemValues = new ContentValues();
        locationItemValues.put(LocationItem.COL_LOCATION_LATITUDE, locationItem.getLocationLatitude());
        locationItemValues.put(LocationItem.COL_LOCATION_LONGITUDE, locationItem.getLocationLongitude());
        locationItemValues.put(LocationItem.COL_LOCATION_ALTITUDE, locationItem.getLocationAltitude());
        locationItemValues.put(LocationItem.COL_LOCATION_ACCURACY, locationItem.getLocationAccuracy());
        locationItemValues.put(LocationItem.COL_TIMESTAMP, locationItem.getTimestamp());
		return locationItemValues;
	}

	private ContentValues getLocationItemContentValuesWithId(LocationItem locationItem) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItemContentValuesWithId");
		}

		ContentValues locationItemValues = getLocationItemContentValues(locationItem);
		locationItemValues.put(LocationItem.COL_ID, locationItem.getId());
		return locationItemValues;
	}

	public void storeLocationItemGetId(LocationItem locationItem) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] storeLocationItemGetId");
		}

		ContentValues locationItemValues = getLocationItemContentValues(locationItem);
		wDb.insert(TABLE_LOCATIONS, null, locationItemValues);
		Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationItem.COL_ID}, null,
				null, null, null, LocationItem.COL_ID + " DESC", "1");
		res.moveToFirst();
		int locationItemId = res.getInt(res.getColumnIndex(LocationItem.COL_ID));
		res.close();
		locationItem.setId(locationItemId);
	}

	public void updateLocationItem(LocationItem locationItem) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] updateLocationItem");
		}

		ContentValues locationItemValues = getLocationItemContentValuesWithId(locationItem);
		int locationItemId = locationItem.getId();
		wDb.update(TABLE_LOCATIONS, locationItemValues, LocationItem.COL_ID + "=?",
				new String[] {Integer.toString(locationItemId)});
	}

	public LocationItem getLocationItem(int locationItemId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLocationItem");
		}

		Cursor res = rDb.query(TABLE_LOCATIONS, null, LocationItem.COL_ID + "=?",
				new String[] {Integer.toString(locationItemId)}, null, null, null);
		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		LocationItem locationItem = new LocationItem(_context);
		locationItem.setId(res.getInt(res.getColumnIndex(LocationItem.COL_ID)));
        locationItem.setLocationLatitude(res.getDouble(res.getColumnIndex(LocationItem.COL_LOCATION_LATITUDE)));
        locationItem.setLocationLongitude(res.getDouble(res.getColumnIndex(LocationItem.COL_LOCATION_LONGITUDE)));
        locationItem.setLocationAltitude(res.getDouble(res.getColumnIndex(LocationItem.COL_LOCATION_ALTITUDE)));
        locationItem.setLocationAccuracy(res.getDouble(res.getColumnIndex(LocationItem.COL_LOCATION_ACCURACY)));
        locationItem.setTimestamp(res.getLong(res.getColumnIndex(LocationItem.COL_TIMESTAMP)));
		res.close();

		return locationItem;
	}

	public ArrayList<LocationItem> getUploadableLocationItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getUploadableLocationItems");
		}

		return getAllLocationItems();
	}

	public void cleanLocationItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] cleanLocationItems");
		}

        flushAll();
	}

	private ArrayList<Integer> getAllLocationItemIds() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAllLocationItemIds");
		}

		Cursor res = rDb.query(TABLE_LOCATIONS, new String[] {LocationItem.COL_ID}, null, null,
				null, null, null);

		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		ArrayList<Integer> locationItemIds = new ArrayList<Integer>();
		do {
			locationItemIds.add(res.getInt(res.getColumnIndex(LocationItem.COL_ID)));
		} while (res.moveToNext());

		return locationItemIds;
	}

	private ArrayList<LocationItem> getAllLocationItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAllLocationItems");
		}

		ArrayList<Integer> locationItemIds = getAllLocationItemIds();

		if (locationItemIds == null) {
			return null;
		}

		ArrayList<LocationItem> locationItems = new ArrayList<LocationItem>();

		for (int locationItemId : locationItemIds) {
			locationItems.add(getLocationItem(locationItemId));
		}

		return locationItems;
	}

	public void removeLocationItem(int locationItemId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeLocationItem");
		}

		wDb.delete(TABLE_LOCATIONS, LocationItem.COL_ID + "=?", new String[] {Integer.toString(locationItemId)});
	}

	public void flushAll() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] flushAll");
		}

		wDb.delete(TABLE_LOCATIONS, null, null);
	}

	public void dropAll() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] dropAll");
		}

		wDb.execSQL(SQL_DROP_TABLE_LOCATIONS);
		lsInstance = null;
	}
}
