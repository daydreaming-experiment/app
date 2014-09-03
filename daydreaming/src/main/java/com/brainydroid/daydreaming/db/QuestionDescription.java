package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.IQuestion;
import com.brainydroid.daydreaming.sequence.QuestionBuilder;
import com.google.inject.Inject;

public class QuestionDescription implements IQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescription";

    private String name = null;
    private IQuestionDescriptionDetails details = null;
    @Inject
    private transient QuestionBuilder questionBuilder;

    public String getName() {
        return name;
    }

    public IQuestionDescriptionDetails getDetails() {
        return details;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating question");

        // Check root parameters
        if (name == null) {
            throw new JsonParametersException("name in question can't be null");
        }
        if (details == null) {
            throw new JsonParametersException("details in question can't be null");
        }

        // Check the details
        details.validateInitialization();
    }

}
