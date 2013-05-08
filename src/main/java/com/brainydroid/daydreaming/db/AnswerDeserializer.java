package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AnswerDeserializer implements JsonDeserializer<IAnswer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "AnswerDeserializer";

    @SuppressWarnings("FieldCanBeLocal")
    private static String ANSWER_SUFFIX = "Answer";

    @Override
    public IAnswer deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context)
            throws JsonParseException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] deserialize");
        }

        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(obj.get("type").getAsString() +
                    ANSWER_SUFFIX);
            return context.deserialize(json, klass);
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
