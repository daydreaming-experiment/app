package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class SliderSubQuestion {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SliderSubQuestion";

    public static final int DEFAULT_INITIAL_POSITION = -1;

    @JsonView(Views.Internal.class)
    private String text = null;
    @JsonView(Views.Internal.class)
    private ArrayList<String> hints = null;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private int initialPosition = DEFAULT_INITIAL_POSITION;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private boolean notApplyAllowed = false;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private boolean showLiveIndication = false;
    @SuppressWarnings("FieldCanBeLocal")
    @JsonView(Views.Internal.class)
    private boolean alreadyValid = false;

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

    public boolean getAlreadyValid() {
        return alreadyValid;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating subQuestion");

        if (text == null) {
            throw new JsonParametersException("text can't be null in SliderSubQuestion");
        }
        if (initialPosition != DEFAULT_INITIAL_POSITION &&
                (initialPosition < 0 || initialPosition > 100)) {
            throw new JsonParametersException("initialPosition must be between 0 and 100 in "
                    + "SliderSubQuestion");
        }
    }

}
