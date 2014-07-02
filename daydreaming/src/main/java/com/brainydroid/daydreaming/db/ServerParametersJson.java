package com.brainydroid.daydreaming.db;

import java.lang.String;
import java.util.ArrayList;

public class ServerParametersJson {

    @SuppressWarnings("UnusedDeclaration")
    private static String TAG = "ServerParametersJson";

    public String version = "not given";
    public int nSlotsPerProbe = -1;
    public int schedulingMeanDelay = -1;
    public int schedulingMinDelay = -1;
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
