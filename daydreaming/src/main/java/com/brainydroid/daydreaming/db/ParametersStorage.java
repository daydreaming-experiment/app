package com.brainydroid.daydreaming.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.HttpConversationCallback;
import com.brainydroid.daydreaming.network.HttpGetData;
import com.brainydroid.daydreaming.network.HttpGetTask;
import com.brainydroid.daydreaming.network.ParametersStorageCallback;
import com.brainydroid.daydreaming.network.ServerConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Singleton
public class ParametersStorage {

    private static String TAG = "ParametersStorage";

    public static String QUESTIONS_SCHEDULING_MIN_DELAY = "schedulingMinDelay";
    public static String QUESTIONS_SCHEDULING_MEAN_DELAY = "schedulingMeanDelay";

    public static String BACKEND_EXP_ID = "backendExpId";
    public static String BACKEND_DB_NAME = "backendDbName";
    public static String EXP_DURATION = "expDuration";
    public static String BACKEND_API_URL = "backendApiUrl";
    public static String RESULTS_PAGE_URL = "resultsPageUrl";

    public static String GLOSSARY = "glossary";
    public static String QUESTIONS = "questions";
    public static String SEQUENCES = "sequences";

    private ArrayList<QuestionDescription> questionsCache;
    private ArrayList<SequenceDescription> sequencesCache;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject Json json;
    @Inject ProfileStorage profileStorage;
    @Inject StatusManager statusManager;
    @Inject Context context;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public ParametersStorage(SharedPreferences sharedPreferences, StatusManager statusManager) {
        Logger.d(TAG, "{} - Building ParametersStorage", statusManager.getCurrentModeName());

        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
    }

