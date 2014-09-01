package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.AbstractSequence;

import java.util.ArrayList;

public class SequenceDescription extends AbstractSequence {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SequenceDescription";

    private String name = null;
    private ArrayList<PageGroupDescription> pageGroups = new ArrayList<PageGroupDescription>();

    public String getName() {
        return name;
    }

    public ArrayList<PageGroupDescription> getPageGroups() {
        return pageGroups;
    }

}
