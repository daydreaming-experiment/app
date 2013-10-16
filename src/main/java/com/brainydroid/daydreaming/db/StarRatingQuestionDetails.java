package com.brainydroid.daydreaming.db;

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

}
