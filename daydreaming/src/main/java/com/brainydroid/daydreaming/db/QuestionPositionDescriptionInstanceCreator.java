package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class QuestionPositionDescriptionInstanceCreator
        implements InstanceCreator<QuestionPositionDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionPositionDescriptionInstanceCreator";

    @Inject QuestionPositionDescriptionFactory questionPositionDescriptionFactory;

    @Override
    public QuestionPositionDescription createInstance(Type type) {
        Logger.v(TAG, "Creating new QuestionPositionDescription instance");
        return questionPositionDescriptionFactory.create();
    }

}
