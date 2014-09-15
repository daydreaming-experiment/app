package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import org.json.JSONException;

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
public class LocationPointJsonFactory
        extends ModelJsonFactory<LocationPoint,LocationPointsStorage,LocationPointJsonFactory> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationPointJsonFactory";

    @Inject Json json;
    @Inject ErrorHandler errorHandler;

    public LocationPoint createFromJson(String jsonContent) {
        Logger.v(TAG, "Creating locationPoint from json");
        try {
            return json.fromJson(jsonContent, LocationPoint.class);
        } catch (JSONException e) {
            errorHandler.handleBaseJsonError(jsonContent, e);
            throw new RuntimeException(e);
        }
    }

}
