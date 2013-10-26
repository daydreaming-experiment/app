package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Poll extends StatusModel<Poll,PollsStorage> {

    private static String TAG = "Poll";

    @Expose @Inject private ArrayList<Question> questions;
    @Expose private long notificationNtpTimestamp;
    @Expose private long notificationSystemTimestamp;

    public static final String STATUS_PENDING = "pollPending"; // Notification has appeared
    public static final String STATUS_RUNNING = "pollRunning"; // QuestionActivity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "pollPartiallyCompleted"; // QuestionActivity was stopped, and Poll expired
    public static final String STATUS_COMPLETED = "pollCompleted"; // QuestionActivity completed

    @Inject transient PollsStorage pollsStorage;
    @Inject transient QuestionsStorage questionsStorage;
    @Inject transient Util util;

    public synchronized void populateQuestions() {
        Logger.d(TAG, "Populating poll");

        int nSlots = questionsStorage.getNSlotsPerPoll();
        HashMap<Integer, ArrayList<Question>> slots =
                new HashMap<Integer, ArrayList<Question>>();

        // Get all our questions
        SlottedQuestions slottedQuestions =
                questionsStorage.getSlottedQuestions();

        // First get the positioned groups, and convert their negative indices
        HashMap<Integer, ArrayList<Question>> positionedGroups =
                slottedQuestions.getPositionedQuestionGroups();
        int originalIndex;
        int convertedIndex;
        for (Map.Entry<Integer, ArrayList<Question>> groupEntry :
                positionedGroups.entrySet()) {
            originalIndex = groupEntry.getKey();
            convertedIndex = originalIndex < 0 ?
                    nSlots + originalIndex : originalIndex;
            slots.put(convertedIndex, groupEntry.getValue());
        }

        // Find which indices we still need to fill up
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>();
        for (int i = 0; i < nSlots; i++) {
            if (!slots.containsKey(i)) {
                remainingIndices.add(i);
            }
        }

        // Get as many floating groups as there remains free slots (they're
        // already randomly ordered)
        int nFloatingToFill = remainingIndices.size();
        ArrayList<ArrayList<Question>> floatingGroups =
                slottedQuestions.getRandomFloatingQuestionGroups(
                        nFloatingToFill);
        for (int i = 0; i < nFloatingToFill; i++) {
            slots.put(remainingIndices.get(i), floatingGroups.get(i));
        }

        // Shuffle each group internally, and store in a two-level ArrayList
        ArrayList<ArrayList<Question>> slotsArray =
                new ArrayList<ArrayList<Question>>(nSlots);
        for (Map.Entry<Integer, ArrayList<Question>> groupEntry :
                slots.entrySet()) {
            slotsArray.add(groupEntry.getKey(),
                    util.shuffle(groupEntry.getValue()));
        }

        // Finally flatten the two-level ArrayList
        questions = new ArrayList<Question>();
        for (ArrayList<Question> slot : slotsArray) {
            questions.addAll(slot);
        }
    }

    public synchronized void addQuestion(Question question) {
        Logger.d(TAG, "Adding question {0} to poll", question.getName());
        question.setPoll(this);
        questions.add(question);
    }

    public synchronized ArrayList<Question> getQuestions() {
        return questions;
    }

    public synchronized Question getQuestionByIndex(int index) {
        return questions.get(index);
    }

    public synchronized int getLength() {
        return questions.size();
    }

    public synchronized long getNotificationNtpTimestamp() {
        return notificationNtpTimestamp;
    }

    public synchronized void setNotificationNtpTimestamp(
            long notificationNtpTimestamp) {
        Logger.v(TAG, "Setting notification ntpTimestamp");
        this.notificationNtpTimestamp = notificationNtpTimestamp;
        saveIfSync();
    }

    public synchronized long getNotificationSystemTimestamp() {
        return notificationSystemTimestamp;
    }

    public synchronized void setNotificationSystemTimestamp(
            long notificationSystemTimestamp) {
        Logger.v(TAG, "Setting notification systemTimestamp");
        this.notificationSystemTimestamp = notificationSystemTimestamp;
        saveIfSync();
    }

    @Override
    protected synchronized Poll self() {
        return this;
    }

    @Override
    protected synchronized PollsStorage getStorage() {
        return pollsStorage;
    }

}
