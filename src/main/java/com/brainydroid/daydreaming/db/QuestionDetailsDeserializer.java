package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestionDetailsDeserializer
        implements JsonDeserializer<IQuestionDetails> {

    private static String TAG = "QuestionDetailsDeserializer";

    private static String QUESTION_DETAILS_SUFFIX = "QuestionDetails";

    @Override
    public IQuestionDetails deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
            throws JsonParseException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] deserialize");
        }

        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(obj.get("type").getAsString() +
                    QUESTION_DETAILS_SUFFIX);
            return (IQuestionDetails)context.deserialize(json, klass);
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
