package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class StarRatingQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingQuestionDescriptionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = "StarRating";
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<StarRatingSubQuestion> subQuestions = null;

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized ArrayList<StarRatingSubQuestion> getSubQuestions() {
        return subQuestions;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (subQuestions == null) {
            throw new JsonParametersException("subQuestions in " +
                    "StarRatingQuestionDescriptionDetails can't be null");
        }

        if (subQuestions.size() == 0) {
            throw new JsonParametersException("subQuestions in " +
                    "StarRatingQuestionDescriptionDetails must have at least one subQuestion");
        }

        for (StarRatingSubQuestion q : subQuestions) {
            q.validateInitialization();
        }
    }

}
