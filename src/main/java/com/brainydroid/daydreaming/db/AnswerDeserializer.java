package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;

public class AnswerDeserializer implements JsonDeserializer<IAnswer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AnswerDeserializer";

    @SuppressWarnings("FieldCanBeLocal")
    private static String ANSWER_SUFFIX = "Answer";

    @Inject Injector injector;

    @Override
    public IAnswer deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context)
            throws JsonParseException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] deserialize");
        }

        String PACKAGE_PREFIX = getClass().getPackage().getName() + ".";
        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(PACKAGE_PREFIX +
                    obj.get("type").getAsString() + ANSWER_SUFFIX);
            IAnswer answer = context.deserialize(json, klass);
            injector.injectMembers(answer);
            return answer;
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
