package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPage;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.PageBuilder;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

public class PageDescription extends BuildableOrderable<Page> implements IPage {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageDescription";

    private String name = null;
    private String position = null;
    private int nSlots = -1;
    private ArrayList<QuestionPositionDescription> questions = null;
    @Inject private transient PageBuilder pageBuilder;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<QuestionPositionDescription> getQuestions() {
        return questions;
    }

    public void validateInitialization() {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in page can't be null");
        }

        // Check nSlots
        if (nSlots == -1) {
            throw new JsonParametersException("nSlots in page can't be it's default value");
        }

        // Check questions
        if (questions == null) {
            throw new JsonParametersException("questions in page can't be null");
        }

        // Check slot consistency
        HashSet<String> positions = new HashSet<String>();
        HashSet<Integer> explicitPositions = new HashSet<Integer>();
        for (QuestionPositionDescription q : questions) {
            positions.add(q.getPosition());
            if (q.isPositionExplicit()) {
                explicitPositions.add(q.getExplicitPosition());
            }
        }
        if (positions.size() < nSlots) {
            throw new JsonParametersException("Too many slots and too few positions defined "
                    + "(less than there are slots)");
        }
        if (explicitPositions.size() > nSlots) {
            throw new JsonParametersException("Too many explicit positions defined "
                    + "(more than there are slots)");
        }

        // Check position
        if (position == null) {
            throw new JsonParametersException("position in page can't be null");
        }

        // Check questions
        if (questions == null || questions.size() == 0) {
            throw new JsonParametersException("questions can't be empty");
        }
        for (QuestionPositionDescription q : questions) {
            q.validateInitialization();
        }
    }

    public Page build(Sequence sequence) {
        return pageBuilder.build(this, sequence);
    }

}
