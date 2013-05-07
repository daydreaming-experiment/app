package com.brainydroid.daydreaming.db;

import android.graphics.Bitmap;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;

import java.util.ArrayList;

public class SliderSubQuestion {

    private static String TAG = "SliderSubQuestion";

    private String text;
    private ArrayList<String> hints;
    private int initialPosition;

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
