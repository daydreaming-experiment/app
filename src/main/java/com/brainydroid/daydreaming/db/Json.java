package com.brainydroid.daydreaming.db;

import com.google.gson.Gson;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class Json {

    @Inject Gson gson;

    // TODO: add another set of methods only taking @exposed members

    public Json() {
        // Do our registering here
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
