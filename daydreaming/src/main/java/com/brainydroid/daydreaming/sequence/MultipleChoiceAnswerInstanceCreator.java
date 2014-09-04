package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class MultipleChoiceAnswerInstanceCreator implements InstanceCreator<MultipleChoiceAnswer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "MultipleChoiceAnswerInstanceCreator";

    @Inject MultipleChoiceAnswerFactory multipleChoiceAnswerFactory;

    @Override
    public MultipleChoiceAnswer createInstance(Type type) {
        Logger.v(TAG, "Creating new MultipleChoiceAnswer instance");
        return multipleChoiceAnswerFactory.create();
    }

}
