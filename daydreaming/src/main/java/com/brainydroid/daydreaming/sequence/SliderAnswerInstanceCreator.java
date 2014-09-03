package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class SliderAnswerInstanceCreator implements InstanceCreator<SliderAnswer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderAnswerInstanceCreator";

    @Inject SliderAnswerFactory sliderAnswerFactory;

    @Override
    public SliderAnswer createInstance(Type type) {
        Logger.v(TAG, "Creating new SliderAnswer instance");
        return sliderAnswerFactory.create();
    }

}
