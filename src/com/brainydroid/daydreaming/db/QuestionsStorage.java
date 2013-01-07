package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;

public class QuestionsStorage {

	private static String TAG = "QuestionsStorage";

	private static QuestionsStorage qsInstance = null;

	private static final String TABLE_QUESTIONS = "questions";

	private static final String SQL_CREATE_TABLE_QUESTIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONS + " (" +
					Question.COL_ID + " TEXT NOT NULL, " +
					Question.COL_CATEGORY + " TEXT NOT NULL, " +
					Question.COL_SUBCATEGORY + " TEXT, " +
					Question.COL_TYPE + " TEXT NOT NULL, " +
					Question.COL_MAIN_TEXT + " TEXT NOT NULL, " +
					Question.COL_PARAMETERS_TEXT + " TEXT NOT NULL, " +
					Question.COL_DEFAULT_POSITION + " INTEGER NOT NULL, " +
					Question.COL_QUESTIONS_VERSION + " INTEGER NOT NULL" +
					");";

	private static final String SQL_DROP_TABLE_QUESTIONS =
			"DROP TABLE IF EXISTS " + TABLE_QUESTIONS + ";";

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;
	private final Random _random;
	private final Gson gson;

	public static synchronized QuestionsStorage getInstance(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

		if (qsInstance == null) {
			qsInstance = new QuestionsStorage(context);
		}
		return qsInstance;
	}

	// Constructor
	private QuestionsStorage(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] QuestionsStorage");
		}

		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		rDb = storage.getWritableDatabase();
		_random = new Random(System.currentTimeMillis());
		gson = new Gson();
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_QUESTIONS);
	}

	public int getQuestionsVersion() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestionsVersion");
		}

		Cursor res = rDb.query(TABLE_QUESTIONS, new String[] {Question.COL_QUESTIONS_VERSION},
				null, null, null, null, null, "1");
		if (!res.moveToFirst()) {
			res.close();
			return -1;
		}
		int questionsVersion = Integer.parseInt(res.getString(res.getColumnIndex(Question.COL_QUESTIONS_VERSION)));
		res.close();
		return questionsVersion;
	}

	// get question from id in db
	public Question getQuestion(String questionId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestion");
		}

		Cursor res = rDb.query(TABLE_QUESTIONS, null, Question.COL_ID + "=?",
				new String[] {questionId}, null, null, null);
		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		Question q = new Question(_context);
		q.setId(res.getString(res.getColumnIndex(Question.COL_ID)));
		q.setCategory(res.getString(res.getColumnIndex(Question.COL_CATEGORY)));
		q.setSubcategory(res.getString(res.getColumnIndex(Question.COL_SUBCATEGORY)));
		q.setType(res.getString(res.getColumnIndex(Question.COL_TYPE)));
		q.setMainText(res.getString(res.getColumnIndex(Question.COL_MAIN_TEXT)));
		q.setParametersText(res.getString(res.getColumnIndex(Question.COL_PARAMETERS_TEXT)));
		q.setDefaultPosition(res.getInt(res.getColumnIndex(Question.COL_DEFAULT_POSITION)));
		q.setQuestionsVersion(
				Integer.parseInt(res.getString(res.getColumnIndex(Question.COL_QUESTIONS_VERSION))));
		res.close();

		return q;
	}

	// get questions ids in questions db
	public ArrayList<String> getQuestionIds() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestionIds");
		}

		Cursor res = rDb.query(TABLE_QUESTIONS, new String[] {Question.COL_ID}, null, null,
				null, null, null);
		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		ArrayList<String> questionIds = new ArrayList<String>();
		do {
			questionIds.add(res.getString(res.getColumnIndex(Question.COL_ID)));
		} while (res.moveToNext());
		res.close();

		return questionIds;
	}

	// getRandomQuestions
	public ArrayList<Question> getRandomQuestions(int nQuestions) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getRandomQuestions");
		}

		ArrayList<String> questionIds = getQuestionIds();
		int nIds = questionIds.size();

		ArrayList<Question> randomQuestions = new ArrayList<Question>();
		int rIndex;
		for (int i = 0; i < nQuestions && i < nIds; i++) {
			rIndex = _random.nextInt(questionIds.size());
			randomQuestions.add(getQuestion(questionIds.get(rIndex)));
			questionIds.remove(rIndex);
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

	public void dropAll() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] dropAll");
		}

		wDb.execSQL(SQL_DROP_TABLE_QUESTIONS);
		qsInstance = null;
	}

	private void addQuestions(ArrayList<Question> questions) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addQuestions");
		}

		Iterator<Question> qIt = questions.iterator();

		while (qIt.hasNext()) {
			Question q = qIt.next();
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
		qValues.put(Question.COL_ID, question.getId());
		qValues.put(Question.COL_CATEGORY, question.getCategory());
		qValues.put(Question.COL_SUBCATEGORY, question.getSubcategory());
		qValues.put(Question.COL_TYPE, question.getType());
		qValues.put(Question.COL_MAIN_TEXT, question.getMainText());
		qValues.put(Question.COL_PARAMETERS_TEXT, question.getParametersText());
		qValues.put(Question.COL_DEFAULT_POSITION, question.getDefaultPosition());
		qValues.put(Question.COL_QUESTIONS_VERSION, question.getQuestionsVersion());
		return qValues;
	}

	// import questions from json file into database
	public void importQuestions(String jsonQuestionsString) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] importQuestions");
		}

		JsonQuestions jsonQuestions = gson.fromJson(jsonQuestionsString, JsonQuestions.class);
		flushAll();
		addQuestions(jsonQuestions.getQuestionsArrayList());
	}
}
