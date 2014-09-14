package com.brainydroid.daydreaming.db;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import org.json.JSONException;

import java.io.IOException;

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
 */
@Singleton
public class Json {

    private static String TAG = "Json";

    private ObjectWriter writerInternal;
    private ObjectWriter writerPublic;
    private ObjectMapper mapper;

    /**
     * Constructor used with dependency injection.
     */
    @Inject
    public Json(ObjectMapper mapper, final Injector injector) {
        Logger.v(TAG, "Building Jackson reader/writer instances");

        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier()
        {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config, BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {
                if (deserializer instanceof AbstractDeserializer) {
                    return deserializer;
                }
                InjectingDeserializer<Object> injectingDeserializer =
                        new InjectingDeserializer<Object>(deserializer);
                injector.injectMembers(injectingDeserializer);
                return injectingDeserializer;
            }
        });
        mapper.registerModule(module);

        VisibilityChecker checker = mapper.getVisibilityChecker()
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE);

        mapper.setVisibilityChecker(checker);
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        writerInternal = mapper.writerWithView(Views.Internal.class);
        writerPublic = mapper.writerWithView(Views.Public.class);
        this.mapper = mapper;

    }

    public String toJsonInternal(Object src) {
        Logger.v(TAG, "Serializing to JSON with internal view");
        try {
            return writerInternal.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Logger.e(TAG, "Could not serialize to JSON");
            e.printStackTrace();
            // TODO: throw real exception here
            throw new RuntimeException(e);
        }
    }

    public String toJsonPublic(Object src) {
        Logger.v(TAG, "Serializing to JSON with public view");
        try {
            return writerPublic.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            Logger.e(TAG, "Could not serialize to JSON");
            e.printStackTrace();
            // TODO: throw real exception here
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JSONException {
        Logger.v(TAG, "Deserializing from JSON");
        try {
            return mapper.readValue(json, classOfT);
        } catch (IOException e) {
            Logger.e(TAG, "Could not deserialize JSON");
            Logger.e(TAG, json.replace("{", "'{'").replace("}", "'}'"));
            e.printStackTrace();
            throw new JSONException(e.getMessage());
        }
    }

    public <T> T fromJson(String json, TypeReference<T> typeRefOfT) throws JSONException {
        Logger.v(TAG, "Deserializing from JSON");
        try {
            return mapper.readValue(json, typeRefOfT);
        } catch (IOException e) {
            Logger.e(TAG, "Could not deserialize JSON");
            Logger.e(TAG, json.replace("{", "'{'").replace("}", "'}'"));
            e.printStackTrace();
            throw new JSONException(e.getMessage());
        }
    }
}
