package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.QuestionPositionDescription;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageBuilder";

    @Inject private QuestionBuilder questionBuilder;
    @Inject private Orderer<QuestionPositionDescription,Question> orderer;

    public Page build(PageDescription pageDescription, Sequence sequence) {
        Logger.v(TAG, "Building page from description {}", pageDescription.getName());

        ArrayList<QuestionPositionDescription> questionPositionDescriptions = pageDescription.getQuestions();
        BuildableOrder<QuestionPositionDescription,Question> buildableOrder =
                orderer.buildOrder(pageDescription.getNSlots(), questionPositionDescriptions);

        return new Page(buildableOrder.build(sequence), sequence);
    }

}
