package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequenceDescriptionFactory;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class StarRatingAnswerInstanceCreator implements InstanceCreator<StarRatingAnswer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingAnswerInstanceCreator";

    @Inject StarRatingAnswerFactory starRatingAnswerFactory;

    @Override
    public StarRatingAnswer createInstance(Type type) {
        Logger.v(TAG, "Creating new StarRatingAnswer instance");
        return starRatingAnswerFactory.create();
    }

}
