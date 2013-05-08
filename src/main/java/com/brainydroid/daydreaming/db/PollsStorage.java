package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

// TODO: factor most of this into Storage
@Singleton
public class PollsStorage {

    private static String TAG = "PollsStorage";

    private static final String TABLE_POLLS = "polls";
    private static final String TABLE_POLL_QUESTIONS = "pollQuestions";

    private static final String SQL_CREATE_TABLE_POLLS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_POLLS + " (" +
                    Poll.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Poll.COL_STATUS + " TEXT NOT NULL, " +
                    Poll.COL_NOTIFICATION_TIMESTAMP + " REAL" +
                    ");";

    private static final String SQL_CREATE_TABLE_POLL_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_POLL_QUESTIONS + " (" +
                    Poll.COL_ID + " INTEGER NOT NULL, " +
                    Question.COL_NAME + " TEXT NOT NULL, " +
                    Question.COL_STATUS + " TEXT, " +
                    Question.COL_ANSWER + " TEXT, " +
                    Question.COL_LOCATION + " TEXT, " +
                    Question.COL_TIMESTAMP + " REAL" +
                    ");";

    @Inject QuestionsStorage questionsStorage;
    @Inject SparseArray<Poll> pollInstances;
    @Inject PollFactory pollFactory;

    private final SQLiteDatabase rDb;
    private final SQLiteDatabase wDb;

    @Inject
    public PollsStorage(Storage storage) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] PollsStorage");
        }

        rDb = storage.getReadableDatabase();
        wDb = storage.getWritableDatabase();
        wDb.execSQL(SQL_CREATE_TABLE_POLLS); // creates db fields
        wDb.execSQL(SQL_CREATE_TABLE_POLL_QUESTIONS); // creates db fields
    }

    private ContentValues getPollContentValues(Poll poll) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPollContentValues");
        }

        ContentValues pollValues = new ContentValues();
        pollValues.put(Poll.COL_STATUS, poll.getStatus());
        pollValues.put(Poll.COL_NOTIFICATION_TIMESTAMP, poll.getNotificationTimestamp());
        return pollValues;
    }

    private ContentValues getPollContentValuesWithId(Poll poll) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPollContentValuesWithId");
        }

        ContentValues pollValues = getPollContentValues(poll);
        pollValues.put(Poll.COL_ID, poll.getId());
        return pollValues;
    }

    private ContentValues getQuestionContentValues(int pollId,
                                                   Question question) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestionContentValues");
        }

        ContentValues qValues = new ContentValues();
        qValues.put(Poll.COL_ID, pollId);
        qValues.put(Question.COL_NAME, question.getName());
        qValues.put(Question.COL_STATUS, question.getStatus());
        qValues.put(Question.COL_ANSWER, question.getAnswerAsJson());
        qValues.put(Question.COL_LOCATION, question.getLocationAsJson());
        qValues.put(Question.COL_TIMESTAMP, question.getTimestamp());
        return qValues;
    }

    public void storePollSetId(Poll poll) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] storePollSetId");
        }

        ContentValues pollValues = getPollContentValues(poll);
        wDb.insert(TABLE_POLLS, null, pollValues);

        Cursor res = rDb.query(TABLE_POLLS, new String[] {Poll.COL_ID}, null,
                null, null, null, Poll.COL_ID + " DESC", "1");
        res.moveToFirst();
        int pollId = res.getInt(res.getColumnIndex(Poll.COL_ID));
        res.close();

        poll.setId(pollId);
        pollInstances.put(pollId, poll);

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionContentValues(pollId, q);
            wDb.insert(TABLE_POLL_QUESTIONS, null, qValues);
        }
    }

    public void updatePoll(Poll poll) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] updatePoll");
        }

        ContentValues pollValues = getPollContentValuesWithId(poll);
        int pollId = poll.getId();
        wDb.update(TABLE_POLLS, pollValues, Poll.COL_ID + "=?",
                new String[] {Integer.toString(pollId)});

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionContentValues(pollId, q);
            wDb.update(TABLE_POLL_QUESTIONS, qValues,
                    Poll.COL_ID + "=? AND " + Question.COL_NAME + "=?",
                    new String[] {Integer.toString(pollId), q.getName()});
        }
    }

    public Poll getPoll(int pollId) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPoll");
        }

        Poll cachedPoll = pollInstances.get(pollId, null);
        if (cachedPoll != null) {
            return cachedPoll;
        }

        Cursor res = rDb.query(TABLE_POLLS, null, Poll.COL_ID + "=?",
                new String[] {Integer.toString(pollId)}, null, null, null);
        if (!res.moveToFirst()) {
            res.close();
            return null;
        }

        Poll poll = pollFactory.create();
        poll.setStatus(res.getString(res.getColumnIndex(Poll.COL_STATUS)));
        poll.setNotificationTimestamp(res.getLong(res.getColumnIndex(Poll.COL_NOTIFICATION_TIMESTAMP)));
        // Setting the id at the end ensures we don't save the Poll to DB again
        poll.setId(res.getInt(res.getColumnIndex(Poll.COL_ID)));
        res.close();

        Cursor qRes = rDb.query(TABLE_POLL_QUESTIONS, null, Poll.COL_ID + "=?",
                new String[] {Integer.toString(pollId)}, null, null, null);
        if (!qRes.moveToFirst()) {
            qRes.close();
            return null;
        }

        do {
            Question q = questionsStorage.getQuestion(qRes.getString(
                    qRes.getColumnIndex(Question.COL_NAME)));
            q.setStatus(qRes.getString(
                    qRes.getColumnIndex(Question.COL_STATUS)));
            q.setAnswerFromJson(qRes.getString(
                    qRes.getColumnIndex(Question.COL_ANSWER)));
            q.setLocationFromJson(qRes.getString(
                    qRes.getColumnIndex(Question.COL_LOCATION)));
            q.setTimestamp(qRes.getLong(
                    qRes.getColumnIndex(Question.COL_TIMESTAMP)));

            poll.addQuestion(q);
        } while (qRes.moveToNext());
        qRes.close();

        return poll;
    }

    public ArrayList<Poll> getUploadablePolls() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getUploadablePolls");
        }

        return getPollsWithStatuses(
                new String[] {Poll.STATUS_COMPLETED, Poll.STATUS_PARTIALLY_COMPLETED});
    }

    public ArrayList<Poll> getPendingPolls() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPendingPolls");
        }

        return getPollsWithStatuses(new String[] {Poll.STATUS_PENDING});
    }

    private ArrayList<Integer> getPollIdsWithStatuses(String[] statuses) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPollIdsWithStatuses (from String[])");
        }

        return getPollIdsWithStatuses(statuses, null);
    }

    private ArrayList<Integer> getPollIdsWithStatuses(String[] statuses, String limit) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPollIdsWithStatuses (from String[], String)");
        }

        String query = Util.multiplyString(Poll.COL_STATUS + "=?", statuses.length, " OR ");
        Cursor res = rDb.query(TABLE_POLLS, new String[] {Poll.COL_ID}, query, statuses,
                null, null, null, limit);
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
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getPollsWithStatuses");
        }

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
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removePoll");
        }

        wDb.delete(TABLE_POLLS, Poll.COL_ID + "=?", new String[]{Integer.toString(pollId)});
        wDb.delete(TABLE_POLL_QUESTIONS, Poll.COL_ID + "=?",
                new String[]{Integer.toString(pollId)});
        pollInstances.delete(pollId);
    }

    public void removePolls(ArrayList<Poll> polls) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] removePolls");
        }

        for (Poll poll : polls) {
            removePoll(poll.getId());
        }
    }

}
