package com.brainydroid.daydreaming.db;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public final class Poll extends StatusModel<Poll,PollsStorage> {

    private static String TAG = "Poll";

    @Expose @Inject private ArrayList<Question> questions;
    @Expose private long notificationTimestamp;

    public static final String COL_NOTIFICATION_TIMESTAMP = "pollNotificationTimestamp";

    public static final String STATUS_PENDING = "pollPending"; // Notification has appeared
    public static final String STATUS_RUNNING = "pollRunning"; // QuestionActivity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "pollPartiallyCompleted"; // QuestionActivity was stopped, and Poll expired
    public static final String STATUS_COMPLETED = "pollCompleted"; // QuestionActivity completed

    @Inject transient PollsStorage pollsStorage;
    @Inject transient QuestionsStorage questionsStorage;

    public void populateQuestions(int nQuestions) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] populateQuestions");
        }

        questions = questionsStorage.getRandomQuestions(nQuestions);
        for (Question question : questions) {
            question.setPoll(this);
        }
    }

    public void addQuestion(Question question) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addQuestion");
        }

        question.setPoll(this);
        questions.add(question);
    }

    public ArrayList<Question> getQuestions() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getQuestions");
        }

        return questions;
    }

    public Question getQuestionByIndex(int index) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getQuestionByIndex");
        }

        return questions.get(index);
    }

    public int getLength() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLength");
        }

        return questions.size();
    }

    public long getNotificationTimestamp() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getNotificationTimestamp");
        }

        return notificationTimestamp;
    }

    public void setNotificationTimestamp(long notificationTimestamp) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setNotificationTimestamp");
        }

        this.notificationTimestamp = notificationTimestamp;
        saveIfSync();
    }

    @Override
    protected Poll self() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] self");
        }

        return this;
    }

    @Override
    protected PollsStorage getStorage() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getStorage");
        }

        return pollsStorage;
    }

}
