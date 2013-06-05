package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public final class PollsStorage extends StatusModelStorage<Poll,
        PollsStorage> {

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
    @Inject PollFactory pollFactory;

    @Override
    protected String[] getTableCreationStrings() {
        return new String[] {SQL_CREATE_TABLE_POLLS,
                SQL_CREATE_TABLE_POLL_QUESTIONS};
    }

    @Inject
    public PollsStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected ContentValues getModelValues(Poll poll) {
        Logger.v(TAG, "Building poll values");

        ContentValues pollValues = super.getModelValues(poll);
        pollValues.put(Poll.COL_NOTIFICATION_TIMESTAMP,
                poll.getNotificationTimestamp());
        return pollValues;
    }

    @Override
    protected String getMainTable() {
        return TABLE_POLLS;
    }

    @Override
    protected Poll create() {
        Logger.v(TAG, "Creating new poll");
        return pollFactory.create();
    }

    private ContentValues getQuestionValues(int pollId, Question question) {
        Logger.d(TAG, "Building question values for question {0}",
                question.getName());

        ContentValues qValues = new ContentValues();
        qValues.put(Poll.COL_ID, pollId);
        qValues.put(Question.COL_NAME, question.getName());
        qValues.put(Question.COL_STATUS, question.getStatus());
        qValues.put(Question.COL_ANSWER, question.getAnswerAsJson());
        qValues.put(Question.COL_LOCATION, question.getLocationAsJson());
        qValues.put(Question.COL_TIMESTAMP, question.getTimestamp());
        return qValues;
    }

    @Override
    public void store(Poll poll) {
        Logger.d(TAG, "Storing poll (and questions) to db");

        super.store(poll);
        int pollId = poll.getId();

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionValues(pollId, q);
            getDb().insert(TABLE_POLL_QUESTIONS, null, qValues);
        }
    }

    @Override
    public void update(Poll poll) {
        Logger.d(TAG, "Updating poll (and questions) in db");

        super.update(poll);
        int pollId = poll.getId();

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionValues(pollId, q);
            getDb().update(TABLE_POLL_QUESTIONS, qValues,
                    Poll.COL_ID + "=? AND " + Question.COL_NAME + "=?",
                    new String[] {Integer.toString(pollId), q.getName()});
        }
    }

    @Override
    public void populateModel(int pollId, Poll poll, Cursor res) {
        Logger.d(TAG, "Populating poll model from db");

        super.populateModel(pollId, poll, res);
        poll.setNotificationTimestamp(res.getLong(
                res.getColumnIndex(Poll.COL_NOTIFICATION_TIMESTAMP)));

        Cursor qRes = getDb().query(TABLE_POLL_QUESTIONS, null,
                Poll.COL_ID + "=?",
                new String[] {Integer.toString(pollId)}, null, null, null);
        if (!qRes.moveToFirst()) {
            qRes.close();
            return;
        }

        do {
            Question q = questionsStorage.get(qRes.getString(
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
    }

    public ArrayList<Poll> getUploadablePolls() {
        Logger.v(TAG, "Getting uploadable polls");
        return getModelsWithStatuses(
                new String[] {Poll.STATUS_COMPLETED, Poll.STATUS_PARTIALLY_COMPLETED});
    }

    public ArrayList<Poll> getPendingPolls() {
        Logger.d(TAG, "Getting pending polls");
        return getModelsWithStatuses(new String[] {Poll.STATUS_PENDING});
    }

    @Override
    public void remove(int pollId) {
        Logger.d(TAG, "Removing poll {0} from db (and questions)", pollId);
        getDb().delete(TABLE_POLL_QUESTIONS, Poll.COL_ID + "=?",
                new String[]{Integer.toString(pollId)});
    }

}
