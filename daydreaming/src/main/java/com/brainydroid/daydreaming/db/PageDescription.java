package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPage;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.PageBuilder;
import com.brainydroid.daydreaming.sequence.Position;
import com.brainydroid.daydreaming.sequence.Question;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageDescription extends DescriptionArrayContainer<QuestionPositionDescription,Question>
        implements BuildableOrderable<PageDescription,Page>, IPage {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageDescription";

    private String name = null;
    private Position position = null;
    private int nSlots = -1;
    private ArrayList<QuestionPositionDescription> questions = null;
    @Inject @JacksonInject @JsonIgnore private PageBuilder pageBuilder;

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<QuestionPositionDescription> getQuestions() {
        return questions;
    }

    public ArrayList<QuestionPositionDescription> getContainedArray() {
        return getQuestions();
    }

    public void validateInitialization(ArrayList<PageDescription> parentArray,
                                       ArrayList<QuestionDescription> questionDescriptions) {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in page can't be null");
        }

        // Check position
        if (position == null) {
            throw new JsonParametersException("position in page can't be null");
        }
        position.validateInitialization(parentArray, this, PageDescription.class);

        validateContained(questionDescriptions);
    }

    public Page build(Sequence sequence) {
        return pageBuilder.build(this, sequence);
    }

}
