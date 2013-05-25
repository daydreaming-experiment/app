package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

public class SliderSubQuestion {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SliderSubQuestion";

    private String text = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int initialPosition = -1;

    public String getText() {
        return text;
    }

    public ArrayList<String> getHints() {
        return hints;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

}
