package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class ManySlidersQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    private static String TAG = "ManySlidersQuestionDescriptionDetails";

    @JsonView(Views.Internal.class)
    private String type = "ManySliders";
    @JsonView(Views.Internal.class)
    private String text = null;
    @JsonView(Views.Internal.class)
    private ArrayList<String> sliderTexts = null;
    @JsonView(Views.Internal.class)
    private ArrayList<String> hints = null;

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized String getText() {
        return text;
    }

    public synchronized ArrayList<String> getSliderTexts() {
        return sliderTexts;
    }

    public synchronized ArrayList<String> getHints() {
        return hints;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (text == null) {
            throw new JsonParametersException("text in " +
                    "ManySlidersQuestionDescriptionDetails can't be null");
        }

        if (sliderTexts == null || sliderTexts.size() == 0) {
            throw new JsonParametersException("sliderTexts in " +
                    "ManySlidersQuestionDescriptionDetails can't by null or empty");
        }

        if (hints == null || hints.size() < 2) {
            throw new JsonParametersException("There must be at least two hints in " +
                    "ManySlidersQuestionDescriptionDetails");
        }
    }
}
