package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PageGroup implements IPageGroup {

    private static String TAG = "PageGroup";

    @Expose private String name = null;
    private String friendlyName = null;
    @Expose private ArrayList<Page> pages = null;

    public synchronized void importFromPageGroupDescription(PageGroupDescription description) {
        setName(description.getName());
        setFriendlyName(description.getFriendlyName());
    }

    private synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    private synchronized void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

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