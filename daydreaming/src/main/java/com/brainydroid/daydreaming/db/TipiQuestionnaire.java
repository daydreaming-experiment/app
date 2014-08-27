package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by vincenta on 03/07/14.
 */
public class TipiQuestionnaire {
    // Naive implementation that closely matches the grammar definition (at least for extraction from parameter JSON)

    private static String TAG = "TipiQuestionnaire";

    private String text = null;
    private ArrayList<String> hintsForAllSubQuestions = null;
    private ArrayList<TipiQuestion> tipiSubQuestions = null;
    private ArrayList<SliderSubQuestion> subQuestions = null;

    public synchronized String getText(){return text;}
    public synchronized ArrayList<String> getHintsForAllSubQuestions(){return hintsForAllSubQuestions;}
    public synchronized ArrayList<TipiQuestion> getTipiSubQuestions(){return tipiSubQuestions;}

    public synchronized void setText(String text){
        this.text = text;
    }
    public synchronized void setHintsForAllSubQuestions(ArrayList<String> hintsForAllSubQuestions){
        this.hintsForAllSubQuestions = hintsForAllSubQuestions;
    }

    public synchronized void setSubQuestions(ArrayList<TipiQuestion> tipiSubQuestions){
        this.tipiSubQuestions = tipiSubQuestions;
    }

    public synchronized ArrayList<SliderSubQuestion> buildQuestion(){
        /**
        * function to return Question object from TipiQuestion Object, that is, add fields
        */
        Logger.v(TAG, "Setting subCategory");

        Question question = new Question();
        question.setName("tipiQuestion");
        question.setCategory("tipiQuestion");
        question.setSubCategory("tipiQuestion");
        //question.setSlot();

        // construction details for the list of tipiQuestions
        SliderQuestionDetails tipiQuestionDetails = new SliderQuestionDetails();

        // constructing the subQuestion for the details
        ArrayList<SliderSubQuestion> subQuestions = new ArrayList<SliderSubQuestion>();

        // looping over the tipiQuestions to construct SliderSubQuestion
        SliderSubQuestion sliderSubQuestion;
        final ArrayList<TipiQuestion> tipiSubQuestions = this.tipiSubQuestions;
        for (TipiQuestion tipiQuestion : tipiSubQuestions) {
            sliderSubQuestion = new SliderSubQuestion();
            sliderSubQuestion.setHints(hintsForAllSubQuestions);
            sliderSubQuestion.setText(tipiQuestion.getText());
            subQuestions.add(sliderSubQuestion);
        }
        return subQuestions;
    }
}

