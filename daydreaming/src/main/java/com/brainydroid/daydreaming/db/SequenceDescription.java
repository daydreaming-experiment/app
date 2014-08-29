package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class SequenceDescription {

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
