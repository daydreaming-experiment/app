package com.brainydroid.daydreaming.db;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

    private ObjectMapper jacksonLocal;
    private ObjectMapper jacksonServer;

    /**
     * Constructor used with dependency injection.
     */
    @Inject
    public Json(ObjectMapper jacksonLocal, ObjectMapper jacksonServer) {
        Logger.v(TAG, "Building Jackson instances");

        // ok - the two serializers
        // ok - injection on object creation
        // ok - interfaces in and out
        // ok - location and JWSSignature serializer/deserializer
        // createFromJson factories

        VisibilityChecker checker;
        this.jacksonLocal = jacksonLocal;
        this.jacksonServer = jacksonServer;

        // Will serialize ALL members not annotated with @JsonIgnore
        checker = this.jacksonLocal.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        this.jacksonLocal.setVisibilityChecker(checker);
        this.jacksonLocal.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Will serialize ONLY members annotated with @JsonProperty
        checker = this.jacksonServer.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        this.jacksonServer.setVisibilityChecker(checker);
        this.jacksonServer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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

    public <T> T fromJson(String json, TypeReference<T> typeRefOfT) {
        Logger.v(TAG, "Deserializing from JSON");
        try {
            return jacksonLocal.readValue(json, typeRefOfT);
        } catch (IOException e) {
            Logger.e(TAG, "Could not deserialize JSON");
            Logger.e(TAG, json.replace("{", "'{'").replace("}", "'}'"));
            e.printStackTrace();
            return null;
        }
    }

}
