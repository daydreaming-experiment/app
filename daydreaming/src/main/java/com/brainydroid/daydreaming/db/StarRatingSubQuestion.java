package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class StarRatingSubQuestion {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "StarRatingSubQuestion";

    private String text = null;
    private String glossaryText = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int numStars = -1;
    @SuppressWarnings("FieldCanBeLocal")
    private float stepSize = -1f;
    @SuppressWarnings("FieldCanBeLocal")
    private float initialRating = -1;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean showHints = false;

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

    public synchronized boolean getShowHints() {
        return showHints;
    }

}
