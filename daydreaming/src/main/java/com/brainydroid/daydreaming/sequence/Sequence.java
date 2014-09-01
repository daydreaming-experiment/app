package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class Sequence implements ISequence {

    private static String TAG = "Sequence";

    private ArrayList<PageGroup> pageGroups;

    public Sequence(ArrayList<PageGroup> pageGroups) {
        Logger.v(TAG, "Creating sequence from list of pageGroups");
        this.pageGroups = pageGroups;
    }

    public ArrayList<PageGroup> getPageGroups() {
        return pageGroups;
    }

}
