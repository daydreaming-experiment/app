package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequenceDescriptionFactory;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class QuestionInstanceCreator implements InstanceCreator<Question> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionInstanceCreator";

    @Inject QuestionFactory questionFactory;

    @Override
    public Question createInstance(Type type) {
        Logger.v(TAG, "Creating new Question instance");
        return questionFactory.create();
    }

}
