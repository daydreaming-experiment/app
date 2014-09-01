package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.QuestionDescription;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class PageBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageBuilder";

    @Inject private Orderer orderer;
    @Inject private QuestionBuilder questionBuilder;

    public Page build(PageDescription pageDescription) {
        Logger.v(TAG, "Building page from description {}", pageDescription.getName());

        ArrayList<QuestionDescription> questionDescriptions = pageDescription.getQuestions();
        BuildableOrder<QuestionDescription,Question> buildableOrder =
                orderer.buildOrder(questionDescriptions, pageDescription.getNSlots(),
                        Question.class);

        return new Page(buildableOrder.build());
    }

}
