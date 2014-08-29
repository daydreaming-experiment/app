package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class StarRatingQuestionDetails implements IQuestionDetails {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "StarRatingQuestionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "StarRating";
    @SuppressWarnings("UnusedDeclaration")
    private ArrayList<StarRatingSubQuestion> subQuestions =
            new ArrayList<StarRatingSubQuestion>();

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized ArrayList<StarRatingSubQuestion> getSubQuestions() {
        return subQuestions;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (subQuestions.size() == 0) {
            throw new JsonParametersException("subQuestions in StarRatingQuestionDetails must "
                    + "have at least one subQuestion");
        }

        for (StarRatingSubQuestion q : subQuestions) {
            q.validateInitialization();
        }
    }

}
