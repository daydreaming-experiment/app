package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class QuestionDescriptionInstanceCreator
        implements InstanceCreator<QuestionDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescriptionInstanceCreator";

    @Inject QuestionDescriptionFactory questionDescriptionFactory;

    @Override
    public QuestionDescription createInstance(Type type) {
        Logger.v(TAG, "Creating new QuestionDescription instance");
        return questionDescriptionFactory.create();
    }

}
