package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PageGroup implements IPageGroup {

    private static String TAG = "PageGroup";

    private String friendlyName = null;
    @Expose private ArrayList<Page> pages = null;

    public synchronized String getFriendlyName() {
        return friendlyName;
    }

    public synchronized void setPages(ArrayList<Page> pages) {
        Logger.v(TAG, "Setting pages");
        this.pages = pages;
    }

    public synchronized ArrayList<Page> getPages() {
        return pages;
    }
}
