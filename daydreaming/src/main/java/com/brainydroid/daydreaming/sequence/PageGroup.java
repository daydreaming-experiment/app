package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PageGroup implements IPageGroup {

    private static String TAG = "PageGroup";

    private String friendlyName = null;
    @Expose private ArrayList<Page> pages = null;

    public PageGroup(ArrayList<Page> pages) {
        Logger.v(TAG, "Creating pageGroup from list of pages");
        this.pages = pages;
    }

    public synchronized String getFriendlyName() {
        return friendlyName;
    }

    public synchronized ArrayList<Page> getPages() {
        return pages;
    }
}
