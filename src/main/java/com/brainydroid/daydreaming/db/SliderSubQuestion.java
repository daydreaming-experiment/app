package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class SliderSubQuestion {

    private static String TAG = "SliderSubQuestion";

    private String text = null;
    private ArrayList<String> hints = new ArrayList<String>();
    @SuppressWarnings("FieldCanBeLocal")
    private int initialPosition = -1;

    public String getText() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getText");
        }

        return text;
    }

    public ArrayList<String> getHints() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getHints");
        }

        return hints;
    }

    public int getInitialPosition() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getInitialPosition");
        }

        return initialPosition;
    }

}
