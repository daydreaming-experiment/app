package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;

/**
 * Deserialize a serialized {@link IAnswer}, making sure it comes out as
 * the proper class.
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see IAnswer
 */
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
        Logger.v(TAG, "Deserializing answer");

        // Prefix specifying the package name of the class to create
        String PACKAGE_PREFIX = getClass().getPackage().getName() + ".";

        try {
            // Deserialize into the proper class
            JsonObject obj = (JsonObject)json;
            Class klass = Class.forName(PACKAGE_PREFIX +
                    obj.get("type").getAsString() + ANSWER_SUFFIX);
            IAnswer answer = context.deserialize(json, klass);

            // Inject missing members, because Guice didn't create our object
            injector.injectMembers(answer);
            return answer;
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

}
