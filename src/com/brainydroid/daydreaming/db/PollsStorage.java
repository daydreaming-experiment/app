package com.brainydroid.daydreaming.db;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

public class PollsStorage {

	private static String TAG = "PollsStorage";

	private static PollsStorage psInstance = null;

	private static final String TABLE_POLLS = "polls";
	private static final String TABLE_POLL_QUESTIONS = "pollQuestions";

	private static final String SQL_CREATE_TABLE_POLLS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_POLLS + " (" +
					Poll.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Poll.COL_STATUS + " TEXT NOT NULL, " +
					Poll.COL_QUESTIONS_VERSION + " INTEGER NOT NULL, " +
					Poll.COL_KEEP_IN_SYNC + " INTEGER DEFAULT 0" +
					");";

	private static final String SQL_CREATE_TABLE_POLL_QUESTIONS =
			"CREATE TABLE IF NOT EXISTS " + TABLE_POLL_QUESTIONS + " (" +
					Poll.COL_ID + " INTEGER NOT NULL, " +
					Question.COL_ID + " TEXT NOT NULL, " +
					Question.COL_STATUS + " TEXT, " +
					Question.COL_ANSWER + " TEXT, " +
					Question.COL_LOCATION_LATITUDE + " REAL, " +
					Question.COL_LOCATION_LONGITUDE + " REAL, " +
					Question.COL_LOCATION_ALTITUDE + " REAL, " +
					Question.COL_LOCATION_ACCURACY + " REAL, " +
					Question.COL_TIMESTAMP + " REAL" +
					");";

	private static final String SQL_DROP_TABLE_POLLS =
			"DROP TABLE IF EXISTS " + TABLE_POLLS + ";";
	private static final String SQL_DROP_TABLE_POLL_QUESTIONS =
			"DROP TABLE IF EXISTS " + TABLE_POLL_QUESTIONS + ";";

	private final Storage storage;
	private final SQLiteDatabase rDb;
	private final SQLiteDatabase wDb;
	private final Context _context;
	private final QuestionsStorage _questionsStorage;
	private final SparseArray<Poll> _pollInstances;

	public static synchronized PollsStorage getInstance(Context context) {

		// Debug
		Log.d(TAG, "[fn] getInstance");

		if (psInstance == null) {
			psInstance = new PollsStorage(context);
		}
		return psInstance;
	}

	// Constructor from context
	private PollsStorage(Context context) {

		// Debug
		Log.d(TAG, "[fn] PollsStorage");

		_context = context.getApplicationContext();
		storage = Storage.getInstance(_context);
		_questionsStorage = QuestionsStorage.getInstance(_context);
		_pollInstances = new SparseArray<Poll>();
		rDb = storage.getWritableDatabase();
		wDb = storage.getWritableDatabase();
		wDb.execSQL(SQL_CREATE_TABLE_POLLS); // creates db fields
		wDb.execSQL(SQL_CREATE_TABLE_POLL_QUESTIONS); // creates db fields
	}

	private ContentValues getPollContentValues(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] getPollContentValues");

