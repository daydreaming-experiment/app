package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;

import java.lang.reflect.Type;

public class QuestionInstanceCreator implements InstanceCreator<Question> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionInstanceCreator";

    @Inject QuestionFactory questionFactory;

    @Override
    public Question createInstance(Type type) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createInstance");
        }

        Question question = questionFactory.create();
        return question;
    }

}
