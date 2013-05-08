package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class Json {

    private static String TAG = "Json";

    private Gson gson;
    private Gson gsonExposed;

    @Inject
    public Json(GsonBuilder gsonBuilder,
                AnswerDeserializer answerDeserializer,
                QuestionDetailsDeserializer questionDetailsDeserializer,
                QuestionInstanceCreator questionInstanceCreator,
                LocationDeserializer locationDeserializer,
                LocationSerializer locationSerializer) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] Json");
        }

        gsonBuilder.registerTypeAdapter(IAnswer.class, answerDeserializer);
        gsonBuilder.registerTypeAdapter(IQuestionDetails.class,
                questionDetailsDeserializer);
        gsonBuilder.registerTypeAdapter(Question.class,
                questionInstanceCreator);
        gsonBuilder.registerTypeAdapter(Location.class,
                locationDeserializer);
        gsonBuilder.registerTypeAdapter(Location.class, locationSerializer);

        gson = gsonBuilder.create();
        gsonExposed = gsonBuilder.excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    public String toJson(Object src) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] toJson");
        }

        return gson.toJson(src);
    }

    public String toJsonExposed(Object src) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] toJsonExposed");
        }

        return gsonExposed.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] fromJson");
        }

        return gson.fromJson(json, classOfT);
    }

}
