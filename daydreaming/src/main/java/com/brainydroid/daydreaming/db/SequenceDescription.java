package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.ISequence;

import java.util.ArrayList;

public class SequenceDescription implements ISequence {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SequenceDescription";

    private String name = null;
    private int nSlots = -1;
    private ArrayList<PageGroupDescription> pageGroups = new ArrayList<PageGroupDescription>();

    public String getName() {
        return name;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageGroupDescription> getPageGroups() {
        return pageGroups;
    }

}
