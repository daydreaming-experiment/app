package com.brainydroid.daydreaming.db;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;

import javax.inject.Inject;

/**
 * Singleton JSON serializer and deserializer to centralize registration of
 * custom adapters.
 * <p/>
 * Here is the place to register type adapters for custom serializing and
 * deserializing of classes. This is used for deserializing interfaces
 * (like {@link IAnswer}, {@link IQuestionDetails},
 * custom instance creation ({@link Question} instances),
 * and useful serialization and deserialization of other classes (here
 * {@code Location} instances).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 */
@Singleton
public class Json {

    private static String TAG = "Json";

    // Standard Gson instance
    private Gson gson;

    // Gson instance serializing only @Expose-annotated members
    private Gson gsonExposed;

    /**
     * Constructor used with dependency injection.
     *
     * @param gsonBuilder An instance of {@code GsonBuilder}
     * @param answerDeserializer An instance of {@link AnswerDeserializer}
     * @param questionDetailsDeserializer An instance of {@link
     *                                    QuestionDetailsDeserializer}
     * @param questionInstanceCreator An instance of {@link
     *                                QuestionInstanceCreator}
     * @param locationDeserializer An instance of {@link
     *                             LocationDeserializer}
     * @param locationSerializer An instance of {@link LocationSerializer}
     */
    @Inject
    public Json(GsonBuilder gsonBuilder,
                AnswerDeserializer answerDeserializer,
                QuestionDetailsDeserializer questionDetailsDeserializer,
                QuestionInstanceCreator questionInstanceCreator,
                LocationDeserializer locationDeserializer,
                LocationSerializer locationSerializer) {
        Logger.v(TAG, "Building Gson instances");

        // Register all our type adapters
        gsonBuilder.registerTypeAdapter(IAnswer.class, answerDeserializer);
        gsonBuilder.registerTypeAdapter(IQuestionDetails.class,
                questionDetailsDeserializer);
        gsonBuilder.registerTypeAdapter(Question.class,
                questionInstanceCreator);
        gsonBuilder.registerTypeAdapter(Location.class,
                locationDeserializer);
        gsonBuilder.registerTypeAdapter(Location.class, locationSerializer);

        // Build the two Gson instances
        gson = gsonBuilder.create();
        gsonExposed = gsonBuilder.excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    /**
     * Serialize an instance by including all its members (except {@code
     * transient}s).
     *
     * @param src Instance to serialize
     * @return JSON representation of {@code src}
     */
    public String toJson(Object src) {
        Logger.v(TAG, "Serializing to JSON");
        return gson.toJson(src);
    }

    /**
     * Serialize an instance by including only its {@code @Expose}-annotated
     * members.
     *
     * @param src Instance to serialize
     * @return JSON representation of {@code src}, including only exposed
     *         members
     */
    public String toJsonExposed(Object src) {
        Logger.v(TAG, "Serializing to JSON with only exposed members");
        return gsonExposed.toJson(src);
    }

    /**
     * Deserialize a JSON representation of an instance.
     *
     * @param json JSON representation to deserialize
     * @param classOfT Class to deserialize the JSON into
     * @param <T> Again the Class to deserialize the JSON into,
     *            if I understand well what {@code Class<T>} means
     * @return New instance of
     */
    public <T> T fromJson(String json, Class<T> classOfT) {
        Logger.v(TAG, "Deserializing from JSON");
        return gson.fromJson(json, classOfT);
    }

}
