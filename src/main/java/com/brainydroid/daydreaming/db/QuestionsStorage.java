package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Random;

@Singleton
public class QuestionsStorage {

    private static String TAG = "QuestionsStorage";

    public static final String COL_NAME = "questionName";
    public static final String COL_CATEGORY = "questionCategory";
    public static final String COL_SUB_CATEGORY = "questionSubCategory";
    public static final String COL_DETAILS = "questionDetails";

    public static final String COL_STATUS = "questionStatus";
    public static final String COL_ANSWER = "questionAnswer";
    public static final String COL_LOCATION = "questionLocation";
    public static final String COL_TIMESTAMP = "questionTimestamp";

    private static String QUESTIONS_VERSION = "questionsVersion";
    private static String TABLE_QUESTIONS = "questions";

    private static final String SQL_CREATE_TABLE_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONS + " (" +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_CATEGORY + " TEXT NOT NULL, " +
                    COL_SUB_CATEGORY + " TEXT, " +
                    COL_DETAILS + " TEXT NOT NULL" +
                    ");";

    @Inject Json json;
    @Inject Random random;
    @Inject QuestionFactory questionFactory;

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor eSharedPreferences;
    private final SQLiteDatabase db;

    // Constructor
    @Inject
    public QuestionsStorage(Storage storage,
                            SharedPreferences sharedPreferences) {

        Logger.d(TAG, "Building QuestionsStorage: creating table if it " +
                "doesn't exist");

        this.sharedPreferences = sharedPreferences;
        eSharedPreferences = sharedPreferences.edit();
        db = storage.getWritableDatabase();
        db.execSQL(SQL_CREATE_TABLE_QUESTIONS);
    }

    public synchronized int getQuestionsVersion() {
        Logger.v(TAG, "Getting questions version");
        int questionsVersion = sharedPreferences.getInt(QUESTIONS_VERSION,
                -1);

        if (questionsVersion == -1) {
            throw new RuntimeException("questionsVersion is not set, " +
                    "meaning questions were never loaded");
        }

        return questionsVersion;
    }

    private synchronized void setQuestionsVersion(int questionsVersion) {
        Logger.d(TAG, "Setting questions version to {0}", questionsVersion);
        eSharedPreferences.putInt(QUESTIONS_VERSION, questionsVersion);
        eSharedPreferences.commit();
    }

    // get question from id in db
    public synchronized Question create(String questionName) {
        Logger.d(TAG, "Retrieving question {0} from db", questionName);

        Cursor res = db.query(TABLE_QUESTIONS, null,
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
        res.close();

        return q;
    }

    // get questions ids in questions db
    public synchronized ArrayList<String> getQuestionNames() {
        Logger.d(TAG, "Retrieving available question names from db");

        Cursor res = db.query(TABLE_QUESTIONS,
                new String[] {COL_NAME}, null, null, null,
                null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        ArrayList<String> questionNames = new ArrayList<String>();
        do {
            questionNames.add(res.getString(
                    res.getColumnIndex(COL_NAME)));
        } while (res.moveToNext());
        res.close();

        return questionNames;
    }

    // getRandomQuestions
    public synchronized ArrayList<Question> getRandomQuestions(
            int nQuestions) {
        Logger.d(TAG, "Retrieving {0} random questions from db", nQuestions);

        ArrayList<String> questionNames = getQuestionNames();
        int nIds = questionNames.size();

        ArrayList<Question> randomQuestions = new ArrayList<Question>();
        int rIndex;

        for (int i = 0; i < nQuestions && i < nIds; i++) {
            rIndex = random.nextInt(questionNames.size());
            randomQuestions.add(create(questionNames.get(rIndex)));
            questionNames.remove(rIndex);
        }

        return randomQuestions;
    }

    public synchronized void flush() {
        Logger.d(TAG, "Flushing questions from db");
        db.delete(TABLE_QUESTIONS, null, null);
    }

    private synchronized void add(ArrayList<Question> questions) {
        Logger.d(TAG, "Storing an array of questions to db");
        for (Question q : questions) {
            add(q);
        }
    }

    // add question in database
    private synchronized void add(Question question) {
        Logger.d(TAG, "Storing question {0} to db", question.getName());
        db.insert(TABLE_QUESTIONS, null, getQuestionValues(question));
    }

    private synchronized ContentValues getQuestionValues(Question question) {
        Logger.d(TAG, "Building question values");

        ContentValues qValues = new ContentValues();
        qValues.put(COL_NAME, question.getName());
        qValues.put(COL_CATEGORY, question.getCategory());
        qValues.put(COL_SUB_CATEGORY,
                question.getSubCategory());
        qValues.put(COL_DETAILS, question.getDetailsAsJson());
        return qValues;
    }

    // import questions from json file into database
    public synchronized void importQuestions(String jsonQuestionsString) {
        Logger.d(TAG, "Importing questions from JSON");

        ServerQuestionsJson serverQuestionsJson = json.fromJson(
                jsonQuestionsString, ServerQuestionsJson.class);
        flush();
        setQuestionsVersion(serverQuestionsJson.getVersion());
        add(serverQuestionsJson.getQuestionsArrayList());
    }

}
