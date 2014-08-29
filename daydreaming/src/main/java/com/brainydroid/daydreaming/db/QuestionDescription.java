package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.AbstractQuestion;

public class QuestionDescription extends AbstractQuestion {

    private static String TAG = "QuestionDescription";

    private String name;
    private String position;
    private IQuestionDetails details;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public IQuestionDetails getDetails() {
        return details;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question");

        // Check root parameters
        if (name == null) {
            throw new JsonParametersException("name in question can't be null");
        }
        if (position == null) {
            throw new JsonParametersException("position in question can't be null");
        }
        if (details == null) {
            throw new JsonParametersException("details in question can't be null");
        }

        // Check the details
        details.validateInitialization();
    }
}
