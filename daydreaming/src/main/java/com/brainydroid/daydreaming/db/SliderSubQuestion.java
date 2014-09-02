package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;

public class SliderSubQuestion {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "SliderSubQuestion";

    public static int DEFAULT_INITIAL_POSITION = -1;
    public static int DEFAULT_INITIAL_RATING = -1;
    private String text = null;
    private String glossaryText = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int initialPosition = DEFAULT_INITIAL_POSITION;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean showLiveIndication = false;

    public synchronized String getText() {
        return text;
    }

    public synchronized String getGlossaryText() {
        return glossaryText;
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

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating subQuestion");

        if (text == null) {
            throw new JsonParametersException("text can't be null in SliderSubQuestion");
        }
        if (initialPosition < 0 || initialPosition > 100) {
            throw new JsonParametersException("initialPosition must be between 0 and 100 in "
                    + "SliderSubQuestion");
        }
    }

}
