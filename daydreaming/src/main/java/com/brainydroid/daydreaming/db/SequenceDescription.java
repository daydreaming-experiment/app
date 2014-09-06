package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.ISequence;

import java.util.ArrayList;
import java.util.HashSet;

public class SequenceDescription implements ISequence {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceDescription";

    private String name = null;
    private String type = null;
    private int nSlots = -1;
    private ArrayList<PageGroupDescription> pageGroups = null;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageGroupDescription> getPageGroups() {
        return pageGroups;
    }

    public void validateInitialization(ArrayList<QuestionDescription> questions) {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in sequence can't be null");
        }

        // Check type
        if (type == null) {
            throw new JsonParametersException("type in sequence can't be null");
        }

        // Check nSlots
        if (nSlots == -1) {
            throw new JsonParametersException("nSlots in sequence can't be it's default value");
        }

        // Check pageGroups
        if (pageGroups == null) {
            throw new JsonParametersException("pageGroups in sequence can't be null");
        }

        // Check slot consistency
        HashSet<String> positions = new HashSet<String>();
        HashSet<Integer> explicitPositions = new HashSet<Integer>();
        for (PageGroupDescription pg : pageGroups) {
            positions.add(pg.getPosition());
            if (pg.isPositionExplicit()) {
                explicitPositions.add(pg.getExplicitPosition());
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

        // Check pageGroups
        if (pageGroups == null || pageGroups.size() == 0) {
            throw new JsonParametersException("pageGroups can't be empty");
        }
        for (PageGroupDescription pg : pageGroups) {
            pg.validateInitialization(pageGroups, questions);
        }
    }

}
