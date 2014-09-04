package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collection;

public class QuestionDescriptionSerializer implements JsonSerializer<QuestionDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescriptionSerializer";

    @Override
    public JsonElement serialize(QuestionDescription src, Type typeOfSrc,
                                 JsonSerializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Serializing question description");

        JsonObject jsonSrc = new JsonObject();
        jsonSrc.addProperty("name", src.getName());
        jsonSrc.add("details", context.serialize(src.getDetails()));
        Logger.d(TAG, jsonSrc.toString().replace("{", "'{'").replace("}", "'}'"));

        return jsonSrc;
    }

}
