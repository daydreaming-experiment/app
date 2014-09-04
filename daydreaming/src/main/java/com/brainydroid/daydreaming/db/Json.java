package com.brainydroid.daydreaming.db;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.network.JWSSignature;
import com.brainydroid.daydreaming.network.JWSSignatureSerializer;
import com.brainydroid.daydreaming.sequence.IAnswer;
import com.brainydroid.daydreaming.sequence.MultipleChoiceAnswer;
import com.brainydroid.daydreaming.sequence.MultipleChoiceAnswerInstanceCreator;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.PageGroup;
import com.brainydroid.daydreaming.sequence.PageGroupInstanceCreator;
import com.brainydroid.daydreaming.sequence.PageInstanceCreator;
import com.brainydroid.daydreaming.sequence.Question;
import com.brainydroid.daydreaming.sequence.QuestionInstanceCreator;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.brainydroid.daydreaming.sequence.SequenceInstanceCreator;
import com.brainydroid.daydreaming.sequence.SliderAnswer;
import com.brainydroid.daydreaming.sequence.SliderAnswerInstanceCreator;
import com.brainydroid.daydreaming.sequence.StarRatingAnswer;
import com.brainydroid.daydreaming.sequence.StarRatingAnswerInstanceCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Singleton JSON serializer and deserializer to centralize registration of
 * custom adapters.
 * <p/>
 * Here is the place to register type adapters for custom serializing and
 * deserializing of classes. This is used for deserializing interfaces
 * (like {@link com.brainydroid.daydreaming.sequence.IAnswer}, {@link IQuestionDescriptionDetails},
 * and useful serialization and deserialization of other classes (here
 * {@link Location} instances).
 *
 * @author Sébastien Lerique
 * @author Vincent Adam
 * @see AnswerDeserializer
 * @see QuestionDescriptionDetailsDeserializer
 * @see LocationDeserializer
 * @see LocationSerializer
 */
@Singleton
public class Json {

    private static String TAG = "Json";

    private ObjectMapper jacksonLocal;
    private ObjectMapper jacksonServer;

    /**
     * Constructor used with dependency injection.
     *
     * @param gsonBuilder An instance of {@link GsonBuilder}
     * @param answerDeserializer An instance of {@link AnswerDeserializer}
     * @param questionDescriptionDetailsDeserializer An instance of {@link
     *                                    QuestionDescriptionDetailsDeserializer}
     * @param locationDeserializer An instance of {@link
     *                             LocationDeserializer}
     * @param locationSerializer An instance of {@link LocationSerializer}
     */
    @Inject
    public Json(GsonBuilder gsonBuilder,
                AnswerDeserializer answerDeserializer,
                QuestionDescriptionDetailsDeserializer questionDescriptionDetailsDeserializer,
                QuestionDescriptionSerializer questionDescriptionSerializer,
                SequenceDescriptionInstanceCreator sequenceDescriptionInstanceCreator,
                PageGroupDescriptionInstanceCreator pageGroupDescriptionInstanceCreator,
                PageDescriptionInstanceCreator pageDescriptionInstanceCreator,
                QuestionDescriptionInstanceCreator questionDescriptionInstanceCreator,
                QuestionPositionDescriptionInstanceCreator questionPositionDescriptionInstanceCreator,
                SequenceInstanceCreator sequenceInstanceCreator,
                PageGroupInstanceCreator pageGroupInstanceCreator,
                PageInstanceCreator pageInstanceCreator,
                QuestionInstanceCreator questionInstanceCreator,
                StarRatingAnswerInstanceCreator starRatingAnswerInstanceCreator,
                SliderAnswerInstanceCreator sliderAnswerInstanceCreator,
                MultipleChoiceAnswerInstanceCreator multipleChoiceAnswerInstanceCreator,
                LocationPointInstanceCreator locationPointInstanceCreator,
                LocationDeserializer locationDeserializer,
                LocationSerializer locationSerializer,
                JWSSignatureSerializer jwsSignatureSerializer) {
        Logger.v(TAG, "Building Jackson instances");

        // ok - the two serializers
        // injection on object creation
        // ok - interfaces in and out
        // location serializer/deserializer

        VisibilityChecker checker;
        jacksonLocal = new ObjectMapper();
        jacksonServer = new ObjectMapper();

        // Will serialize ALL members not annotated with @JsonIgnore
        checker = jacksonLocal.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        jacksonLocal.setVisibilityChecker(checker);

        // Will serialize ONLY members annotated with @JsonProperty
        checker = jacksonServer.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        jacksonServer.setVisibilityChecker(checker);


        // Register all our type adapters
        gsonBuilder.registerTypeAdapter(IAnswer.class, answerDeserializer);
        gsonBuilder.registerTypeAdapter(IQuestionDescriptionDetails.class,
                questionDescriptionDetailsDeserializer);
        gsonBuilder.registerTypeAdapter(QuestionDescription.class,
                questionDescriptionSerializer);


        gsonBuilder.registerTypeAdapter(SequenceDescription.class,
                sequenceDescriptionInstanceCreator);
        gsonBuilder.registerTypeAdapter(PageGroupDescription.class,
                pageGroupDescriptionInstanceCreator);
        gsonBuilder.registerTypeAdapter(PageDescription.class,
                pageDescriptionInstanceCreator);
        gsonBuilder.registerTypeAdapter(QuestionDescription.class,
                questionDescriptionInstanceCreator);
        gsonBuilder.registerTypeAdapter(QuestionPositionDescription.class,
                questionPositionDescriptionInstanceCreator);
        gsonBuilder.registerTypeAdapter(Sequence.class, sequenceInstanceCreator);
        gsonBuilder.registerTypeAdapter(PageGroup.class, pageGroupInstanceCreator);
        gsonBuilder.registerTypeAdapter(Page.class, pageInstanceCreator);
        gsonBuilder.registerTypeAdapter(Question.class, questionInstanceCreator);
        gsonBuilder.registerTypeAdapter(StarRatingAnswer.class, starRatingAnswerInstanceCreator);
        gsonBuilder.registerTypeAdapter(SliderAnswer.class, sliderAnswerInstanceCreator);
        gsonBuilder.registerTypeAdapter(MultipleChoiceAnswer.class,
                multipleChoiceAnswerInstanceCreator);
        gsonBuilder.registerTypeAdapter(LocationPoint.class, locationPointInstanceCreator);


        gsonBuilder.registerTypeAdapter(Location.class,
                locationDeserializer);
        gsonBuilder.registerTypeAdapter(Location.class, locationSerializer);
        gsonBuilder.registerTypeAdapter(JWSSignature.class,
                jwsSignatureSerializer);

        // Build the two Gson instances
        gson = gsonBuilder.create();
        gsonExposed = gsonBuilder.excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    public String toJsonLocal(Object src) {
        Logger.v(TAG, "Serializing to JSON with local visibility");
        try {
            return jacksonLocal.writer().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Logger.e(TAG, "Could not serialize to JSON");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String toJsonServer(Object src) {
        Logger.v(TAG, "Serializing to JSON with server visibility");
        try {
            return jacksonServer.writer().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Logger.e(TAG, "Could not serialize to JSON");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        Logger.v(TAG, "Deserializing from JSON");
        try {
            return jacksonLocal.readValue(json, classOfT);
        } catch (IOException e) {
            Logger.e(TAG, "Could not deserialize JSON");
            Logger.e(TAG, json.replace("{", "'{'").replace("}", "'}'"));
            e.printStackTrace();
            return null;
        }
    }

}
