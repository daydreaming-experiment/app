package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class QuestionDescriptionsArray extends ArrayList<QuestionDescription> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "QuestionDescriptionsArray";

    public void validateInitialization() {
        Logger.v(TAG, "Validating initialization");

        if (size() == 0) {
            throw new JsonParametersException("QuestionDescriptionsArray can't be empty");
        }
        for (QuestionDescription q : this) {
            q.validateInitialization();
        }
    }
}
