package com.brainydroid.daydreaming.db;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 25/08/14.
 */
public class TipiQuestionnaireDeserializer
        implements JsonDeserializer<TipiQuestionnaire> {

    private static String TAG = "TipiQuestionnaireDeserializer";

    public TipiQuestionnaire deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {


        final TipiQuestionnaire tipiQuestionnaire = new TipiQuestionnaire();
        final JsonObject jsonObject = json.getAsJsonObject();
        // literal extraction (objects directly map that described in parameter JSON of grammar-v2.1)
        final String text = jsonObject.get("text").getAsString();

        // extracting arraylist types from json
        final JsonObject hintsForAllSubQuestions_json = jsonObject.get("hintsForAllSubQuestions").getAsJsonObject();
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        final ArrayList<String> hintsForAllSubQuestions = new Gson().fromJson(hintsForAllSubQuestions_json, listType);

        final JsonObject subQuestions_json = jsonObject.get("subQuestions").getAsJsonObject();
        Type listType2 = new TypeToken<ArrayList<TipiQuestion>>() {}.getType();
        final ArrayList<TipiQuestion> subQuestions = new Gson().fromJson(subQuestions_json, listType2);

        tipiQuestionnaire.setHintsForAllSubQuestions(hintsForAllSubQuestions);
        tipiQuestionnaire.setSubQuestions(subQuestions);
        tipiQuestionnaire.setText(text);

        return tipiQuestionnaire;
    }


}


