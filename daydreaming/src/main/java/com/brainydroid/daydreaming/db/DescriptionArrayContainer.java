package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.Position;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class DescriptionArrayContainer<D extends BuildableOrderable<D,C>,C> {

    private static String TAG = "DescriptionArrayContainer";

    abstract public int getNSlots();

    abstract protected ArrayList<D> getContainedArray();

    protected void validateContained(ArrayList<QuestionDescription> questionDescriptions) {
        Logger.d(TAG, "Validating contained array and related items");

        int nSlots = getNSlots();
        ArrayList<D> containedArray = getContainedArray();

        // Check nSlots
        if (nSlots == -1) {
            throw new JsonParametersException("nSlots can't be it's default value");
        }

        // Check contained array
        if (containedArray == null || containedArray.size() == 0) {
            throw new JsonParametersException("Contained array can't be empty");
        }

        // Check unicity of names
        HashSet<String> names = new HashSet<String>();
        for (D d : containedArray) {
            if (names.contains(d.getName())) {
                throw new JsonParametersException("Names in the contained array must be unique");
            }
            names.add(d.getName());
        }

        // Check slot consistency
        HashSet<Integer> fixedPositions = new HashSet<Integer>();
        HashSet<String> floatingPositions = new HashSet<String>();
        Position currentPosition;
        for (D d : containedArray) {
            currentPosition = d.getPosition();
            if (currentPosition.isFixed()) {
                fixedPositions.add(currentPosition.getFixedPosition());
            } else if (currentPosition.isFloating()) {
                floatingPositions.add(currentPosition.getFloatingPosition());
            }
        }
        if (fixedPositions.size() + floatingPositions.size() < nSlots) {
            throw new JsonParametersException("Too many slots and too few fixed+floating " +
                    "positions defined (less than there are slots)");
        }
        if (fixedPositions.size() > nSlots) {
            throw new JsonParametersException("Too many fixed positions defined "
                    + "(more than there are slots)");
        }

        // Check contained array items
        for (D d : containedArray) {
            d.validateInitialization(containedArray, questionDescriptions);
        }
    }
}
