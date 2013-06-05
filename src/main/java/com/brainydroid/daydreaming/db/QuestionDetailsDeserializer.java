package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;

public class QuestionDetailsDeserializer
        implements JsonDeserializer<IQuestionDetails> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDetailsDeserializer";

    @SuppressWarnings("FieldCanBeLocal")
    private static String QUESTION_DETAILS_SUFFIX = "QuestionDetails";

    @Inject Injector injector;

    @Override
    public IQuestionDetails deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Deserializing question details");

        String PACKAGE_PREFIX = getClass().getPackage().getName() + ".";
        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(PACKAGE_PREFIX +
                    obj.get("type").getAsString() + QUESTION_DETAILS_SUFFIX);
            IQuestionDetails questionDetails = context.deserialize(json,
                    klass);
            injector.injectMembers(questionDetails);
            return questionDetails;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

}
