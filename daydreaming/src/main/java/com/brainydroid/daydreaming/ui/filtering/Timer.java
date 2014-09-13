package com.brainydroid.daydreaming.ui.filtering;

import java.util.Calendar;
import java.util.HashMap;

public class Timer {

    private static String TAG = "Timer";

    private HashMap<String, Long> running = new HashMap<String, Long>();

    public void start(String name) {
        running.put(name, now());
    }

    public long finish(String name) {
        return now() - running.remove(name);
    }

    private long now() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
