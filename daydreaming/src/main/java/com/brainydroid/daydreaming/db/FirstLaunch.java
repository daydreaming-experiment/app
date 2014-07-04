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
    private String tipiIntroText = null;
    // tipi Questionnaire is a single question with multiple subquestions
    private Question tipiQuestions = null;

    public synchronized String getWelcomeText() {
        return welcomeText;
    }

    public synchronized String getDescriptionText() {
        return descriptionText;
    }

    public synchronized void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public synchronized void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public synchronized void setTipiIntroText(String tipiIntroText) {
        this.tipiIntroText = tipiIntroText;
    }

    public synchronized void setTipiQuestions(Question tipiQuestions) {
        this.tipiQuestions = tipiQuestions;
    }

}
