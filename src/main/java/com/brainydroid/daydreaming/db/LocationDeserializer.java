package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.location.LocationManager;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Deserialize a JSON representation of a {@code Location} instance.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
public class LocationDeserializer implements JsonDeserializer<Location> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationDeserializer";

    @Override
    public Location deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Deserializing location");

        // The given provider is just a dummy: we never use it in the rest
        // of the code, but it's required by the constructor.
        Location location = new Location(LocationManager.NETWORK_PROVIDER);

        // Fill up our Location instance
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
