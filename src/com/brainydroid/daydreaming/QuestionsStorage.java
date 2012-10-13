package com.brainydroid.daydreaming;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuestionsStorage {

	private static QuestionsStorage qsInstance = null;

	private static final String TABLE_QUESTIONS = "questions";

	private static final String SQL_CREATE_TABLE_QUESTIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONS + " (" +
					Question.COL_ID + " TEXT NOT NULL, " +
					Question.COL_CATEGORY + " TEXT NOT NULL, " +
					Question.COL_SUBCATEGORY + " TEXT, " +
					Question.COL_TYPE + " TEXT NOT NULL, " +
					Question.COL_QUESTIONS_VERSION + " INTEGER NOT NULL" +
					");";

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;
	private final Random _random;

	public static QuestionsStorage getInstance(Context context) {
		if (qsInstance == null) {
			qsInstance = new QuestionsStorage(context);
		}
		return qsInstance;
	}

	private QuestionsStorage(Context context) {
		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		rDb = storage.getWritableDatabase();
		_random = new Random(System.currentTimeMillis());
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_QUESTIONS);
	}

	public int getQuestionsVersion() {
		Cursor res = rDb.query(TABLE_QUESTIONS, new String[] {Question.COL_QUESTIONS_VERSION},
				null, null, null, null, null, "1");
		int questionsVersion = Integer.parseInt(res.getString(res.getColumnIndex(Question.COL_QUESTIONS_VERSION)));
		res.close();
		return questionsVersion;
	}

	public Question getQuestion(String questionId) {
		Cursor res = rDb.query(TABLE_QUESTIONS, new String[] {"*"}, Question.COL_ID + "='?'",
				new String[] {questionId}, null, null, null);
		if (res.getCount() == 0) {
			res.close();
			return null;
		}

		Question q = new Question(_context);
		q.setId(res.getString(res.getColumnIndex(Question.COL_ID)));
		q.setCategory(res.getString(res.getColumnIndex(Question.COL_CATEGORY)));
		q.setSubcategory(res.getString(res.getColumnIndex(Question.COL_SUBCATEGORY)));
		q.setType(res.getString(res.getColumnIndex(Question.COL_TYPE)));
		q.setQuestionsVersion(
				Integer.parseInt(res.getString(res.getColumnIndex(Question.COL_QUESTIONS_VERSION))));
		res.close();

		return q;
	}

	public ArrayList<String> getQuestionIds() {
		Cursor res = rDb.query(TABLE_QUESTIONS, new String[] {Question.COL_ID}, null, null,
				null, null, null);
		if (!res.moveToFirst())
		{
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

	public ArrayList<Question> getRandomQuestions(int nQuestions) {
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
}