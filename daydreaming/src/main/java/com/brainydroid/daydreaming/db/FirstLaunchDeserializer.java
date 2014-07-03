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

            final JsonObject jsonObject = json.getAsJsonObject();
            final String welcomeText = jsonObject.get("welcomeText").getAsString();
            final String descriptionText = jsonObject.get("descriptionText").getAsString();
            final JsonObject tipiQuestionnaire = jsonObject.get("tipiQuestionnaire").getAsJsonObject();


            final FirstLaunch firstLaunch = new FirstLaunch();
            firstLaunch.setDescriptionText(descriptionText);
            firstLaunch.setWelcomeText(welcomeText);
            //firstLaunch.setTipiQuestionnaire(tipiQuestionnaire);


            // Extracting tipi questions
            final JsonArray hints = tipiQuestionnaire.get("subQuestions").getAsJsonArray();
            final JsonArray questions = tipiQuestionnaire.get("hintsForAllSubQuestions").getAsJsonArray();
            final ArrayList<Question> tipiQuestions = new ArrayList<Question>();
            for (int i = 0; i < questions.size(); i++) {
                Question q = new Question();
                //individual details
                JsonObject details = questions.get(i).getAsJsonObject();
                //shared hints
                details.add("hints",hints);
                q.setDetailsFromJson(details.getAsString());
                tipiQuestions.add(q);
            }
            firstLaunch.setTipiQuestions(tipiQuestions);
            return firstLaunch;
    }
}
