package com.brainydroid.daydreaming.db;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LocationItem {

	private static String TAG = "LocationItem";

	private transient int _id;

    @Expose private double locationLatitude;
    @Expose private double locationLongitude;
    @Expose private double locationAltitude;
    @Expose private double locationAccuracy;
    @Expose private long timestamp;

    public static final String COL_ID = "locationId";
    public static final String COL_LOCATION_LATITUDE = "locationLocationLatitude";
    public static final String COL_LOCATION_LONGITUDE = "locationLocationLongitude";
    public static final String COL_LOCATION_ALTITUDE = "locationLocationAltitude";
    public static final String COL_LOCATION_ACCURACY = "locationLocationAccuracy";
    public static final String COL_TIMESTAMP = "locationTimestamp";

	private transient final Context _context;
	private transient final LocationsStorage locationsStorage;

	public LocationItem(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Poll");
		}

		_context = context.getApplicationContext();
		_id = -1;
        locationsStorage = LocationsStorage.getInstance(_context);
	}

	public int getId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getId");
		}

		return _id;
	}

	public void setId(int id) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setId");
		}

		_id = id;
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

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setLocationLatitude");
        }

        this.locationLatitude = locationLatitude;
    }

    public void setLocationLongitude(double locationLongitude) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setLocationLongitude");
        }

        this.locationLongitude = locationLongitude;
    }

    public void setLocationAltitude(double locationAltitude) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setLocationAltitude");
        }

        this.locationAltitude = locationAltitude;
    }

    public void setLocationAccuracy(double locationAccuracy) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setLocationAccuracy");
        }

        this.locationAccuracy = locationAccuracy;
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
    }

    public long getTimestamp() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getTimestamp");
        }

        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setTimestamp");
        }

        this.timestamp = timestamp;
    }

	private void saveIfSync() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveIfSync");
		}

		if (_id != -1) {
			save();
		}
	}

	public void save() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] save");
		}

		if (_id != -1) {
			locationsStorage.updateLocationItem(this);
		} else {
			locationsStorage.storeLocationItemGetId(this);
		}
	}
}
