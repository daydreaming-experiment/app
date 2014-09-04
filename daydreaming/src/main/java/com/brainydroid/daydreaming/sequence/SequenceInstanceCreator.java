package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class SequenceInstanceCreator implements InstanceCreator<Sequence> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceInstanceCreator";

    @Inject SequenceFactory sequenceFactory;

    @Override
    public Sequence createInstance(Type type) {
        Logger.v(TAG, "Creating new Sequence instance");
        return sequenceFactory.create();
    }

}
