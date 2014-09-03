package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.QuestionPositionDescription;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class PageBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageBuilder";

    @Inject private QuestionBuilder questionBuilder;

    public Page build(PageDescription pageDescription, Sequence sequence) {
        Logger.v(TAG, "Building page from description {}", pageDescription.getName());

        Orderer<QuestionPositionDescription,Question> orderer =
                new Orderer<QuestionPositionDescription, Question>(pageDescription.getNSlots());
        ArrayList<QuestionPositionDescription> questionPositionDescriptions = pageDescription.getQuestions();
        BuildableOrder<QuestionPositionDescription,Question> buildableOrder =
                orderer.buildOrder(questionPositionDescriptions);

        return new Page(buildableOrder.build(sequence), sequence);
    }

}
