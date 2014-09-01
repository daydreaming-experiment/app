package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.sequence.AbstractPageGroup;

import java.util.ArrayList;

public class PageGroupDescription extends AbstractPageGroup {

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

}
