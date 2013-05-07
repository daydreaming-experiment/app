package com.brainydroid.daydreaming.db;

import com.google.gson.*;

import java.lang.reflect.Type;

public class AnswerDeserializer implements JsonDeserializer<IAnswer> {

    @Override
    public IAnswer deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context)
            throws JsonParseException {
        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(obj.get("klass").getAsString());
            return (IAnswer)context.deserialize(json, klass);
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
