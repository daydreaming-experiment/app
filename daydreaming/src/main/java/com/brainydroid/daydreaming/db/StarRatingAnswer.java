package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class StarRatingAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "StarRating";

    // Don't inject this or it will override Json-loaded values when
    // deserializing.
    @Expose HashMap<String, Float> starRatings = new HashMap<String, Float>();

    public synchronized String getType() {
        return type;
    }

    public synchronized void addAnswer(String text, float rating) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, rating);
        starRatings.put(text, rating);
    }

}
