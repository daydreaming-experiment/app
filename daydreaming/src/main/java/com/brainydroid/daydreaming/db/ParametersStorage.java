package com.brainydroid.daydreaming.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.SchedulerService;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.network.CryptoStorageCallback;
import com.brainydroid.daydreaming.network.HttpConversationCallback;
import com.brainydroid.daydreaming.network.HttpGetData;
import com.brainydroid.daydreaming.network.HttpGetTask;
import com.brainydroid.daydreaming.network.ParametersStorageCallback;
import com.brainydroid.daydreaming.network.ServerConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Singleton
public class ParametersStorage {

    private static String TAG = "ParametersStorage";

    public static final String COL_NAME = "questionName";
    public static final String COL_CATEGORY = "questionCategory";
    public static final String COL_SUB_CATEGORY = "questionSubCategory";
    public static final String COL_DETAILS = "questionDetails";
    public static final String COL_SLOT = "questionSlot";

    public static final String COL_STATUS = "questionStatus";
    public static final String COL_ANSWER = "questionAnswer";
    public static final String COL_LOCATION = "questionLocation";
    public static final String COL_NTP_TIMESTAMP = "questionNtpTimestamp";
    public static final String COL_SYSTEM_TIMESTAMP =
            "questionSystemTimestamp";

    private static String TABLE_QUESTIONS = "Questions";

    private static final String SQL_CREATE_TABLE_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS {}" + TABLE_QUESTIONS + " (" +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_CATEGORY + " TEXT NOT NULL, " +
                    COL_SUB_CATEGORY + " TEXT, " +
                    COL_DETAILS + " TEXT NOT NULL, " +
                    COL_SLOT + " TEXT NOT NULL" +
                    ");";

    public static String QUESTIONS_SCHEDULING_MIN_DELAY = "schedulingMinDelay";
    public static String QUESTIONS_SCHEDULING_MEAN_DELAY = "schedulingMeanDelay";
    public static String QUESTIONS_N_SLOTS_PER_PROBE = "questionsNSlotsPerProbe";

    public static String BACKEND_EXP_ID = "backendExpId";
    public static String BACKEND_DB_NAME = "backendDbName";
    public static String EXP_DURATION = "expDuration";
    public static String BACKEND_API_URL = "backendApiUrl";
    public static String RESULTS_PAGE_URL = "resultsPageUrl";
    public static String FIRST_LAUNCH =  "firstLaunch";
    public static String GLOSSARY = "glossary";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject Json json;
    @Inject QuestionFactory questionFactory;
    @Inject ProfileStorage profileStorage;
    @Inject SlottedQuestionsFactory slottedQuestionsFactory;
    @Inject StatusManager statusManager;
    @Inject Context context;

    private final SQLiteDatabase db;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public ParametersStorage(Storage storage, StatusManager statusManager,
                             SharedPreferences sharedPreferences) {
        Logger.d(TAG, "{} - Building ParametersStorage: creating table if it " +
                "doesn't exist", statusManager.getCurrentModeName());

        db = storage.getWritableDatabase();
        for (String modeName : StatusManager.AVAILABLE_MODE_NAMES) {
            db.execSQL(MessageFormat.format(SQL_CREATE_TABLE_QUESTIONS, modeName));
        }

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

    private synchronized void setNSlotsPerProbe(int nSlotsPerProbe) {
        Logger.d(TAG, "{0} - Setting nSlotsPerProbe to {1}", statusManager.getCurrentModeName(), nSlotsPerProbe);
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + QUESTIONS_N_SLOTS_PER_PROBE, nSlotsPerProbe);
        eSharedPreferences.commit();
    }

    public synchronized int getNSlotsPerProbe() {
        int nSlotsPerProbe = sharedPreferences.getInt(
                statusManager.getCurrentModeName() + QUESTIONS_N_SLOTS_PER_PROBE,
                ServerParametersJson.DEFAULT_N_SLOTS_PER_PROBE);
        Logger.v(TAG, "{0} - nSlotsPerProbe is {1}", statusManager.getCurrentModeName(), nSlotsPerProbe);
        return nSlotsPerProbe;
    }

    private synchronized void setGlossary(HashMap<String,String> glossary) {
        String glossaryString = json.toJson(glossary);
        Logger.d(TAG, "{0} - Setting glossary to {1}", statusManager.getCurrentModeName(), glossaryString);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + GLOSSARY, glossaryString);
        eSharedPreferences.commit();
    }

    public synchronized HashMap<String,String> getGlossary() {
        String glossaryString = sharedPreferences.getString(
                statusManager.getCurrentModeName() + GLOSSARY,
                ServerParametersJson.DEFAULT_GLOSSARY);
        Type hmtype = new TypeToken<HashMap<String,String>>() {}.getType();
        HashMap<String,String> glossary = json.fromJson(glossaryString,hmtype);
        Logger.v(TAG, "{0} - glossary is {1}", statusManager.getCurrentModeName(), glossaryString);
        return glossary;
    }

    public synchronized void clearNSlotsPerProbe() {
        Logger.d(TAG, "{} - Clearing nSlotsPerProbe", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + QUESTIONS_N_SLOTS_PER_PROBE);
        eSharedPreferences.commit();
    }

    public synchronized void setFirstLaunch(FirstLaunch firstLaunch) {
        // Save raw JSON
        String jsonFirstLaunch = json.toJson(firstLaunch);
        Logger.d(TAG, "{0} - Setting (json)firstLaunch to {1}",
                statusManager.getCurrentModeName(), jsonFirstLaunch);

        eSharedPreferences.putString(statusManager.getCurrentModeName() + FIRST_LAUNCH, jsonFirstLaunch);
        eSharedPreferences.commit();
    }

