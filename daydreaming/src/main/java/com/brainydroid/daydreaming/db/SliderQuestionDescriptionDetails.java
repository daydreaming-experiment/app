package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.SliderSubQuestion;

import java.util.ArrayList;

public class SliderQuestionDescriptionDetails implements IQuestionDescriptionDetails {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderQuestionDetails";

    @SuppressWarnings("FieldCanBeLocal")
    private String type = "Slider";
    @SuppressWarnings("UnusedDeclaration")
    private ArrayList<SliderSubQuestion> subQuestions =
            new ArrayList<SliderSubQuestion>();

    @Override
    public synchronized String getType() {
        return type;
    }

    public synchronized ArrayList<SliderSubQuestion> getSubQuestions() {
        return subQuestions;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question details");

        if (subQuestions.size() == 0) {
            throw new JsonParametersException("subQuestions in SliderQuestionDetails must "
                    + "have at least one subQuestion");
        }

        for (SliderSubQuestion q : subQuestions) {
            q.validateInitialization();
        }
    }

}
