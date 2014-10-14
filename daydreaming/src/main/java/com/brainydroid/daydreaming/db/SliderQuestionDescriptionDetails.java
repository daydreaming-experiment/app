package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.PreLoadCallback;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class SliderQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderQuestionDescriptionDetails";

    public static String TYPE = "Slider";

    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private String type = TYPE;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Internal.class)
    private ArrayList<SliderSubQuestion> subQuestions = null;

    @Override
    public synchronized boolean isPreLoaded() {
        return true;
    }

    @Override
    public synchronized void onPreLoaded(PreLoadCallback preLoadCallback) {
        // This question is always already loaded
        if (preLoadCallback != null) {
            preLoadCallback.onPreLoaded();
        }
    }

    @Override
    public synchronized Object getPreLoadedObject() {
        return null;
    }

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
