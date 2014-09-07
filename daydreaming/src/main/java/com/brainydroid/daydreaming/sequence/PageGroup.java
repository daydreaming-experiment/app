package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PageGroup implements IPageGroup {

    private static String TAG = "PageGroup";

    @JsonProperty private String name = null;
    private String friendlyName = null;
    @JsonProperty private boolean bonus = false;
    @JsonProperty private ArrayList<Page> pages = null;

    @JsonIgnore private boolean isLastOfSequence = false;

    public synchronized void importFromPageGroupDescription(PageGroupDescription description) {
        setName(description.getName());
        setFriendlyName(description.getFriendlyName());
        setBonus(description.getPosition().isBonus());
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

    public synchronized boolean isBonus() {
        return bonus;
    }

    private synchronized void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public synchronized void setPages(ArrayList<Page> pages) {
        Logger.v(TAG, "Setting pages");
        this.pages = pages;
    }

    public synchronized ArrayList<Page> getPages() {
        return pages;
    }

    public synchronized boolean isLastOfSequence() {
        return isLastOfSequence;
    }

    public synchronized void setIsLastOfSequence() {
        isLastOfSequence = true;
    }
}
