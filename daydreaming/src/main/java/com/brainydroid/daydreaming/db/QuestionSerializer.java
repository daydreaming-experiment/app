package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collection;

public class QuestionSerializer implements JsonSerializer<Question> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionSerializer";

    @Override
    public JsonElement serialize(Question src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Serializing question");

        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty("name", src.getName());
        jsonSrc.addProperty("status", src.getStatus());
        jsonSrc.add("answer", context.serialize(src.getAnswer()));
        jsonSrc.add("location", context.serialize(src.getLocation()));
        jsonSrc.addProperty("ntpTimestamp", src.getNtpTimestamp());
        jsonSrc.addProperty("systemTimestamp", src.getSystemTimestamp());

        return jsonSrc;
    }

}
