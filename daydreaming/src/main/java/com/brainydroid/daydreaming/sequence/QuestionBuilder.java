package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.google.inject.Singleton;

@Singleton
public class QuestionBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionBuilder";

    public Question build(QuestionDescription questionDescription, Sequence sequence) {
        Logger.v(TAG, "Building question from description {}", questionDescription.getName());
        return new Question(questionDescription.getName(), questionDescription.getDetails(),
                sequence);
    }
}
