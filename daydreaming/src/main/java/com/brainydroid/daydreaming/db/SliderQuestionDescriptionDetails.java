package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class SliderQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderQuestionDescriptionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = "Slider";
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<SliderSubQuestion> subQuestions = null;

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized ArrayList<SliderSubQuestion> getSubQuestions() {
        return subQuestions;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (subQuestions == null) {
            throw new JsonParametersException("subQuestions in " +
                    "SliderQuestionDescriptionDetails can't be null");
        }

        if (subQuestions.size() == 0) {
            throw new JsonParametersException("subQuestions in " +
                    "SliderQuestionDescriptionDetails must have at least one subQuestion");
        }

        for (SliderSubQuestion q : subQuestions) {
            q.validateInitialization();
        }
    }

}
