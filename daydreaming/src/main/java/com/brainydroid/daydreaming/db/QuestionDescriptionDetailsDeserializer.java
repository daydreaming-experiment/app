package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;

public class QuestionDescriptionDetailsDeserializer
        implements JsonDeserializer<IQuestionDescriptionDetails> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDetailsDeserializer";

    @SuppressWarnings("FieldCanBeLocal")
    private static String QUESTION_DETAILS_SUFFIX = "QuestionDescriptionDetails";

    @Inject Injector injector;

    @Override
    public IQuestionDescriptionDetails deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
            throws JsonParseException {
        Logger.v(TAG, "Deserializing question details");

        String PACKAGE_PREFIX = getClass().getPackage().getName() + ".";
        try {
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(PACKAGE_PREFIX +
                    obj.get("type").getAsString() + QUESTION_DETAILS_SUFFIX);
            IQuestionDescriptionDetails questionDetails = context.deserialize(json,
                    klass);

            // No QuestionDetails class can have injection or this line will override any
            // value set by Json. To use injection in those classes, register Factories and
            // InstanceCreators on Json and AppModule, and remove this line (since the injection
            // will be done in the instance creation in context.deserialize() above. But, please,
            // only do this once #160 is fixed.
            injector.injectMembers(questionDetails);
            return questionDetails;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

}
