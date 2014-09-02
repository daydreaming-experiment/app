package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IQuestion;
import com.brainydroid.daydreaming.sequence.Question;
import com.brainydroid.daydreaming.sequence.QuestionBuilder;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

public class QuestionDescription extends BuildableOrderable<Question> implements IQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescription";

    private String name = null;
    private String position = null;
    private IQuestionDescriptionDetails details = null;
    @Inject private transient QuestionBuilder questionBuilder;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
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
        if (position == null) {
            throw new JsonParametersException("position in question can't be null");
        }
        if (details == null) {
            throw new JsonParametersException("details in question can't be null");
        }

        // Check the details
        details.validateInitialization();
    }

    @Override
    public Question build(Sequence sequence) {
        return questionBuilder.build(this, sequence);
    }
}
