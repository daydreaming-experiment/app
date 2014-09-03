package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class PageGroupDescriptionInstanceCreator
        implements InstanceCreator<PageGroupDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupDescriptionInstanceCreator";

    @Inject PageGroupDescriptionFactory pageGroupDescriptionFactory;

    @Override
    public PageGroupDescription createInstance(Type type) {
        Logger.v(TAG, "Creating new PageGroupDescription instance");
        return pageGroupDescriptionFactory.create();
    }

}