    private synchronized void setBackendExpId(String backendExpId) {
        Logger.d(TAG, "{} - Setting backendExpId to {}", statusManager.getCurrentModeName(), backendExpId);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + BACKEND_EXP_ID, backendExpId);
        eSharedPreferences.commit();
    }

    public synchronized String getBackendExpId() {
        String backendExpId = sharedPreferences.getString(
                statusManager.getCurrentModeName() + BACKEND_EXP_ID, null);
        if (backendExpId == null) {
            Logger.e(TAG, "{} - backendExpId is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("backendExpId is asked for but not set");
        }
        Logger.d(TAG, "{0} - backendExpId is {1}", statusManager.getCurrentModeName(),
                backendExpId);
        return backendExpId;
    }

    private synchronized void clearBackendExpId() {
        Logger.d(TAG, "{} - Clearing backendExpId", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + BACKEND_EXP_ID);
    }

    private synchronized void setBackendDbName(String backendDbName) {
        Logger.d(TAG, "{} - Setting backendDbName to {}", statusManager.getCurrentModeName(), backendDbName);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + BACKEND_DB_NAME, backendDbName);
        eSharedPreferences.commit();
    }

    public synchronized String getBackendDbName() {
        String backendDbName = sharedPreferences.getString(
                statusManager.getCurrentModeName() + BACKEND_DB_NAME, null);
        if (backendDbName == null) {
            Logger.e(TAG, "{} - backendDbName is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("backendDbName is asked for but not set");
        }
        Logger.d(TAG, "{0} - backendDbName is {1}", statusManager.getCurrentModeName(),
                backendDbName);
        return backendDbName;
    }

    private synchronized void clearBackendDbName() {
        Logger.d(TAG, "{} - Clearing backendDbName", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + BACKEND_DB_NAME);
    }

    private synchronized void setExpDuration(int expDuration) {
        Logger.d(TAG, "{} - Setting expDuration to {}", statusManager.getCurrentModeName(), expDuration);
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + EXP_DURATION, expDuration);
        eSharedPreferences.commit();
    }

    public synchronized int getExpDuration() {
        int expDuration = sharedPreferences.getInt(
                statusManager.getCurrentModeName() + EXP_DURATION, -1);
        if (expDuration == -1) {
            Logger.e(TAG, "{} - expDuration is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("expDuration is asked for but not set");
        }
        Logger.d(TAG, "{0} - expDuration is {1}", statusManager.getCurrentModeName(),
                expDuration);
        return expDuration;
    }

    private synchronized void clearExpDuration() {
        Logger.d(TAG, "{} - Clearing expDuration", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + EXP_DURATION);
    }

    private synchronized void setBackendApiUrl(String backendApiUrl) {
        Logger.d(TAG, "{} - Setting backendApiUrl to {}", statusManager.getCurrentModeName(), backendApiUrl);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + BACKEND_API_URL, backendApiUrl);
        eSharedPreferences.commit();
    }

    public synchronized String getBackendApiUrl() {
        String backendApiUrl = sharedPreferences.getString(
                statusManager.getCurrentModeName() + BACKEND_API_URL, null);
        if (backendApiUrl == null) {
            Logger.e(TAG, "{} - backendApiUrl is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("backendApiUrl is asked for but not set");
        }
        Logger.d(TAG, "{0} - backendApiUrl is {1}", statusManager.getCurrentModeName(),
                backendApiUrl);
        return backendApiUrl;
    }

    private synchronized void clearBackendApiUrl() {
        Logger.d(TAG, "{} - Clearing backendApiUrl", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + BACKEND_API_URL);
    }

    private synchronized void setResultsPageUrl(String resultsPageUrl) {
        Logger.d(TAG, "{} - Setting resultsPageUrl to {}", statusManager.getCurrentModeName(), resultsPageUrl);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + RESULTS_PAGE_URL, resultsPageUrl);
        eSharedPreferences.commit();
    }

    public synchronized String getResultsPageUrl() {
        String resultsPageUrl = sharedPreferences.getString(
                statusManager.getCurrentModeName() + RESULTS_PAGE_URL, null);
        if (resultsPageUrl == null) {
            Logger.e(TAG, "{} - resultsPageUrl is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("resultsPageUrl is asked for but not set");
        }
        Logger.d(TAG, "{0} - resultsPageUrl is {1}", statusManager.getCurrentModeName(),
                resultsPageUrl);
        return resultsPageUrl;
    }

    private synchronized void clearResultsPageUrl() {
        Logger.d(TAG, "{} - Clearing resultsPageUrl", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + RESULTS_PAGE_URL);
    }

    private synchronized void setSchedulingMinDelay(int schedulingMinDelay) {
        Logger.d(TAG, "{0} - Setting schedulingMinDelay to {1}", statusManager.getCurrentModeName(), schedulingMinDelay);
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MIN_DELAY, schedulingMinDelay);
        eSharedPreferences.commit();
    }

    public synchronized int getSchedulingMinDelay() {
        int schedulingMinDelay = sharedPreferences.getInt(
                statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MIN_DELAY, -1);
        if (schedulingMinDelay == -1) {
            Logger.e(TAG, "{} - SchedulingMinDelay is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("SchedulingMinDelay is asked for but not set");
        }
        Logger.d(TAG, "{0} - schedulingMinDelay is {1}", statusManager.getCurrentModeName(),
                schedulingMinDelay);
        return schedulingMinDelay;
    }

    private synchronized void clearSchedulingMinDelay() {
        Logger.d(TAG, "{} - Clearing schedulingMinDelay", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MIN_DELAY);
    }

    private synchronized void setSchedulingMeanDelay(int schedulingMeanDelay) {
        Logger.d(TAG, "{0} - Setting schedulingMeanDelay to {1}", statusManager.getCurrentModeName(), schedulingMeanDelay);
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MEAN_DELAY, schedulingMeanDelay);
        eSharedPreferences.commit();
    }

    public synchronized int getSchedulingMeanDelay() {
        int schedulingMeanDelay = sharedPreferences.getInt(
                statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MEAN_DELAY, -1);
        if (schedulingMeanDelay == -1) {
            Logger.e(TAG, "{} - SchedulingMeanDelay is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("SchedulingMeanDelay is asked for but not set");
        }
        Logger.d(TAG, "{0} - schedulingMeanDelay is {1}", statusManager.getCurrentModeName(),
                schedulingMeanDelay);
        return schedulingMeanDelay;
    }

    private synchronized void clearSchedulingMeanDelay() {
        Logger.d(TAG, "{} - Clearing schedulingMeanDelay", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + QUESTIONS_SCHEDULING_MEAN_DELAY);
        eSharedPreferences.commit();
    }

    public synchronized void setQuestions(ArrayList<QuestionDescription> questions) {
        Logger.d(TAG, "{} - Setting questions array (and keeping in cache)",
                statusManager.getCurrentModeName());
        questionsCache = questions;
        eSharedPreferences.putString(statusManager.getCurrentModeName() + QUESTIONS,
                json.toJsonInternal(questions));
        eSharedPreferences.commit();
    }

    public synchronized ArrayList<QuestionDescription> getQuestions() {
        Logger.d(TAG, "{} - Getting questions", statusManager.getCurrentModeName());
        if (questionsCache != null) {
            Logger.v(TAG, "{} - Cache is present -> returning questions from cache",
                    statusManager.getCurrentModeName());
        } else {
            Logger.v(TAG, "{} - Cache not present -> getting questions from sharedPreferences",
                    statusManager.getCurrentModeName());
            TypeReference<ArrayList<QuestionDescription>> questionDescriptionsArrayType =
                    new TypeReference<ArrayList<QuestionDescription>>() {};
            questionsCache = json.fromJson(
                    sharedPreferences.getString(statusManager.getCurrentModeName() + QUESTIONS, null),
                    questionDescriptionsArrayType);
        }

        if (questionsCache == null) {
            Logger.e(TAG, "{} - Questions asked for but not set",
                    statusManager.getCurrentModeName());
            throw new RuntimeException("Questions asked for but not set");
        }

        return questionsCache;
    }

    private synchronized void clearQuestions() {
        Logger.d(TAG, "{} - Clearing questions (and clearing cache)",
                statusManager.getCurrentModeName());
        questionsCache = null;
        eSharedPreferences.remove(statusManager.getCurrentModeName() + QUESTIONS);
        eSharedPreferences.commit();
    }

    private synchronized void setGlossary(HashMap<String,String> glossary) {
        String glossaryJson = json.toJsonInternal(glossary);
        Logger.d(TAG, "{0} - Setting glossary to {1}", statusManager.getCurrentModeName(), glossaryJson);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + GLOSSARY, glossaryJson);
        eSharedPreferences.commit();
    }

    public synchronized HashMap<String,String> getGlossary() {
        String glossaryJson = sharedPreferences.getString(
                statusManager.getCurrentModeName() + GLOSSARY, null);
        if (glossaryJson == null) {
            String msg = "Glossary asked for but not found";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        HashMap<String,String> glossary = json.fromJson(glossaryJson,
                new TypeReference<HashMap<String,String>>() {});
        Logger.v(TAG, "{0} - Glossary is {1}", statusManager.getCurrentModeName(), glossaryJson);
        return glossary;
    }

    private synchronized void clearGlossary() {
        Logger.d(TAG, "{} - Clearing glossary", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + GLOSSARY);
        eSharedPreferences.commit();
    }

    public synchronized QuestionDescription getQuestionDescription(String name) {
        Logger.d(TAG, "{0} - Looking for questionDescription {1}",
                statusManager.getCurrentModeName(), name);

        // Get list of all names
        ArrayList<QuestionDescription> questions = getQuestions();
        ArrayList<String> names = new ArrayList<String>(questions.size());
        for (QuestionDescription qd : questions) {
            names.add(qd.getQuestionName());
        }

        int questionIndex = names.indexOf(name);
        if (questionIndex == -1) {
            Logger.e(TAG, "{0} - Question {1} asked for but not found",
                    statusManager.getCurrentModeName(), name);
            throw new RuntimeException("Question asked for but not found (see logs)");
        }

        return questions.get(questionIndex);
    }

    public synchronized void setSequences(ArrayList<SequenceDescription> sequences) {
        Logger.d(TAG, "{} - Setting sequences array (and keeping in cache)",
                statusManager.getCurrentModeName());
        sequencesCache = sequences;
        eSharedPreferences.putString(statusManager.getCurrentModeName() + SEQUENCES,
                json.toJsonInternal(sequences));
        eSharedPreferences.commit();
    }

    public synchronized ArrayList<SequenceDescription> getSequences() {
        Logger.d(TAG, "{} - Getting sequences", statusManager.getCurrentModeName());
        if (sequencesCache != null) {
            Logger.v(TAG, "{} - Cache is present -> returning sequences from cache",
                    statusManager.getCurrentModeName());
        } else {
            Logger.v(TAG, "{} - Cache not present -> getting sequences from sharedPreferences",
                    statusManager.getCurrentModeName());
            TypeReference<ArrayList<SequenceDescription>> sequenceDescriptionsArrayType =
                    new TypeReference<ArrayList<SequenceDescription>>() {};
            sequencesCache = json.fromJson(
                    sharedPreferences.getString(statusManager.getCurrentModeName() + SEQUENCES, null),
                    sequenceDescriptionsArrayType);
        }

        if (sequencesCache == null) {
            Logger.e(TAG, "{} - Sequences asked for but not set",
                    statusManager.getCurrentModeName());
            throw new RuntimeException("Sequences asked for but not set");
        }

        return sequencesCache;
    }

    private synchronized void clearSequences() {
        Logger.d(TAG, "{} - Clearing sequences (and clearing cache)",
                statusManager.getCurrentModeName());
        sequencesCache = null;
        eSharedPreferences.remove(statusManager.getCurrentModeName() + SEQUENCES);
        eSharedPreferences.commit();
    }

    public synchronized SequenceDescription getSequenceDescription(String name) {
        Logger.d(TAG, "{0} - Looking for sequenceDescription {1}",
                statusManager.getCurrentModeName(), name);

        // Get list of all names
        ArrayList<SequenceDescription> sequences = getSequences();
        ArrayList<String> names = new ArrayList<String>(sequences.size());
        for (SequenceDescription s : sequences) {
            names.add(s.getName());
        }

        int sequenceIndex = names.indexOf(name);
        if (sequenceIndex == -1) {
            Logger.e(TAG, "{0} - Sequence {1} asked for but not found",
                    statusManager.getCurrentModeName(), name);
            throw new RuntimeException("Sequence asked for but not found (see logs)");
        }

        return sequences.get(sequenceIndex);
    }

    public synchronized ArrayList<SequenceDescription> getSequencesByType(String type) {
        Logger.d(TAG, "{} - Getting sequences by type", statusManager.getCurrentModeName());
        ArrayList<SequenceDescription> sequences = getSequences();
        ArrayList<SequenceDescription> sequencesByType = new ArrayList<SequenceDescription>();
        for (SequenceDescription s : sequences) {
            if (s.getType().equals(type)){
                sequencesByType.add(s);
            }
        }
        return sequencesByType;
    }

    public synchronized void flush() {
        Logger.d(TAG, "{} - Flushing all parameters", statusManager.getCurrentModeName());
        statusManager.clearParametersUpdated();
        statusManager.setParametersFlushed();

        profileStorage.clearParametersVersion();
        clearBackendExpId();
        clearBackendDbName();
        clearExpDuration();
        clearBackendApiUrl();
        clearResultsPageUrl();
        clearSchedulingMinDelay();
        clearSchedulingMeanDelay();
        clearQuestions();
        clearSequences();
        clearGlossary();
    }

    // import parameters from json file into database
    public synchronized void importParameters(String jsonParametersString)
            throws ParametersSyntaxException {
        Logger.d(TAG, "{} - Importing parameters from JSON", statusManager.getCurrentModeName());
        try {
            ServerParametersJson serverParametersJson = json.fromJson(
                    jsonParametersString, ServerParametersJson.class);

            if (serverParametersJson == null) {
                throw new JsonParametersException("Server Json was malformed, could not be parsed");
            }

            serverParametersJson.validateInitialization();

            // All is good, do the real import of all objects in the root
            flush();
            profileStorage.setParametersVersion(serverParametersJson.getVersion());
            setBackendExpId(serverParametersJson.getBackendExpId());
            setBackendDbName(serverParametersJson.getBackendDbName());
            setExpDuration(serverParametersJson.getExpDuration());
            setBackendApiUrl(serverParametersJson.getBackendApiUrl());
            setResultsPageUrl(serverParametersJson.getResultsPageUrl());
            setSchedulingMinDelay(serverParametersJson.getSchedulingMinDelay());
            setSchedulingMeanDelay(serverParametersJson.getSchedulingMeanDelay());
            setGlossary(serverParametersJson.getGlossary());

            // loading the questions
            setQuestions(serverParametersJson.getQuestions());
            setSequences(serverParametersJson.getSequences());
        } catch (JsonParametersException e) {
            e.printStackTrace();
            throw new ParametersSyntaxException();
        }
    }

    public synchronized void onReady(ParametersStorageCallback callback, String startSyncAppMode,
                                     boolean isDebug) {
        if (!statusManager.areParametersUpdated()) {
            Logger.i(TAG, "{} - ParametersStorage not ready -> " +
                    "updating parameters", statusManager.getCurrentModeName());

            // If during our network request, parameters are flushed,
            // we won't import the received parameters
            Logger.v(TAG, "Clearing parameters flushed");
            statusManager.clearParametersFlushed();

            asyncUpdateParameters(callback, startSyncAppMode, isDebug);
        } else {
            Logger.i(TAG, "{} - ParametersStorage ready -> calling back callback " +
                    "straight away", statusManager.getCurrentModeName());
            callback.onParametersStorageReady(true);
        }
    }

    private synchronized void asyncUpdateParameters(final ParametersStorageCallback callback,
                                                    final String startSyncAppMode,
                                                    final boolean isDebug) {
        Logger.d(TAG, "Updating parameters");

        if (statusManager.getCurrentMode() == StatusManager.MODE_TEST && isDebug) {
            Toast.makeText(context, "Reloading parameters...", Toast.LENGTH_SHORT).show();
        }

        HttpConversationCallback updateParametersCallback =
                new HttpConversationCallback() {

            private String TAG = "Parameters HttpConversationCallback";

            @Override
            public void onHttpConversationFinished(boolean success,
                                                   String serverAnswer) {
                Logger.d(TAG, "Parameters update HttpConversation finished");

                // Exit if app mode has changed before we could import parameters
                if (!statusManager.getCurrentModeName().equals(startSyncAppMode)) {
                    Logger.i(TAG, "App mode has changed from {0} to {1} since sync started, "
                                    + "aborting parameters update.", startSyncAppMode,
                            statusManager.getCurrentModeName());
                    callback.onParametersStorageReady(false);
                    return;
                }

                // Exit if parameters have been flushed since we started
                if (statusManager.areParametersFlushed()) {
                    Logger.i(TAG, "Parameters have been flushed since sync started, "
                            + "aborting parameters update.");
                    callback.onParametersStorageReady(false);
                    return;
                }

                if (success) {
                    Logger.i(TAG, "Successfully retrieved parameters from " +
                            "server");
                    Logger.td(context, TAG + ": new " +
                            "parameters downloaded from server");

                    // Import the parameters, and remember not to update
                    // parameters again.
                    try {
                        ParametersStorage.this.importParameters(serverAnswer);
                        Logger.d(TAG, "Importing new parameters to storage");
                    } catch (ParametersSyntaxException e) {
                        e.printStackTrace();
                        Logger.e(TAG, "Downloaded parameters were malformed -> " +
                                "parameters not updated");
                        callback.onParametersStorageReady(false);
                        if (statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
                            Toast.makeText(context, "Test parameters from server were malformed! " +
                                    "Correct them and try again", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    Logger.i(TAG, "Parameters successfully imported");
                    statusManager.setParametersUpdated();
                    callback.onParametersStorageReady(true);

                    if (isDebug && statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
                        Toast.makeText(context, "Parameters successfully updated",
                                Toast.LENGTH_SHORT).show();
                    }

                    Logger.d(TAG, "Starting SchedulerService to take new parameters into account");
                    Intent schedulerIntent = new Intent(context, SchedulerService.class);
                    context.startService(schedulerIntent);
                } else {
                    Logger.w(TAG, "Error while retrieving new parameters from " +
                            "server");
                    callback.onParametersStorageReady(false);
                    if (isDebug && statusManager.getCurrentMode() == StatusManager.MODE_TEST) {
                        Toast.makeText(context, "Error retrieving parameters from server. " +
                                "Are you connected to internet?", Toast.LENGTH_LONG).show();
                    }
                }
            }

        };

        String getUrl = MessageFormat.format(ServerConfig.PARAMETERS_URL_BASE,
                statusManager.getCurrentModeName());
        HttpGetData updateParametersData = new HttpGetData(getUrl, updateParametersCallback);
        HttpGetTask updateParametersTask = new HttpGetTask();
        updateParametersTask.execute(updateParametersData);
    }

}
