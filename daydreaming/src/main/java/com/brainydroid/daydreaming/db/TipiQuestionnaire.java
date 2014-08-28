package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class TipiQuestionnaire {

    private static String TAG = "TipiQuestionnaire";

    public static String DEFAULT_TEXT = "n/c";

    private String text = DEFAULT_TEXT;
    private ArrayList<String> hintsForAllSubQuestions = null;
    private ArrayList<TipiQuestion> tipiQuestions = null;

    public synchronized String getText() {
        return text;
    }

    public synchronized void setText(String text) {
        this.text = text;
    }

    // TODO[seb]: check, it mixes SliderSubQuestions which shouldn't be used. Do when implementing tipi layout inflation
//    public synchronized ArrayList<SliderSubQuestion> buildQuestion() {
//        /**
//        * function to return Question object from TipiQuestion Object, that is, add fields
//        */
//        Logger.v(TAG, "Setting subCategory");
//
//        Question question = new Question();
//        question.setName("tipiQuestion");
//        question.setCategory("tipiQuestion");
//        question.setSubCategory("tipiQuestion");
//        //question.setSlot();
//
//        // construction details for the list of tipiQuestions
//        SliderQuestionDetails tipiQuestionDetails = new SliderQuestionDetails();
//
//        // constructing the subQuestion for the details
//        ArrayList<SliderSubQuestion> subQuestions = new ArrayList<SliderSubQuestion>();
//
//        // looping over the tipiQuestions to construct SliderSubQuestion
//        SliderSubQuestion sliderSubQuestion;
//        final ArrayList<TipiQuestion> tipiSubQuestions = this.tipiQuestions;
//        for (TipiQuestion tipiQuestion : tipiSubQuestions) {
//            sliderSubQuestion = new SliderSubQuestion();
//            sliderSubQuestion.setHints(hintsForAllSubQuestions);
//            sliderSubQuestion.setText(tipiQuestion.getText());
//            subQuestions.add(sliderSubQuestion);
//        }
//        return subQuestions;
//    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating tipiQuestionnaire");

        // Check firstLaunch.tipiQuestionnaire.text
        if (text.equals(DEFAULT_TEXT)) {
            throw new JsonParametersException("firstLaunch.tipiQuestionnaire.text can't be its "
                    + "unset value");
        }

        // Check firstLaunch.tipiQuestionnaire.hintsForAllSubQuestions
        if (hintsForAllSubQuestions == null) {
            throw new JsonParametersException(
                    "firstLaunch.tipiQuestionnaire.hintsForAllSubQuestions can't be its "
                            + "unset value"
            );
        }

        // Check tipiQuestions
        if (tipiQuestions.size() == 0) {
            throw new JsonParametersException("tipiQuestionnaire must have at least one question");
        }
        for (TipiQuestion q : tipiQuestions) {
            q.validateInitialization();
        }
    }
}

