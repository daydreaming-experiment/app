package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPageGroup;
import com.brainydroid.daydreaming.sequence.PageGroup;
import com.brainydroid.daydreaming.sequence.PageGroupBuilder;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

public class PageGroupDescription extends BuildableOrderable<PageGroup> implements IPageGroup {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupDescription";

    private String name = null;
    private String friendlyName = null;
    private String position = null;
    private int nSlots = -1;
    private ArrayList<PageDescription> pages = new ArrayList<PageDescription>();
    @Inject private transient PageGroupBuilder pageGroupBuilder;

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageDescription> getPages() {
        return pages;
    }

    public void validateInitialization() {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in pageGroup can't be null");
        }

        // Check friendlyName
        if (friendlyName == null) {
            throw new JsonParametersException("friendlyName in pageGroup can't be null");
        }

        // Check position
        if (position == null) {
            throw new JsonParametersException("position in pageGroup can't be null");
        }

        // Check nSlots
        if (nSlots == -1) {
            throw new JsonParametersException("nSlots in pageGroup can't be it's default value");
        }
        HashSet<String> positions = new HashSet<String>();
        HashSet<Integer> explicitPositions = new HashSet<Integer>();
        for (PageDescription p : pages) {
            positions.add(p.getPosition());
            if (p.isPositionExplicit()) {
                explicitPositions.add(p.getExplicitPosition());
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

        // Check pages
        if (pages == null || pages.size() == 0) {
            throw new JsonParametersException("pages can't be empty");
        }
        for (PageDescription p : pages) {
            p.validateInitialization();
        }
    }

    @Override
    public PageGroup build(Sequence sequence) {
        return pageGroupBuilder.build(this, sequence);
    }

}
