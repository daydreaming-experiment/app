package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPageGroup;
import com.brainydroid.daydreaming.sequence.PageGroup;

import java.util.ArrayList;

public class PageGroupDescription extends BuildableOrderable<PageGroup> implements IPageGroup {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "PageGroupDescription";

    private String name = null;
    private String friendlyName = null;
    private String position = null;
    private ArrayList<PageDescription> pages = new ArrayList<PageDescription>();

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getPosition() {
        return position;
    }

    public ArrayList<PageDescription> getPages() {
        return pages;
    }

    @Override
    public PageGroup build() {
        return null;
    }

}
