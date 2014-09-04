package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class SequenceDescriptionInstanceCreator
        implements InstanceCreator<SequenceDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceDescriptionInstanceCreator";

    @Inject SequenceDescriptionFactory sequenceDescriptionFactory;

    @Override
    public SequenceDescription createInstance(Type type) {
        Logger.v(TAG, "Creating new SequenceDescription instance");
        return sequenceDescriptionFactory.create();
    }

}
