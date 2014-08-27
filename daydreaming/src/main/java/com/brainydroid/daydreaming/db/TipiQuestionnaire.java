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

/**
 *
 * A question object contains the following mandatory properties:

 name: a string defining a name for the question (used to identify questions when retrieving the uploaded results; it's recommended that question names be unique across questions).
 category: a string representing the question's category.
 subCategory: a string representing the question's sub-category (both are used for classification purposes when analysing results).
 slot: a string representing both the question's group and group position (see below the explanation about question groups and slots).
 details: a JSON object containing the details of the question, as detailed in the following rule.


 A details object contains the following mandatory properties:

 type: a string representing the type of question asked; can be either "MultipleChoice", "Slider", or "StarRating" (star-ratings appear like sliders in the app, but behave in a discreet manner instead of continuous).
 If type is "MultipleChoice", the following properties are mandatory:
 text: a string containing the actual question asked to the user.
 choices: a list of strings, each one being a choice proposed to the user (the order is conserved).
 If type is either "Slider" or "StarRating", the following property is mandatory:
 subQuestions: a list of sub-question objects, as detailed in the following rule.



 **/