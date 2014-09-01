package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class PageGroup implements IPageGroup {

    private static String TAG = "PageGroup";

    private String friendlyName;
    private ArrayList<Page> pages;

    public PageGroup(ArrayList<Page> pages) {
        Logger.v(TAG, "Creating pageGroup from list of pages");
        this.pages = pages;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }
}
