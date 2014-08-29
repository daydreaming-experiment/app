package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class PageGroupDescription {

    private static String TAG = "PageGroupDescription";

    private String name;
    private String friendlyName;
    private String position;
    private ArrayList<PageDescription> pages;

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
