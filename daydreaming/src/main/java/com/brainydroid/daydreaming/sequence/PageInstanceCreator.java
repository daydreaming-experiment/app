package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class PageInstanceCreator implements InstanceCreator<Page> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageInstanceCreator";

    @Inject PageFactory pageFactory;

    @Override
    public Page createInstance(Type type) {
        Logger.v(TAG, "Creating new Page instance");
        return pageFactory.create();
    }

}
