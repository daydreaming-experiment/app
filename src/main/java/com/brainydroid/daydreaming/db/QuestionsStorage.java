package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Random;

@Singleton
public class QuestionsStorage {

    private static String TAG = "QuestionsStorage";

    private static String QUESTIONS_VERSION = "questionsVersion";
    private static String TABLE_QUESTIONS = "questions";

    private static final String SQL_CREATE_TABLE_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONS + " (" +
                    Question.COL_NAME + " TEXT NOT NULL, " +
                    Question.COL_CATEGORY + " TEXT NOT NULL, " +
                    Question.COL_SUB_CATEGORY + " TEXT, " +
                    Question.COL_DETAILS + " TEXT NOT NULL" +
                    ");";

    @Inject Json json;
    @Inject Random random;
    @Inject QuestionFactory questionFactory;

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor eSharedPreferences;
    private final SQLiteDatabase rDb;
    private final SQLiteDatabase wDb;

    // Constructor
    @Inject
    public QuestionsStorage(Storage storage,
                            SharedPreferences sharedPreferences) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] QuestionsStorage");
        }

        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
        rDb = storage.getReadableDatabase();
        wDb = storage.getWritableDatabase();
        wDb.execSQL(SQL_CREATE_TABLE_QUESTIONS);
    }

    public int getQuestionsVersion() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestionsVersion");
        }

        int questionsVersion = sharedPreferences.getInt(QUESTIONS_VERSION,
                -1);

        if (questionsVersion == -1) {
            throw new RuntimeException("questionsVersion is not set, " +
                    "meaning questions were never loaded");
        }

        return questionsVersion;
    }

    private void setQuestionsVersion(int questionsVersion) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setQuestionsVersion");
        }

        eSharedPreferences.putInt(QUESTIONS_VERSION, questionsVersion);
        eSharedPreferences.commit();
    }

    // get question from id in db
    public Question getQuestion(String questionName) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestion");
        }

        Cursor res = rDb.query(TABLE_QUESTIONS, null,
                Question.COL_NAME + "=?", new String[] {questionName},
                null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        Question q = questionFactory.create();
        q.setName(res.getString(res.getColumnIndex(Question.COL_NAME)));
        q.setCategory(res.getString(res.getColumnIndex(Question.COL_CATEGORY)));
        q.setSubCategory(res.getString(
                res.getColumnIndex(Question.COL_SUB_CATEGORY)));
        q.setDetailsFromJson(res.getString(
                res.getColumnIndex(Question.COL_DETAILS)));
        res.close();

        return q;
    }

    // get questions ids in questions db
    public ArrayList<String> getQuestionNames() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestionNames");
        }

        Cursor res = rDb.query(TABLE_QUESTIONS,
                new String[] {Question.COL_NAME}, null, null, null,
                null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<String> questionNames = new ArrayList<String>();
        do {
            questionNames.add(res.getString(
                    res.getColumnIndex(Question.COL_NAME)));
        } while (res.moveToNext());
        res.close();

        return questionNames;
    }

    // getRandomQuestions
    public ArrayList<Question> getRandomQuestions(int nQuestions) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getRandomQuestions");
        }

        ArrayList<String> questionNames = getQuestionNames();
        int nIds = questionNames.size();

        ArrayList<Question> randomQuestions = new ArrayList<Question>();
        int rIndex;

        for (int i = 0; i < nQuestions && i < nIds; i++) {
            rIndex = random.nextInt(questionNames.size());
            randomQuestions.add(getQuestion(questionNames.get(rIndex)));
            questionNames.remove(rIndex);
        }

        return randomQuestions;
    }

    public void flushAll() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] flushAll");
        }

        wDb.delete(TABLE_QUESTIONS, null, null);
    }

    private void addQuestions(ArrayList<Question> questions) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addQuestions");
        }

        for (Question q : questions) {
            addQuestion(q);
        }
    }

    // add question in database
    private void addQuestion(Question question) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addQuestion");
        }

        wDb.insert(TABLE_QUESTIONS, null, getQuestionContentValues(question));
    }

    private ContentValues getQuestionContentValues(Question question) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestionContentValues");
        }

        ContentValues qValues = new ContentValues();
        qValues.put(Question.COL_NAME, question.getName());
        qValues.put(Question.COL_CATEGORY, question.getCategory());
        qValues.put(Question.COL_SUB_CATEGORY,
                question.getSubCategory());
        qValues.put(Question.COL_DETAILS, question.getDetailsAsJson());
        return qValues;
    }

    // import questions from json file into database
    public void importQuestions(String jsonQuestionsString) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] importQuestions");
        }

        ServerQuestionsJson serverQuestionsJson = json.fromJson(
                jsonQuestionsString, ServerQuestionsJson.class);
        flushAll();
        setQuestionsVersion(serverQuestionsJson.getVersion());
        addQuestions(serverQuestionsJson.getQuestionsArrayList());
    }

}
