package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.QuestionPositionDescription;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageBuilder";

    @Inject private PageFactory pageFactory;
    @Inject private QuestionBuilder questionBuilder;
    @Inject private Orderer<QuestionPositionDescription,Question> orderer;

    public Page build(PageDescription pageDescription, Sequence sequence) {
        Logger.v(TAG, "Building page from description {}", pageDescription.getName());

        ArrayList<QuestionPositionDescription> questionPositionDescriptions = pageDescription.getQuestions();
        BuildableOrder<QuestionPositionDescription,Question> buildableOrder =
                orderer.buildOrder(pageDescription.getNSlots(), questionPositionDescriptions);

        Page page = pageFactory.create();
        page.importFromPageDescription(pageDescription);
        page.setQuestions(buildableOrder.build(sequence));
        page.setSequence(sequence);
        return page;
    }

}
