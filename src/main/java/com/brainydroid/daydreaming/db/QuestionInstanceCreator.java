package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;

public class QuestionInstanceCreator implements InstanceCreator<Question> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionInstanceCreator";

    @Inject Injector injector;

    @Override
    public Question createInstance(Type type) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] createInstance");
        }

        Question question = new Question();
        injector.injectMembers(question);
        return question;
    }

}
