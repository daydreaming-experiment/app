package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.brainydroid.daydreaming.db.QuestionPositionDescription;
import com.google.inject.Inject;

public class QuestionBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionBuilder";

    @Inject QuestionFactory questionFactory;
    @Inject ParametersStorage parametersStorage;

    public Question build(QuestionPositionDescription questionPositionDescription,
                          Sequence sequence) {
        Logger.v(TAG, "Building question from description {0} (referencing questionName {1}",
                questionPositionDescription.getName(),
                questionPositionDescription.getQuestionName());
        QuestionDescription questionDescription = parametersStorage.getQuestionDescription(
                questionPositionDescription.getQuestionName());

        Question question = questionFactory.create();
        question.importFromQuestionDescription(questionDescription);
        question.setSequence(sequence);
        return question;
    }
}
