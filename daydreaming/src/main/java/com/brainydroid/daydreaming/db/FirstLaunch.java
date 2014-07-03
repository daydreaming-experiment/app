package com.brainydroid.daydreaming.db;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vincenta on 03/07/14.
 */
public class FirstLaunch {
    private String welcomeText = null;
    private String descriptionText = null;
    // Sequential deserialization
    //private JsonObject tipiQuestionnaire = null;
    private ArrayList<Question> tipiQuestions = null;

    public synchronized String getWelcomeText() {
        return welcomeText;
    }

    public synchronized String getDescriptionText() {
        return descriptionText;
    }

    //public synchronized JsonObject getTipiQuestionnaire() {
    //    return tipiQuestionnaire;
    //}


    public synchronized void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public synchronized void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    //public synchronized void setTipiQuestionnaire(JsonObject tipiQuestionnaire) {
    //    this.tipiQuestionnaire = tipiQuestionnaire;
    //}

    public synchronized void setTipiQuestions(ArrayList<Question> tipiQuestions) {
        this.tipiQuestions = tipiQuestions;
    }
}
