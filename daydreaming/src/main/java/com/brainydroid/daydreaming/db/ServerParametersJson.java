package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ServerParametersJson {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "ServerParametersJson";

    public static String DEFAULT_PARAMETERS_VERSION = "n/c";
    public static int DEFAULT_N_SLOTS_PER_PROBE = -1;
    public static int DEFAULT_SCHEDULING_MEAN_DELAY = -1;
    public static int DEFAULT_SCHEDULING_MIN_DELAY = -1;
    public static String DEFAULT_BACKEND_EXP_ID = "n/c";
    public static String DEFAULT_BACKEND_DB_NAME = "n/c";
    public static int DEFAULT_EXP_DURATION = -1;
    public static String DEFAULT_BACKEND_API_URL = "n/c";
    public static String DEFAULT_RESULTS_PAGE_URL = "n/c";
    public static String DEFAULT_GLOSSARY = "n/c";
    public static HashMap<String,String> DEFAULT_GLOSSARY_JSON = null;

    public String version = DEFAULT_PARAMETERS_VERSION;
    public String backendExpId = DEFAULT_BACKEND_EXP_ID;
    public String backendDbName = DEFAULT_BACKEND_DB_NAME;
    public int expDuration = DEFAULT_EXP_DURATION;
    public String backendApiUrl = DEFAULT_BACKEND_API_URL;
    public String resultsPageUrl = DEFAULT_RESULTS_PAGE_URL;
    public FirstLaunch firstLaunch = null;
    public int nSlotsPerProbe = DEFAULT_N_SLOTS_PER_PROBE;
    public int schedulingMeanDelay = DEFAULT_SCHEDULING_MEAN_DELAY;
    public int schedulingMinDelay = DEFAULT_SCHEDULING_MIN_DELAY;
    ArrayList<Question> questions = new ArrayList<Question>();
    public HashMap<String,String> glossary = DEFAULT_GLOSSARY_JSON;

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

    public synchronized HashMap<String,String> getGlossary() {
        return glossary;
    }

    public synchronized String getBackendExpId() {
        return backendExpId;
    }

    public synchronized String getBackendDbName() {
        return backendDbName;
    }

    public synchronized int getExpDuration() {
        return expDuration;
    }

    public synchronized String getBackendApiUrl() {
        return backendApiUrl;
    }

    public synchronized String getResultsPageUrl() {
        return resultsPageUrl;
    }

    public synchronized FirstLaunch getFirstLaunch() {
        return firstLaunch;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating parameters");

        // Check version is set
        if (version.equals(DEFAULT_PARAMETERS_VERSION)) {
            throw new JsonParametersException("version can't be its unset value");
        }

        // Check nSlotsPerProbe is set
        if (nSlotsPerProbe == DEFAULT_N_SLOTS_PER_PROBE) {
            throw new JsonParametersException("nSlotsPerProbe can't be its unset value");
        }

        // Check schedulingMinDelay is set
        if (schedulingMinDelay == DEFAULT_SCHEDULING_MIN_DELAY) {
            throw new JsonParametersException("schedulingMinDelay can't be its unset value");
        }

        // Check schedulingMeanDelay is set
        if (schedulingMeanDelay == DEFAULT_SCHEDULING_MEAN_DELAY) {
            throw new JsonParametersException("schedulingMeanDelay can't be its unset value");
        }

        // Get all question slots and check there are at least as many as
        // nSlotsPerProbe
        HashSet<String> slots = new HashSet<String>();
        for (Question q : questions) {
            slots.add(q.getSlot());
            q.validateInitialization();
        }
        if (slots.size() < nSlotsPerProbe) {
            throw new JsonParametersException("There must be at least as many" +
                    " slots defined in the questions as nSlotsPerProbe");
        }

        // Check backendExpId is set
        if (backendExpId.equals(DEFAULT_BACKEND_EXP_ID)) {
            throw new JsonParametersException("backendExpId can't be its unset value");
        }

        // Check backendDbName is set
        if (backendDbName.equals(DEFAULT_BACKEND_DB_NAME)) {
            throw new JsonParametersException("backendDbName can't be its unset value");
        }

        // Check expDuration is set
        if (expDuration == DEFAULT_EXP_DURATION) {
            throw new JsonParametersException("expDuration can't be its unset value");
        }

        // Check expDuration is set
        if (backendApiUrl.equals(DEFAULT_BACKEND_API_URL)) {
            throw new JsonParametersException("backendApiUrl can't be its unset value");
        }

        // Check expDuration is set
        if (resultsPageUrl.equals(DEFAULT_RESULTS_PAGE_URL)) {
            throw new JsonParametersException("resultsPageUrl can't be its unset value");
        }

        if (glossary == DEFAULT_GLOSSARY_JSON) {
            throw new JsonParametersException("glossary can't be its unset value");
        }

        firstLaunch.validateInitialization();
    }

}