		ContentValues pollValues = new ContentValues();
		pollValues.put(Poll.COL_STATUS, poll.getStatus());
		pollValues.put(Poll.COL_QUESTIONS_VERSION, poll.getQuestionsVersion());
		pollValues.put(Poll.COL_KEEP_IN_SYNC,
				poll.getKeepInSync() ? Poll.KEEP_IN_SYNC_ON : Poll.KEEP_IN_SYNC_OFF);
		return pollValues;
	}

	private ContentValues getPollContentValuesWithId(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] getPollContentValuesWithId");

		ContentValues pollValues = getPollContentValues(poll);
		pollValues.put(Poll.COL_ID, poll.getId());
		return pollValues;
	}

	private ContentValues getQuestionContentValues(int pollId, Question question) {

		// Debug
		Log.d(TAG, "[fn] getQuestionContentValues");

		ContentValues qValues = new ContentValues();
		qValues.put(Poll.COL_ID, pollId);
		qValues.put(Question.COL_ID, question.getId());
		qValues.put(Question.COL_STATUS, question.getStatus());
		qValues.put(Question.COL_ANSWER, question.getAnswer());
		qValues.put(Question.COL_LOCATION_LATITUDE, question.getLocationLatitude());
		qValues.put(Question.COL_LOCATION_LONGITUDE, question.getLocationLongitude());
		qValues.put(Question.COL_LOCATION_ALTITUDE, question.getLocationAltitude());
		qValues.put(Question.COL_LOCATION_ACCURACY, question.getLocationAccuracy());
		qValues.put(Question.COL_TIMESTAMP, question.getTimestamp());
		return qValues;
	}

	public void storePollGetId(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] storePollGetId");

		ContentValues pollValues = getPollContentValues(poll);
		wDb.insert(TABLE_POLLS, null, pollValues);
		Cursor res = rDb.query(TABLE_POLLS, new String[] {Poll.COL_ID}, null,
				null, null, null, Poll.COL_ID + " DESC", "1");
		res.moveToFirst();
		int pollId = res.getInt(res.getColumnIndex(Poll.COL_ID));
		res.close();
		poll.setId(pollId);
		_pollInstances.put(pollId, poll);

		Iterator<Question> qIterator = poll.getQuestions().iterator();
		while (qIterator.hasNext()) {
			ContentValues qValues = getQuestionContentValues(pollId, qIterator.next());
			wDb.insert(TABLE_POLL_QUESTIONS, null, qValues);
		}

		checkNetworkReceiver();
	}

	public void updatePoll(Poll poll) {

		// Debug
		Log.d(TAG, "[fn] updatePoll");

		ContentValues pollValues = getPollContentValuesWithId(poll);
		int pollId = poll.getId();
		wDb.update(TABLE_POLLS, pollValues, Poll.COL_ID + "=?",
				new String[] {Integer.toString(pollId)});

		Iterator<Question> qIterator = poll.getQuestions().iterator();
		while (qIterator.hasNext()) {
			Question q = qIterator.next();
			ContentValues qValues = getQuestionContentValues(pollId, q);
			wDb.update(TABLE_POLL_QUESTIONS, qValues, Poll.COL_ID + "=? AND " + Question.COL_ID + "=?",
					new String[] {Integer.toString(pollId), q.getId()});
		}

		checkNetworkReceiver();
	}

	public Poll getPoll(int pollId) {

		// Debug
		Log.d(TAG, "[fn] getPoll");

		Poll cachedPoll = _pollInstances.get(pollId, null);
		if (cachedPoll != null) {
			return cachedPoll;
		}
		Cursor res = rDb.query(TABLE_POLLS, null, Poll.COL_ID + "=?",
				new String[] {Integer.toString(pollId)}, null, null, null);
		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		Poll poll = new Poll(_context);
		poll.setId(res.getInt(res.getColumnIndex(Poll.COL_ID)));
		poll.setStatus(res.getString(res.getColumnIndex(Poll.COL_STATUS)));
		poll.setQuestionsVersion(res.getInt(res.getColumnIndex(Poll.COL_QUESTIONS_VERSION)));
		if (res.getInt(res.getColumnIndex(Poll.COL_KEEP_IN_SYNC)) == Poll.KEEP_IN_SYNC_ON) {
			poll.setKeepInSync();
		}
		res.close();

		Cursor qRes = rDb.query(TABLE_POLL_QUESTIONS, null, Poll.COL_ID + "=?",
				new String[] {Integer.toString(pollId)}, null, null, null);
		if (!qRes.moveToFirst()) {
			qRes.close();
			return null;
		}

		do {
			Question q = _questionsStorage.getQuestion(
					qRes.getString(qRes.getColumnIndex(Question.COL_ID)));
			q.setStatus(qRes.getString(qRes.getColumnIndex(Question.COL_STATUS)));
			q.setAnswer(qRes.getString(qRes.getColumnIndex(Question.COL_ANSWER)));
			q.setLocationLatitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_LATITUDE)));
			q.setLocationLongitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_LONGITUDE)));
			q.setLocationAltitude(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_ALTITUDE)));
			q.setLocationAccuracy(qRes.getDouble(qRes.getColumnIndex(Question.COL_LOCATION_ACCURACY)));
			q.setTimestamp(qRes.getLong(qRes.getColumnIndex(Question.COL_TIMESTAMP)));

			poll.addQuestion(q);
		} while (qRes.moveToNext());
		qRes.close();

		return poll;
	}

	public ArrayList<Poll> getUploadablePolls() {

		// Debug
		Log.d(TAG, "[fn] getUploadablePolls");

		return getPollsWithStatuses(
				new String[] {Poll.STATUS_COMPLETED, Poll.STATUS_PARTIALLY_COMPLETED});
	}

	public ArrayList<Poll> getPendingPolls() {

		// Debug
		Log.d(TAG, "[fn] getPendingPolls");

		return getPollsWithStatuses(new String[] {Poll.STATUS_PENDING});
	}

	public void cleanPolls() {

		// Debug
		Log.d(TAG, "[fn] cleanPolls");

		ArrayList<Integer> pollIdsToClean = getPollIdsWithStatuses(
				new String[] {Poll.STATUS_EXPIRED, Poll.STATUS_DISMISSED});

		if (pollIdsToClean != null) {
			for (int pollId : pollIdsToClean) {
				removePoll(pollId);
			}
		}
	}

	private ArrayList<Integer> getPollIdsWithStatuses(String[] statuses) {

		// Debug
		Log.d(TAG, "[fn] getPollIdsWithStatuses");

		String query = Util.multiplyString(Poll.COL_STATUS + "=?", statuses.length, " OR ");
		Cursor res = rDb.query(TABLE_POLLS, new String[] {Poll.COL_ID}, query, statuses,
				null, null, null);

		if (!res.moveToFirst()) {
			res.close();
			return null;
		}

		ArrayList<Integer> statusPollIds = new ArrayList<Integer>();
		do {
			statusPollIds.add(res.getInt(res.getColumnIndex(Poll.COL_ID)));
		} while (res.moveToNext());

		return statusPollIds;
	}

	private ArrayList<Poll> getPollsWithStatuses(String[] statuses) {

		// Debug
		Log.d(TAG, "[fn] getPollsWithStatuses");

		ArrayList<Integer> statusPollIds = getPollIdsWithStatuses(statuses);

		if (statusPollIds == null) {
			return null;
		}

		ArrayList<Poll> statusPolls = new ArrayList<Poll>();

		for (int pollId : statusPollIds) {
			statusPolls.add(getPoll(pollId));
		}

		return statusPolls;
	}

	public void removePoll(int pollId) {

		// Debug
		Log.d(TAG, "[fn] removePoll");

		wDb.delete(TABLE_POLLS, Poll.COL_ID + "=?", new String[] {Integer.toString(pollId)});
		wDb.delete(TABLE_POLL_QUESTIONS, Poll.COL_ID + "=?", new String[] {Integer.toString(pollId)});
		_pollInstances.delete(pollId);

		checkNetworkReceiver();
	}

	public void flushAll() {

		// Debug
		Log.d(TAG, "[fn] flushAll");

		wDb.delete(TABLE_POLLS, null, null);
		wDb.delete(TABLE_POLL_QUESTIONS, null, null);
		_pollInstances.clear();
	}

	public void dropAll() {

		// Debug
		Log.d(TAG, "[fn] dropAll");

		wDb.execSQL(SQL_DROP_TABLE_POLLS);
		wDb.execSQL(SQL_DROP_TABLE_POLL_QUESTIONS);
		_pollInstances.clear();
		psInstance = null;
	}

	private void checkNetworkReceiver() {

		// Debug
		Log.d(TAG, "[fn] checkNetworkReceiver");

		// TODO: if no polls are uploadable, disable NetworkReceiver. Otherwise activate it.
	}
}