    public synchronized FirstLaunch getFirstLaunch() {
        String jsonFirstLaunch = sharedPreferences.getString(
                statusManager.getCurrentModeName() + FIRST_LAUNCH, null);
        if (jsonFirstLaunch == null) {
            Logger.e(TAG, "{} - (json)firstLaunch is asked for but not set",
                    statusManager.getCurrentMode());
            throw new RuntimeException("(json)firstLaunch is asked for but not set");
        }
        Logger.d(TAG, "{0} - (json)FirstLaunch is {1}", statusManager.getCurrentModeName(),
                jsonFirstLaunch);

        // Deserialize the JSON
        return json.fromJson(jsonFirstLaunch, FirstLaunch.class);
    }

    private synchronized void clearFirstLaunch() {
        Logger.d(TAG, "{} - Clearing firstLaunch", statusManager.getCurrentModeName());
        eSharedPreferences.remove(statusManager.getCurrentModeName() + FIRST_LAUNCH);
    }

    // get question from id in db
    public synchronized Question create(String questionName) {
        Logger.d(TAG, "{0} - Retrieving question {1} from db", statusManager.getCurrentModeName(), questionName);

        Cursor res = db.query(statusManager.getCurrentModeName() + TABLE_QUESTIONS, null,
                COL_NAME + "=?", new String[]{questionName},
                null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        Question q = questionFactory.create();
        q.setName(res.getString(res.getColumnIndex(COL_NAME)));
        q.setCategory(res.getString(res.getColumnIndex(COL_CATEGORY)));
        q.setSubCategory(res.getString(
                res.getColumnIndex(COL_SUB_CATEGORY)));
        q.setDetailsFromJson(res.getString(
                res.getColumnIndex(COL_DETAILS)));
        q.setSlot(res.getString(res.getColumnIndex(COL_SLOT)));
        res.close();

        return q;
    }

    public synchronized SlottedQuestions getSlottedQuestions() {
        Logger.d(TAG, "{} - Retrieving all questions from db", statusManager.getCurrentModeName());

        Cursor res = db.query(statusManager.getCurrentModeName() + TABLE_QUESTIONS, null, null, null, null, null,
                null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        SlottedQuestions slottedQuestions = slottedQuestionsFactory.create();
        do {
            Question q = questionFactory.create();
            q.setName(res.getString(res.getColumnIndex(COL_NAME)));
            q.setCategory(res.getString(res.getColumnIndex(COL_CATEGORY)));
            q.setSubCategory(res.getString(
                    res.getColumnIndex(COL_SUB_CATEGORY)));
            q.setDetailsFromJson(res.getString(
                    res.getColumnIndex(COL_DETAILS)));
            q.setSlot(res.getString(res.getColumnIndex(COL_SLOT)));

            slottedQuestions.add(q);
        } while (res.moveToNext());
        res.close();

        return slottedQuestions;
    }

    public synchronized void flush() {
        Logger.d(TAG, "{} - Flushing all parameters", statusManager.getCurrentModeName());
        statusManager.clearParametersUpdated();
        statusManager.setParametersFlushed();
        flushQuestions();
        profileStorage.clearParametersVersion();
        clearSchedulingMinDelay();
        clearSchedulingMeanDelay();
        clearNSlotsPerProbe();
    }

    public synchronized void flushQuestions() {
        Logger.d(TAG, "{} - Flushing questions from db", statusManager.getCurrentModeName());
        db.delete(statusManager.getCurrentModeName() + TABLE_QUESTIONS, null, null);
    }

    private synchronized void add(ArrayList<Question> questions) {
        Logger.d(TAG, "{} - Storing an array of questions to db", statusManager.getCurrentModeName());
        for (Question q : questions) {
            add(q);
        }
    }

    // add question in database
    private synchronized void add(Question question) {
        Logger.d(TAG, "{0} - Storing question {1} to db", statusManager.getCurrentModeName(), question.getName());
        db.insert(statusManager.getCurrentModeName() + TABLE_QUESTIONS, null, getQuestionValues(question));
    }

    private synchronized ContentValues getQuestionValues(Question question) {
        Logger.d(TAG, "{} - Building question values", statusManager.getCurrentModeName());

        ContentValues qValues = new ContentValues();
        qValues.put(COL_NAME, question.getName());
        qValues.put(COL_CATEGORY, question.getCategory());
        qValues.put(COL_SUB_CATEGORY,
                question.getSubCategory());
        qValues.put(COL_DETAILS, question.getDetailsAsJson());
        qValues.put(COL_SLOT, question.getSlot());
        return qValues;
    }

    // import parameters from json file into database
    public synchronized void importParameters(String jsonParametersString)
            throws ParametersSyntaxException {
        Logger.d(TAG, "{} - Importing parameters from JSON", statusManager.getCurrentModeName());
        try {
            ServerParametersJson serverParametersJson = json.fromJson(
                    jsonParametersString, ServerParametersJson.class);

            if (serverParametersJson == null) {
                throw new JsonSyntaxException("Server Json was malformed, could not be parsed");
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
            setFirstLaunch(serverParametersJson.getFirstLaunch());
            setNSlotsPerProbe(serverParametersJson.getNSlotsPerProbe());
            setSchedulingMinDelay(serverParametersJson.getSchedulingMinDelay());
            setSchedulingMeanDelay(serverParametersJson.getSchedulingMeanDelay());
            setGlossary(serverParametersJson.getGlossary());

            // loading the questions
            add(serverParametersJson.getQuestionsArrayList());

        } catch (JsonParametersException e) {
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

    // TODO[seb]: check this is still ok when switching back and forth from test mode, and resetting parameters while keeping profile answers (esp. when exp_id has changed server-side)
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
