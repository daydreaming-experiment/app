package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.ISequence;
import com.brainydroid.daydreaming.sequence.PageGroup;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class SequenceDescription extends DescriptionArrayContainer<PageGroupDescription,PageGroup>
        implements ISequence {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceDescription";

    @JsonView(Views.Internal.class)
    private String name = null;
    @JsonView(Views.Internal.class)
    private String type = null;
    @JsonView(Views.Internal.class)
    private String intro = null;
    @JsonView(Views.Internal.class)
    private int nSlots = -1;
    @JsonView(Views.Internal.class)
    private ArrayList<PageGroupDescription> pageGroups = null;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIntro() {
        return intro;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageGroupDescription> getPageGroups() {
        return pageGroups;
    }

    protected ArrayList<PageGroupDescription> getContainedArray() {
        return getPageGroups();
    }

    public void validateInitialization(ArrayList<QuestionDescription> questionDescriptions) {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in sequence can't be null");
        }

        // Check type
        if (type == null) {
            throw new JsonParametersException("type in sequence can't be null");
        }

        // Check intro
        if (intro == null) {
            throw new JsonParametersException("intro in sequence can't be null");
        }

        validateContained(questionDescriptions);
    }

}
