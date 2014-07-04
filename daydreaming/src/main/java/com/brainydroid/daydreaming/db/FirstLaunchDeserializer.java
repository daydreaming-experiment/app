package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FirstLaunchDeserializer
        implements JsonDeserializer<FirstLaunch> {

    private static String TAG = "FirstLaunchDeserializer";

    public FirstLaunch deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

        Logger.v(TAG, "Deserializing firstlaunch");

            final FirstLaunch firstLaunch = new FirstLaunch();

            final JsonObject jsonObject = json.getAsJsonObject();

            // first level
            final String welcomeText = jsonObject.get("welcomeText").getAsString();
            final String descriptionText = jsonObject.get("descriptionText").getAsString();
            final String tipiIntroText = jsonObject.get("text").getAsString();
            final Question tipiQuestions = new Question();
            tipiQuestions.setName("TipiQuestion");
            tipiQuestions.setCategory("TipiQuestion");
            tipiQuestions.setSubCategory("TipiQuestion");
            tipiQuestions.setSlot("-1");


            // second level
            final JsonObject details = jsonObject.get("tipiQuestionnaire").getAsJsonObject();
            final JsonArray subQuestions = details.get("subQuestions").getAsJsonArray();
            final JsonArray hints = details.get("hintsForAllSubQuestions").getAsJsonArray();
            for (int i = 0; i < subQuestions.size(); i++) {
                    JsonObject subq = subQuestions.get(i).getAsJsonObject();
                    subq.add("hints",hints);
            }
            tipiQuestions.setDetailsFromJson(details.getAsString());

            // assignment
            firstLaunch.setDescriptionText(descriptionText);
            firstLaunch.setWelcomeText(welcomeText);
            firstLaunch.setTipiIntroText(tipiIntroText);
            firstLaunch.setTipiQuestions(tipiQuestions);

        return firstLaunch;
    }
}
