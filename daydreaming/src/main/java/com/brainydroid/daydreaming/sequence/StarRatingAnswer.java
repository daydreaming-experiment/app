package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.util.HashMap;

public class StarRatingAnswer implements IAnswer {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StarRatingAnswer";

    @Inject @JacksonInject @JsonProperty HashMap<String, Float> starRatings;

    public synchronized void addAnswer(String text, float rating) {
        Logger.v(TAG, "Adding answer {0} at position {1}", text, rating);
        starRatings.put(text, rating);
    }

}
