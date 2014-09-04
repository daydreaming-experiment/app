package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class LocationPointInstanceCreator implements InstanceCreator<LocationPoint> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "LocationPointInstanceCreator";

    @Inject LocationPointFactory locationPointFactory;

    @Override
    public LocationPoint createInstance(Type type) {
        Logger.v(TAG, "Creating new LocationPoint instance");
        return locationPointFactory.create();
    }

}
