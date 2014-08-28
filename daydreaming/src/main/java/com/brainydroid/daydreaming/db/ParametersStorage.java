package com.brainydroid.daydreaming.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.text.MessageFormat;
import java.util.ArrayList;
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

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor eSharedPreferences;

    @Inject Json json;
    @Inject QuestionFactory questionFactory;
    @Inject ProfileStorage profileStorage;
    @Inject SlottedQuestionsFactory slottedQuestionsFactory;
    @Inject StatusManager statusManager;

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
            // loading the questions
            add(serverParametersJson.getQuestionsArrayList());

        } catch (JsonParametersException e) {
            throw new ParametersSyntaxException();
        }
    }

}
