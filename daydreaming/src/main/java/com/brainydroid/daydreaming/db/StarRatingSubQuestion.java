package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class StarRatingSubQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingSubQuestion";

    public static int DEFAULT_INITIAL_RATING = -1;

    @JsonView(Views.Internal.class)
    private String text = null;
    @JsonView(Views.Internal.class)
    private String glossaryText = null;
    @JsonView(Views.Internal.class)
    private ArrayList<String> hints = null;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private int numStars = -1;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private float stepSize = -1f;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private float initialRating = DEFAULT_INITIAL_RATING;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
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
