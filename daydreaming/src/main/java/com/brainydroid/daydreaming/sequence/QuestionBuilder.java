package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionPositionDescription;
import com.google.inject.Singleton;

@Singleton
public class QuestionBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionBuilder";

    public Question build(QuestionPositionDescription questionPositionDescription, Sequence sequence) {
        Logger.v(TAG, "Building question from description {}", questionPositionDescription.getName());
        return new Question(questionPositionDescription.getName(), questionPositionDescription.getDetails(),
                sequence);
    }
}
