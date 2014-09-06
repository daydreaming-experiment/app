package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IQuestion;
import com.brainydroid.daydreaming.sequence.Position;
import com.brainydroid.daydreaming.sequence.Question;
import com.brainydroid.daydreaming.sequence.QuestionBuilder;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import java.util.ArrayList;

public class QuestionPositionDescription extends BuildableOrderable<Question>
        implements IQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionPositionDescription";

    private String name = null;
    private Position position = null;
    @Inject @JacksonInject @JsonIgnore private QuestionBuilder questionBuilder;

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public synchronized void validateInitialization(
            ArrayList<QuestionPositionDescription> parentArray,
            ArrayList<QuestionDescription> questionDescriptions)
            throws JsonParametersException {
        Logger.v(TAG, "Validating question");

        // Check root parameters
        if (name == null) {
            throw new JsonParametersException("name in question can't be null");
        }
        // TODO: check the name exists in the questionDescriptions
        if (position == null) {
            throw new JsonParametersException("position in question can't be null");
        }
        position.validateInitialization(parentArray, this, QuestionPositionDescription.class);
    }

    @Override
    public Question build(Sequence sequence) {
        return questionBuilder.build(this, sequence);
    }

}
