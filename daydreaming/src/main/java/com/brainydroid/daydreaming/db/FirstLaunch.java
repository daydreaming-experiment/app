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
    private String tipiIntroText = null; //TODO: see if necessary and add to ParameterStorage
    private TipiQuestionnaire tipiQuestionnaire = null;

    // naive implementation that follows precisely the grammar definition in terms of the object created
    // parameters of the tipi questionnaires are very "compressed" in the grammar definition
    // they will be extracted as such
    // TODO Question objects to be fed to UI, will be constructed from TipiQuestionnaire class method

    public synchronized String getWelcomeText() {
        return welcomeText;
    }

    public synchronized String getDescriptionText() {
        return descriptionText;
    }

    public synchronized TipiQuestionnaire getTipiQuestionnaire() {
        return tipiQuestionnaire;
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



}
