package com.brainydroid.daydreaming.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class Json {

    private Gson gson;
    private Gson gsonExposed;

    @Inject
    public Json(GsonBuilder gsonBuilder,
                AnswerDeserializer answerDeserializer,
                QuestionDetailsDeserializer questionDetailsDeserializer,
                QuestionInstanceCreator questionInstanceCreator) {
        gsonBuilder.registerTypeAdapter(IAnswer.class, answerDeserializer);
        gsonBuilder.registerTypeAdapter(IQuestionDetails.class,
                questionDetailsDeserializer);
        gsonBuilder.registerTypeAdapter(Question.class,
                questionInstanceCreator);

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
