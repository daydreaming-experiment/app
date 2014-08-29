package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.AbstractSequence;

import java.util.ArrayList;

public class SequenceDescription extends AbstractSequence {

    private static String TAG = "SequenceDescription";

    private String name;
    private ArrayList<PageGroupDescription> pageGroups;

    public String getName() {
        return name;
    }

    public ArrayList<PageGroupDescription> getPageGroups() {
        return pageGroups;
    }

}
