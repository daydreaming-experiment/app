package com.brainydroid.daydreaming;

import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PollsStorage {

	private static PollsStorage psInstance = null;

	private static final String TABLE_POLLS = "polls";
	private static final String TABLE_POLL_QUESTIONS = "pollQuestions";

	private static final String SQL_CREATE_TABLE_POLLS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_POLLS + " (" +
					Poll.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Poll.COL_STATUS + " TEXT, " +
					Poll.COL_LOCATION_LATITUDE + " REAL, " +
					Poll.COL_LOCATION_LONGITUDE + " REAL, " +
					Poll.COL_LOCATION_ALTITUDE + " REAL, " +
					Poll.COL_LOCATION_ACCURACY + " REAL, " +
					Poll.COL_TIMESTAMP + " DATE, " +
					Poll.COL_QUESTIONS_VERSION + " INTEGER NOT NULL" +
					");";

	private static final String SQL_CREATE_TABLE_POLL_QUESTIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_POLL_QUESTIONS + " (" +
					Poll.COL_ID + " INTEGER NOT NULL" +
					Question.COL_ID + " TEXT NOT NULL, " +
					Question.COL_CATEGORY + " TEXT NOT NULL, " +
					Question.COL_SUBCATEGORY + " TEXT, " +
					Question.COL_TYPE + " TEXT NOT NULL, " +
					Question.COL_STATUS + " TEXT, " +
					Question.COL_ANSWER + " TEXT, " +
					Question.COL_LOCATION_LATITUDE + " REAL, " +
					Question.COL_LOCATION_LONGITUDE + " REAL, " +
					Question.COL_LOCATION_ALTITUDE + " REAL, " +
					Question.COL_LOCATION_ACCURACY + " REAL, " +
					Question.COL_TIMESTAMP + " REAL, " +
					Question.COL_QUESTIONS_VERSION + " INTEGER NOT NULL" +
					");";

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;

	public static PollsStorage getInstance(Context context) {
		if (psInstance == null) {
			psInstance = new PollsStorage(context);
		}
		return psInstance;
	}

	private PollsStorage(Context context) {
		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		rDb = storage.getWritableDatabase();
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_POLLS);
		wDb.execSQL(SQL_CREATE_TABLE_POLL_QUESTIONS);
	}

	private ContentValues getPollContentValues(Poll poll) {
		ContentValues pollValues = new ContentValues();
		pollValues.put(Poll.COL_LOCATION_LATITUDE, poll.getLocationLatitude());
		pollValues.put(Poll.COL_LOCATION_LONGITUDE, poll.getLocationLongitude());
		pollValues.put(Poll.COL_LOCATION_ALTITUDE, poll.getLocationAltitude());
		pollValues.put(Poll.COL_LOCATION_ACCURACY, poll.getLocationAccuracy());
		pollValues.put(Poll.COL_TIMESTAMP, poll.getTimestamp());
		pollValues.put(Poll.COL_QUESTIONS_VERSION, poll.getQuestionsVersion());
		return pollValues;
	}

	private ContentValues getPollContentValuesWithId(Poll poll) {
		ContentValues pollValues = getPollContentValues(poll);
		pollValues.put(Poll.COL_ID, poll.getId());
		return pollValues;
	}

	private ContentValues getQuestionContentValues(int pollId, Question question) {
		ContentValues qValues = new ContentValues();
		qValues.put(Poll.COL_ID, pollId);
		qValues.put(Question.COL_ID, question.getId());
		qValues.put(Question.COL_CATEGORY, question.getCategory());
		qValues.put(Question.COL_SUBCATEGORY, question.getSubcategory());
		qValues.put(Question.COL_TYPE, question.getType());
		qValues.put(Question.COL_STATUS, question.getStatus());
		qValues.put(Question.COL_ANSWER, question.getAnswer());
		qValues.put(Question.COL_LOCATION_LATITUDE, question.getLocationLatitude());
		qValues.put(Question.COL_LOCATION_LONGITUDE, question.getLocationLongitude());
		qValues.put(Question.COL_LOCATION_ALTITUDE, question.getLocationAltitude());
		qValues.put(Question.COL_LOCATION_ACCURACY, question.getLocationAccuracy());
		qValues.put(Question.COL_TIMESTAMP, question.getTimestamp());
		qValues.put(Question.COL_QUESTIONS_VERSION, question.getQuestionsVersion());
		return qValues;
	}

	public void storePollGetId(Poll poll) {
		ContentValues pollValues = getPollContentValues(poll);
		wDb.insert(TABLE_POLLS, null, pollValues);
		Cursor res = rDb.query(TABLE_POLLS, new String[] {Poll.COL_ID}, null,
				null, null, null, Poll.COL_ID + " DESC", "1");
		int pollId = res.getInt(res.getColumnIndex(Poll.COL_ID));
		res.close();
		poll.setId(pollId);

		Iterator<Question> qIterator = poll.getQuestions().iterator();
		while (qIterator.hasNext()) {
			ContentValues qValues = getQuestionContentValues(pollId, qIterator.next());
			wDb.insert(TABLE_POLL_QUESTIONS, null, qValues);
		}
	}

	public void updatePoll(Poll poll) {
		ContentValues pollValues = getPollContentValuesWithId(poll);
		int pollId = poll.getId();
		wDb.update(TABLE_POLLS, pollValues, Poll.COL_ID + "='?'",
				new String[] {Integer.toString(pollId)});

		Iterator<Question> qIterator = poll.getQuestions().iterator();
		while (qIterator.hasNext()) {
			Question q = qIterator.next();
			ContentValues qValues = getQuestionContentValues(pollId, q);
			wDb.update(TABLE_POLL_QUESTIONS, qValues,
					Poll.COL_ID + "='?' AND " + Question.COL_ID + "='?'",
					new String[] {Integer.toString(pollId), q.getId()});
		}
	}

	public Poll getPoll(int pollId) {
		Cursor res = rDb.query(TABLE_POLLS, new String[] {"*"}, Poll.COL_ID + "='?'",
				new String[] {Integer.toString(pollId)}, null, null, null);
		if (res.getCount() == 0) {
			res.close();
			return null;
		}

		Poll poll = new Poll(_context);
		poll.setId(res.getInt(res.getColumnIndex(Poll.COL_ID)));
		poll.setLocationLatitude(res.getDouble(res.getColumnIndex(Poll.COL_LOCATION_LATITUDE)));
		poll.setLocationLongitude(res.getDouble(res.getColumnIndex(Poll.COL_LOCATION_LONGITUDE)));
		poll.setLocationAltitude(res.getDouble(res.getColumnIndexOrThrow(Poll.COL_LOCATION_ALTITUDE)));
		poll.setLocationAccuracy(res.getDouble(res.getColumnIndex(Poll.COL_LOCATION_ACCURACY)));
		poll.setStatus(res.getString(res.getColumnIndex(Poll.COL_STATUS)));
		poll.setTimestamp(res.getLong(res.getColumnIndex(Poll.COL_TIMESTAMP)));
		poll.setQuestionsVersion(res.getInt(res.getColumnIndex(Poll.COL_QUESTIONS_VERSION)));
		res.close();

		Cursor qRes = rDb.query(TABLE_POLL_QUESTIONS, new String[] {"*"}, Poll.COL_ID + "='?'",
				new String[] {Integer.toString(pollId)}, null, null, null);
		if (!qRes.moveToFirst()) {
			qRes.close();
			return null;
		}

		do {
			Question q = new Question(_context);
			q.setId(qRes.getString(qRes.getColumnIndex(Question.COL_ID)));
			q.setCategory(qRes.getString(qRes.getColumnIndex(Question.COL_CATEGORY)));
			if (Question.CATEGORIES_WITH_SUBCATEGORIES.contains(q.getCategory())) {
				q.setSubcategory(qRes.getString(qRes.getColumnIndex(Question.COL_SUBCATEGORY)));
			}
			q.setType(qRes.getString(qRes.getColumnIndex(Question.COL_TYPE)));
			q.setStatus(qRes.getString(qRes.getColumnIndex(Question.COL_STATUS)));
			q.setAnswer(qRes.getString(qRes.getColumnIndex(Question.COL_ANSWER)));
			q.setLocationLatitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_LATITUDE)));
			q.setLocationLongitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_LONGITUDE)));
			q.setLocationAltitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_ALTITUDE)));
			q.setLocationAccuracy(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_ACCURACY)));
			q.setTimestamp(qRes.getLong(qRes.getColumnIndex(Question.COL_TIMESTAMP)));
			q.setQuestionsVersion(qRes.getInt(qRes.getColumnIndex(Question.COL_QUESTIONS_VERSION)));

			poll.addQuestion(q);
		} while (qRes.moveToNext());
		qRes.close();

		return poll;
	}

	public Poll popPoll(int pollId) {
		Poll poll = getPoll(pollId);

		if (poll == null) {
			return null;
		}

		removePoll(poll.getId());
		poll.clearId();
		return poll;
	}

	public void removePoll(int pollId) {
		wDb.delete(TABLE_POLLS, Poll.COL_ID + "='?'", new String[] {Integer.toString(pollId)});
		wDb.delete(TABLE_POLL_QUESTIONS, Poll.COL_ID + "='?'", new String[] {Integer.toString(pollId)});
	}
}