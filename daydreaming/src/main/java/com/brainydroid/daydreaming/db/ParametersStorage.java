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
    private static String TIPI_QUESTIONS = "TipiQuestions";


    private static final String SQL_CREATE_TABLE_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS {}" + TABLE_QUESTIONS + " (" +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_CATEGORY + " TEXT NOT NULL, " +
                    COL_SUB_CATEGORY + " TEXT, " +
                    COL_DETAILS + " TEXT NOT NULL, " +
                    COL_SLOT + " TEXT NOT NULL" +
                    ");";

    private static String QUESTIONS_SCHEDULING_MIN_DELAY = "schedulingMinDelay";
    private static String QUESTIONS_SCHEDULING_MEAN_DELAY = "schedulingMeanDelay";

    private static String PARAMETERS_VERSION = "parametersVersion";
    private static String EXP_DURATION = "expDuration"; // no need to make global but listing is nice
    private static String BACKEND_EXP_ID = "backendExpId";
    private static String URL_BACKEND_API = "backendUrlApi";
    private static String URL_RESULTS_PAGE = "resultsPageUrl";
    private static String WELCOME_TEXT = "welcomeText";
    private static String DESCRIPTION_TEXT = "descriptionText";

    private static String QUESTIONS_N_SLOTS_PER_PROBE = "questionsNSlotsPerProbe";

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

    private synchronized void setParametersVersion(String version) {
        Logger.d(TAG, "{} - Setting parametersVersion to {}", statusManager.getCurrentModeName(), version);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + PARAMETERS_VERSION, version);
        eSharedPreferences.commit();
        profileStorage.setParametersVersion(version);
    }

    //setExpDuration    setBackendExpId    setBackendUrlApi    setResultsPageUrl
    private synchronized void setExpDuration(int expDuration) {
        Logger.d(TAG, "{} - Setting expDuration to {}", statusManager.getCurrentModeName(), expDuration);
        eSharedPreferences.putInt(statusManager.getCurrentModeName() + EXP_DURATION, expDuration);
        eSharedPreferences.commit();
    }

    private synchronized void setBackendExpId(String expId) {
        Logger.d(TAG, "{} - Setting expId to {}", statusManager.getCurrentModeName(), expId);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + BACKEND_EXP_ID, expId);
        eSharedPreferences.commit();
    }

    private synchronized void setBackendUrlApi(String urlBackendApi) {
        Logger.d(TAG, "{} - Setting urlBackendApi to {}", statusManager.getCurrentModeName(), urlBackendApi);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + URL_BACKEND_API, urlBackendApi);
        eSharedPreferences.commit();
    }

    private synchronized void setResultsPageUrl(String urlResultsPage) {
        Logger.d(TAG, "{} - Setting urlResultsPage to {}", statusManager.getCurrentModeName(), urlResultsPage);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + URL_RESULTS_PAGE, urlResultsPage);
        eSharedPreferences.commit();
    }

    private synchronized void setDescriptionText(String descriptionText) {
        Logger.d(TAG, "{} - Setting urlResultsPage to {}", statusManager.getCurrentModeName(), descriptionText);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + DESCRIPTION_TEXT, descriptionText);
        eSharedPreferences.commit();
    }

    private synchronized void setWelcomeText(String welcomeText) {
        Logger.d(TAG, "{} - Setting urlResultsPage to {}", statusManager.getCurrentModeName(), welcomeText);
        eSharedPreferences.putString(statusManager.getCurrentModeName() + WELCOME_TEXT, welcomeText);
        eSharedPreferences.commit();
    }

    private synchronized void clearParametersVersion() {
        Logger.d(TAG, "{} - Clearing parameters version", statusManager.getCurrentModeName());
        eSharedPreferences.remove(PARAMETERS_VERSION);
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

            // Check version is set
            String version = serverParametersJson.getVersion();
            if (version.equals(ServerParametersJson.DEFAULT_PARAMETERS_VERSION)) {
                throw new JsonSyntaxException("version can't be its unset value");
            }

            // Check nSlotsPerProbe is set
            int nSlotsPerProbe = serverParametersJson.getNSlotsPerProbe();
            if (nSlotsPerProbe == ServerParametersJson.DEFAULT_N_SLOTS_PER_PROBE) {
                throw new JsonSyntaxException("nSlotsPerProbe can't be its unset value");
            }

            // Check schedulingMinDelay is set
            int schedulingMinDelay = serverParametersJson.getSchedulingMinDelay();
            if (schedulingMinDelay == ServerParametersJson.DEFAULT_SCHEDULING_MIN_DELAY) {
                throw new JsonSyntaxException("schedulingMinDelay can't be its unset value");
            }

            // Check schedulingMeanDelay is set
            int schedulingMeanDelay = serverParametersJson.getSchedulingMeanDelay();
            if (schedulingMeanDelay == ServerParametersJson.DEFAULT_SCHEDULING_MEAN_DELAY) {
                throw new JsonSyntaxException("schedulingMeanDelay can't be its unset value");
            }

            // Get all question slots and check there are at least as many as
            // nSlotsPerProbe
            HashSet<String> slots = new HashSet<String>();
            for (Question q : serverParametersJson.getQuestionsArrayList()) {
                slots.add(q.getSlot());
            }
            if (slots.size() < nSlotsPerProbe) {
                throw new JsonSyntaxException("There must be at least as many" +
                        " slots defined in the questions as nSlotsPerProbe");
            }

            // All is good, do the real import
            flush();
            setParametersVersion(serverParametersJson.getVersion());
            setExpDuration(serverParametersJson.getExpDuration());
            setBackendExpId(serverParametersJson.getBackendExpId());
            setBackendUrlApi(serverParametersJson.getBackendApiUrl());
            setResultsPageUrl(serverParametersJson.getResultsPageUrl());
            setWelcomeText(serverParametersJson.getWelcomeText());
            setDescriptionText(serverParametersJson.getDescriptionText());

            // testing execution of json parsing for new grammar
            FirstLaunch firstLaunch = serverParametersJson.getFirstLaunch();

            profileStorage.setParametersVersion(version);
            setSchedulingMinDelay(schedulingMinDelay);
            setSchedulingMeanDelay(schedulingMeanDelay);
            setNSlotsPerProbe(nSlotsPerProbe);
            add(serverParametersJson.getQuestionsArrayList());

        } catch (JsonSyntaxException e) {
            throw new ParametersSyntaxException();
        }
    }

}
