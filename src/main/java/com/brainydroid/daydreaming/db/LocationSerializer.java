package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LocationSerializer implements JsonSerializer<Location> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationDeserializer";

    public static String LOCATION_LATITUDE = "latitude";
    public static String LOCATION_LONGITUDE = "longitude";
    public static String LOCATION_ALTITUDE = "altitude";
    public static String LOCATION_ACCURACY = "accuracy";

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "serialize");
        }

        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty(LOCATION_LATITUDE, src.getLatitude());
        jsonSrc.addProperty(LOCATION_LONGITUDE, src.getLongitude());
        jsonSrc.addProperty(LOCATION_ALTITUDE, src.getAltitude());
        jsonSrc.addProperty(LOCATION_ACCURACY, src.getAccuracy());

        return jsonSrc;
    }

}
