package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

public class TipiQuestion {

    private static String TAG = "TipiQuestion";

    public static String DEFAULT_TEXT = "n/c";
    public static int DEFAULT_INITIAL_POSITION = 50;

    private String text = DEFAULT_TEXT;
    private int initialPosition = DEFAULT_INITIAL_POSITION;

    public synchronized String getText(){
        return text;
    }

    public synchronized int getInitialPosition() {
        return initialPosition;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating tipiQuestion");

        if (text.equals(DEFAULT_TEXT)) {
            throw new JsonParametersException("text in tipiQuestion is the default text");
        }
        if (initialPosition < 0 || initialPosition > 100) {
            throw new JsonParametersException("initialPosition in tipiQuestion must be "
                    + "between 0 and 100");
        }
    }

}
