package com.brainydroid.daydreaming.db;

import java.lang.String;
import java.util.ArrayList;

public class ServerParametersJson {

    //default Parameters
    private static String TAG = "ServerParametersJson";
    public static String DEFAULT_PARAMETERS_VERSION = "-1";
    public static int DEFAULT_N_SLOTS_PER_PROBE = -1;
    public static int DEFAULT_SCHEDULING_MEAN_DELAY = -1;
    public static int DEFAULT_SCHEDULING_MIN_DELAY = -1;

    //elements in root of JSON grammar file
    public String version = DEFAULT_PARAMETERS_VERSION;
    public String backendExpId = "not given";
    public String backendDbName = "not given";
    public int expDuration = -1;
    public String backendApiUrl = "";
    public String resultsPageUrl = "";
    public FirstLaunch firstLaunch = null;
    public int nSlotsPerProbe = DEFAULT_N_SLOTS_PER_PROBE;
    public int schedulingMeanDelay = DEFAULT_SCHEDULING_MEAN_DELAY;
    public int schedulingMinDelay = DEFAULT_SCHEDULING_MIN_DELAY;
    ArrayList<Question> questions = new ArrayList<Question>();
    public String welcomeText = "not given";
    public String descriptionText = "not given";

    //methods

    public synchronized ArrayList<Question> getQuestionsArrayList() {
        return questions;
    }
    public synchronized String getVersion() {
        return version;
    }

    public synchronized String getWelcomeText() {
        return welcomeText;
    }

    public synchronized String getDescriptionText() {
        return descriptionText;
    }

    public synchronized int getExpDuration() {
        return expDuration;
    }

    public synchronized String getBackendExpId() {
        return backendExpId;
    }

    public synchronized String getBackendDbName() {
        return backendDbName;
    }

    public synchronized String getBackendApiUrl() {
        return backendApiUrl;
    }

    public synchronized String getResultsPageUrl() {
        return resultsPageUrl;
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

    public synchronized FirstLaunch getFirstLaunch() {
        return firstLaunch;
    }


}
