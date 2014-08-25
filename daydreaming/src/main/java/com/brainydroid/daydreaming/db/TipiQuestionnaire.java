package com.brainydroid.daydreaming.db;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by vincenta on 03/07/14.
 */
public class TipiQuestionnaire {
    // Naive implementation that closely matches the grammar definition (at least for extraction from parameter JSON)
    private String text = null;
    private ArrayList<String> hintsForAllSubQuestions = null;
    private ArrayList<TipiQuestion> subQuestions = null;


    public synchronized String getText(){return text;}
    public synchronized ArrayList<String> getHintsForAllSubQuestions(){return hintsForAllSubQuestions;}
    public synchronized ArrayList<TipiQuestion> getSubQuestions(){return subQuestions;}

    public synchronized void setText(String text){
        this.text = text;
    }
    public synchronized void setHintsForAllSubQuestions(ArrayList<String> hintsForAllSubQuestions){
        this.hintsForAllSubQuestions = hintsForAllSubQuestions;
    }
    public synchronized void setSubQuestions(ArrayList<TipiQuestion> subQuestions){
        this.subQuestions = subQuestions;
    }
}
