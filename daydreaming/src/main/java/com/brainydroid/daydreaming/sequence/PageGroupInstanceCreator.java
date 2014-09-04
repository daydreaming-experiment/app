package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class PageGroupInstanceCreator implements InstanceCreator<PageGroup> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupInstanceCreator";

    @Inject PageGroupFactory pageGroupFactory;

    @Override
    public PageGroup createInstance(Type type) {
        Logger.v(TAG, "Creating new PageGroup instance");
        return pageGroupFactory.create();
    }

}
