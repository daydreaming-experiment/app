package com.brainydroid.daydreaming.db;

import java.lang.String;
import java.util.ArrayList;

public class ServerParametersJson {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ServerParametersJson";

    public static String DEFAULT_PARAMETERS_VERSION = "-1";
    public static int DEFAULT_N_SLOTS_PER_PROBE = -1;
    public static int DEFAULT_SCHEDULING_MEAN_DELAY = -1;
    public static int DEFAULT_SCHEDULING_MIN_DELAY = -1;

    public String version = DEFAULT_PARAMETERS_VERSION;
    public int nSlotsPerProbe = DEFAULT_N_SLOTS_PER_PROBE;
    public int schedulingMeanDelay = DEFAULT_SCHEDULING_MEAN_DELAY;
    public int schedulingMinDelay = DEFAULT_SCHEDULING_MIN_DELAY;
    ArrayList<Question> questions = new ArrayList<Question>();

    public synchronized ArrayList<Question> getQuestionsArrayList() {
        return questions;
    }

    public synchronized String getVersion() {
        return version;
    }

    public synchronized int getNSlotsPerProbe() {
        return nSlotsPerProbe;
    }

    public synchronized int getSchedulingMeanDelay() {
        return schedulingMeanDelay;
    }

    public synchronized int getSchedulingMinDelay() {
        return schedulingMinDelay;
    }

}
