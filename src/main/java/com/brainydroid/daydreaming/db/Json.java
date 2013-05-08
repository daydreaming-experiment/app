package com.brainydroid.daydreaming.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class Json {

    @Inject GsonBuilder gsonBuilder;
    private Gson gson;
    private Gson gsonExposed;

    public Json() {
        gsonBuilder.registerTypeAdapter(IAnswer.class,
                new AnswerDeserializer());
        gsonBuilder.registerTypeAdapter(IQuestionDetails.class,
                new QuestionDetailsDeserializer());

        gson = gsonBuilder.create();
        gsonExposed = gsonBuilder.excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public String toJsonExposed(Object src) {
        return gsonExposed.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
