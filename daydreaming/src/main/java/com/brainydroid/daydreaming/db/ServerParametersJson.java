package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;

import java.util.HashMap;
import java.util.ArrayList;

public class ServerParametersJson {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "ServerParametersJson";

    public static String DEFAULT_PARAMETERS_VERSION = "n/c";
    public static int DEFAULT_SCHEDULING_MEAN_DELAY = -1;
    public static int DEFAULT_SCHEDULING_MIN_DELAY = -1;
    public static String DEFAULT_BACKEND_EXP_ID = "n/c";
    public static String DEFAULT_BACKEND_DB_NAME = "n/c";
    public static int DEFAULT_EXP_DURATION = -1;
    public static String DEFAULT_BACKEND_API_URL = "n/c";
    public static String DEFAULT_RESULTS_PAGE_URL = "n/c";
    public static HashMap<String,String> DEFAULT_GLOSSARY_JSON = null;

    private String version = DEFAULT_PARAMETERS_VERSION;
    private String backendExpId = DEFAULT_BACKEND_EXP_ID;
    private String backendDbName = DEFAULT_BACKEND_DB_NAME;
    private int expDuration = DEFAULT_EXP_DURATION;
    private String backendApiUrl = DEFAULT_BACKEND_API_URL;
    private String resultsPageUrl = DEFAULT_RESULTS_PAGE_URL;
    private int schedulingMeanDelay = DEFAULT_SCHEDULING_MEAN_DELAY;
    private int schedulingMinDelay = DEFAULT_SCHEDULING_MIN_DELAY;
    private ArrayList<QuestionDescription> questions = null;
    private ArrayList<SequenceDescription> sequences = null;
    private HashMap<String,String> glossary = DEFAULT_GLOSSARY_JSON;

    public synchronized String getVersion() {
        return version;
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

    public synchronized int getSchedulingMeanDelay() {
        return schedulingMeanDelay;
    }

    public synchronized int getSchedulingMinDelay() {
        return schedulingMinDelay;
    }

    public synchronized ArrayList<QuestionDescription> getQuestions() {
        return questions;
    }

    public synchronized ArrayList<SequenceDescription> getSequences() {
        return sequences;
    }

    public synchronized void validateInitialization() throws JsonParametersException {
        Logger.v(TAG, "Validating parameters");

        // Check version is set
        if (version.equals(DEFAULT_PARAMETERS_VERSION)) {
            throw new JsonParametersException("version can't be its unset value");
        }

        // Check schedulingMinDelay is set
        if (schedulingMinDelay == DEFAULT_SCHEDULING_MIN_DELAY) {
            throw new JsonParametersException("schedulingMinDelay can't be its unset value");
        }

        // Check schedulingMeanDelay is set
        if (schedulingMeanDelay == DEFAULT_SCHEDULING_MEAN_DELAY) {
            throw new JsonParametersException("schedulingMeanDelay can't be its unset value");
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

        // Validate questions
        if (questions == null || questions.size() == 0) {
            throw new JsonParametersException("questions can't be empty");
        }
        for (QuestionDescription q : questions) {
            q.validateInitialization();
        }

        // Validate sequences
        if (sequences == null || sequences.size() == 0) {
            throw new JsonParametersException("sequences can't be empty");
        }
        for (SequenceDescription s : sequences) {
            s.validateInitialization(questions);
        }
    }

}
