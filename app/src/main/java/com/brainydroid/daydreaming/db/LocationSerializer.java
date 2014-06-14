package com.brainydroid.daydreaming.db;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Serialize a {@link Location} instance to JSON.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see Location
 * @see LocationSerializer
 */
public class LocationSerializer implements JsonSerializer<Location> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationSerializer";

    public static String LOCATION_LATITUDE = "latitude";
    public static String LOCATION_LONGITUDE = "longitude";
    public static String LOCATION_ALTITUDE = "altitude";
    public static String LOCATION_ACCURACY = "accuracy";

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Serializing location");

        // Serialize the properties we're interested in, nothing else
        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty(LOCATION_LATITUDE, src.getLatitude());
        jsonSrc.addProperty(LOCATION_LONGITUDE, src.getLongitude());
        jsonSrc.addProperty(LOCATION_ALTITUDE, src.getAltitude());
        jsonSrc.addProperty(LOCATION_ACCURACY, src.getAccuracy());

        return jsonSrc;
    }

}
