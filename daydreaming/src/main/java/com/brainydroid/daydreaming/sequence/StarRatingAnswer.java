package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.HashMap;

public class StarRatingAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingAnswer";

    @SuppressWarnings("FieldCanBeLocal")
    @Expose private String type = "StarRating";
    @Inject @Expose HashMap<String, Float> starRatings;

    public synchronized String getType() {
        return type;
    }

    public synchronized void addAnswer(String text, float rating) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, rating);
        starRatings.put(text, rating);
    }

}
