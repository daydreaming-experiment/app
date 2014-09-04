package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class PageDescriptionInstanceCreator implements InstanceCreator<PageDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageDescriptionInstanceCreator";

    @Inject PageDescriptionFactory pageDescriptionFactory;

    @Override
    public PageDescription createInstance(Type type) {
        Logger.v(TAG, "Creating new PageDescription instance");
        return pageDescriptionFactory.create();
    }

}
