package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LocationDeserializer implements JsonDeserializer<Location> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationDeserializer";

    @Override
    public Location deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext context)
            throws JsonParseException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "deserialize");
        }

        // The given provider is just a dummy, we don't use it.
        Location location = new Location(LocationManager.NETWORK_PROVIDER);

        JsonObject obj = (JsonObject)jsonElement;
        location.setLatitude(
                obj.get(LocationSerializer.LOCATION_LATITUDE).getAsDouble());
        location.setLongitude(
                obj.get(LocationSerializer.LOCATION_LONGITUDE).getAsDouble());
        location.setAltitude(
                obj.get(LocationSerializer.LOCATION_ALTITUDE).getAsDouble());
        location.setAccuracy(
                obj.get(LocationSerializer.LOCATION_ACCURACY).getAsFloat());

        return location;
    }

}
