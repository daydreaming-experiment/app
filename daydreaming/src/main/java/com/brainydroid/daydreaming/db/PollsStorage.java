package com.brainydroid.daydreaming.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public final class PollsStorage extends StatusModelStorage<Poll,
        PollsStorage> {

    private static String TAG = "PollsStorage";

    public static final String COL_NOTIFICATION_NTP_TIMESTAMP =
            "pollNotificationNtpTimestamp";
    public static final String COL_NOTIFICATION_SYSTEM_TIMESTAMP =
            "pollNotificationSystemTimestamp";

    private static final String TABLE_POLLS = "polls";
    private static final String TABLE_POLL_QUESTIONS = "pollQuestions";

    private static final String SQL_CREATE_TABLE_POLLS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_POLLS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_STATUS + " TEXT NOT NULL, " +
                    COL_NOTIFICATION_NTP_TIMESTAMP + " REAL, " +
                    COL_NOTIFICATION_SYSTEM_TIMESTAMP + " REAL" +
                    ");";

    private static final String SQL_CREATE_TABLE_POLL_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_POLL_QUESTIONS + " (" +
                    COL_ID + " INTEGER NOT NULL, " +
                    ParametersStorage.COL_NAME + " TEXT NOT NULL, " +
                    ParametersStorage.COL_STATUS + " TEXT, " +
                    ParametersStorage.COL_ANSWER + " TEXT, " +
                    ParametersStorage.COL_LOCATION + " TEXT, " +
                    ParametersStorage.COL_NTP_TIMESTAMP + " REAL, " +
                    ParametersStorage.COL_SYSTEM_TIMESTAMP + " REAL" +
                    ");";

    @Inject Provider<ParametersStorage> questionsStorageProvider;
    @Inject PollFactory pollFactory;

    @Inject
    public PollsStorage(Storage storage) {
        super(storage);
    }

    @Override
    protected synchronized String[] getTableCreationStrings() {
        return new String[] {SQL_CREATE_TABLE_POLLS,
                SQL_CREATE_TABLE_POLL_QUESTIONS};
    }

    @Override
    protected synchronized ContentValues getModelValues(Poll poll) {
        Logger.v(TAG, "Building poll values");

        ContentValues pollValues = super.getModelValues(poll);
        pollValues.put(COL_NOTIFICATION_NTP_TIMESTAMP,
                poll.getNotificationNtpTimestamp());
        pollValues.put(COL_NOTIFICATION_SYSTEM_TIMESTAMP,
                poll.getNotificationSystemTimestamp());
        return pollValues;
    }

    @Override
    protected synchronized String getMainTable() {
        return TABLE_POLLS;
    }

    @Override
    protected synchronized Poll create() {
        Logger.v(TAG, "Creating new poll");
        return pollFactory.create();
    }

    private synchronized ContentValues getQuestionValues(int pollId,
                                                         Question question) {
        Logger.d(TAG, "Building question values for question {0}",
                question.getName());

        ContentValues qValues = new ContentValues();
        qValues.put(COL_ID, pollId);
        qValues.put(ParametersStorage.COL_NAME, question.getName());
        qValues.put(ParametersStorage.COL_STATUS, question.getStatus());
        qValues.put(ParametersStorage.COL_ANSWER, question.getAnswerAsJson());
        qValues.put(ParametersStorage.COL_LOCATION, question.getLocationAsJson());
        qValues.put(ParametersStorage.COL_NTP_TIMESTAMP,
                question.getNtpTimestamp());
        qValues.put(ParametersStorage.COL_SYSTEM_TIMESTAMP,
                question.getSystemTimestamp());
        return qValues;
    }

    @Override
    public synchronized void store(Poll poll) {
        Logger.d(TAG, "Storing poll (and questions) to db");

        super.store(poll);
        int pollId = poll.getId();

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionValues(pollId, q);
            getDb().insert(TABLE_POLL_QUESTIONS, null, qValues);
        }
    }

    @Override
    public synchronized void update(Poll poll) {
        Logger.d(TAG, "Updating poll (and questions) in db");

        super.update(poll);
        int pollId = poll.getId();

        for (Question q : poll.getQuestions()) {
            ContentValues qValues = getQuestionValues(pollId, q);
            getDb().update(TABLE_POLL_QUESTIONS, qValues,
                    COL_ID + "=? AND " + ParametersStorage.COL_NAME + "=?",
                    new String[] {Integer.toString(pollId), q.getName()});
        }
    }

    @Override
    public synchronized void populateModel(int pollId, Poll poll, Cursor res) {
        Logger.d(TAG, "Populating poll model from db");

        super.populateModel(pollId, poll, res);
        poll.setNotificationNtpTimestamp(res.getLong(
                res.getColumnIndex(COL_NOTIFICATION_NTP_TIMESTAMP)));
        poll.setNotificationSystemTimestamp(res.getLong(
                res.getColumnIndex(COL_NOTIFICATION_SYSTEM_TIMESTAMP)));

        Cursor qRes = getDb().query(TABLE_POLL_QUESTIONS, null,
                COL_ID + "=?",
                new String[] {Integer.toString(pollId)}, null, null, null);
        if (!qRes.moveToFirst()) {
            qRes.close();
            return;
        }

        do {
            Question q = questionsStorageProvider.get().create(qRes.getString(
                    qRes.getColumnIndex(ParametersStorage.COL_NAME)));
            q.setStatus(qRes.getString(
                    qRes.getColumnIndex(ParametersStorage.COL_STATUS)));
            q.setAnswerFromJson(qRes.getString(
                    qRes.getColumnIndex(ParametersStorage.COL_ANSWER)));
            q.setLocationFromJson(qRes.getString(
                    qRes.getColumnIndex(ParametersStorage.COL_LOCATION)));
            q.setNtpTimestamp(qRes.getLong(
                    qRes.getColumnIndex(ParametersStorage.COL_NTP_TIMESTAMP)));
            q.setSystemTimestamp(qRes.getLong(
                    qRes.getColumnIndex(ParametersStorage.COL_SYSTEM_TIMESTAMP)
            ));

            poll.addQuestion(q);
        } while (qRes.moveToNext());
        qRes.close();
    }

    public synchronized ArrayList<Poll> getUploadablePolls() {
        Logger.v(TAG, "Getting uploadable polls");
        return getModelsWithStatuses(
                new String[] {Poll.STATUS_COMPLETED, Poll.STATUS_PARTIALLY_COMPLETED});
    }

    public synchronized ArrayList<Poll> getPendingPolls() {
        Logger.d(TAG, "Getting pending polls");
        return getModelsWithStatuses(new String[] {Poll.STATUS_PENDING});
    }

    @Override
    public synchronized void remove(int pollId) {
        super.remove(pollId);
        Logger.d(TAG, "Removing questions of poll {0} from db", pollId);
        getDb().delete(TABLE_POLL_QUESTIONS, COL_ID + "=?",
                new String[]{Integer.toString(pollId)});
    }

    public synchronized void removePolls(ArrayList<Poll> polls) {
        Logger.d(TAG, "Removing multiple polls");

        if (polls != null){
            for (Poll poll : polls) {
                remove(poll.getId());
            }
        }
    }

    public synchronized void removeUploadablePolls() {
        Logger.d(TAG, "Removing uploadable polls");
        removePolls(getUploadablePolls());
    }

}
