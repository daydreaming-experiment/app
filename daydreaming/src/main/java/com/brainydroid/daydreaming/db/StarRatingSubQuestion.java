package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class StarRatingSubQuestion {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "StarRatingSubQuestion";

    public static int DEFAULT_INITIAL_RATING = -1;

    private String text = null;
    private String glossaryText = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int numStars = -1;
    @SuppressWarnings("FieldCanBeLocal")
    private float stepSize = -1f;
    @SuppressWarnings("FieldCanBeLocal")
    private float initialRating = DEFAULT_INITIAL_RATING;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean showLiveIndication = false;

    public synchronized String getGlossaryText() {
        return glossaryText;
    }

    public synchronized String getText() {
        return text;
    }

    public synchronized ArrayList<String> getHints() {
        return hints;
    }

    public synchronized int getNumStars() {
        return numStars;
    }

    public synchronized float getStepSize() {
        return stepSize;
    }

    public synchronized float getInitialRating() {
        return initialRating;
    }

    public synchronized boolean getNotApplyAllowed() {
        return notApplyAllowed;
    }

    public synchronized boolean getShowLiveIndication() {
        return showLiveIndication;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating subQuestion");

        if (text == null) {
            throw new JsonParametersException("text can't be null in StarRatingSubQuestion");
        }
        if (numStars < 2) {
            throw new JsonParametersException("There must be at least two possible stars in "
                    + "StarRatingSubQuestion");
        }
        if (stepSize <= 0) {
            throw new JsonParametersException("stepSize must be strictly positive in "
                    + "StarRatingSubQuestion");
        }
        if (initialRating < 0 || initialRating > numStars) {
            throw new JsonParametersException("initialRating must be between 0 and numStars in "
                    + "StarRatingSubQuestion");
        }
    }

}
