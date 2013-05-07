package com.brainydroid.daydreaming.db;

import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestionDeserializer implements JsonDeserializer<IQuestion> {

    @Override
    public IQuestion deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context)
            throws JsonParseException {
        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(obj.get("className").getAsString());
            return (IQuestion)context.deserialize(json, klass);
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
