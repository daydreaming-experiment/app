package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
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
        Logger.d(TAG, "Populating poll with {0} questions", nQuestions);
        questions = questionsStorage.getRandomQuestions(nQuestions);
        for (Question question : questions) {
            question.setPoll(this);
        }
    }

    public void addQuestion(Question question) {
        Logger.d(TAG, "Adding question {0} to poll", question.getName());
        question.setPoll(this);
        questions.add(question);
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public Question getQuestionByIndex(int index) {
        return questions.get(index);
    }

    public int getLength() {
        return questions.size();
    }

    public long getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(long notificationTimestamp) {
        Logger.v(TAG, "Setting notification timestamp");
        this.notificationTimestamp = notificationTimestamp;
        saveIfSync();
    }

    @Override
    protected Poll self() {
        return this;
    }

    @Override
    protected PollsStorage getStorage() {
        return pollsStorage;
    }

}
