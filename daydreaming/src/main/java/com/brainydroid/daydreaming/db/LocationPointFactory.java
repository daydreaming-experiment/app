package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

/**
 * Create {@link LocationPoint}s whenever we need an instance that can't be
 * injected with Guice.
 * <p/>
 * Guice handles the implementation of this interface,
 * we only need to register it in a module (which is done in {@link
 * com.brainydroid.daydreaming.ui.AppModule}.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see LocationPoint
 */
public class LocationPointFactory
        extends ModelFactory<LocationPoint,LocationPointsStorage,LocationPointFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationPointFactory";

    @Inject Json json;

    public LocationPoint createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating locationPoint from json");
        return json.fromJson(jsonContent, LocationPoint.class);
    }

}
