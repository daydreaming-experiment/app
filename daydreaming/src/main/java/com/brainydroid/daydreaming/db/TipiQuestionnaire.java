package com.brainydroid.daydreaming.db;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by vincenta on 03/07/14.
 */
public class TipiQuestionnaire {
    private String text = null;
    private ArrayList<String> hintsForAllSubQuestions = null;
    private ArrayList<JsonObject> subQuestions = null;
    public synchronized String getText(){return text;}
    public synchronized ArrayList<String> getHintsForAllSubQuestions(){return hintsForAllSubQuestions;}
    public synchronized ArrayList<JsonObject> getSubQuestions(){return subQuestions;}

    public synchronized void setText(String text){
        this.text = text;
    }
    public synchronized void setHintsForAllSubQuestions(ArrayList<String> hintsForAllSubQuestions){
        this.hintsForAllSubQuestions = hintsForAllSubQuestions;
    }
    public synchronized void setSubQuestions(ArrayList<JsonObject> subQuestions){
        this.subQuestions = subQuestions;
    }
}
