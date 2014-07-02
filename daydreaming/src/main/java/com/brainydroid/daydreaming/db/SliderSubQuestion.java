package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class SliderSubQuestion {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SliderSubQuestion";

    private String text = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int initialPosition = -1;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean showLiveIndication = false;

    public synchronized String getText() {
        return text;
    }

    public synchronized ArrayList<String> getHints() {
        return hints;
    }

    public synchronized int getInitialPosition() {
        return initialPosition;
    }

    public synchronized boolean getNotApplyAllowed() {
        return notApplyAllowed;
    }

    public synchronized boolean getShowLiveIndication() {
        return showLiveIndication;
    }

}
