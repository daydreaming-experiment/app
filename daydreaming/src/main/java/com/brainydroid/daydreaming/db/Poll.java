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
    @Inject transient ParametersStorage parametersStorage;
    @Inject transient Util util;

    public synchronized void populateQuestions() {
        Logger.d(TAG, "Populating poll");

        int nSlots = parametersStorage.getNSlotsPerProbe();
        HashMap<Integer, ArrayList<Question>> slots =
                new HashMap<Integer, ArrayList<Question>>();

        // Get all our questions
        SlottedQuestions slottedQuestions =
                parametersStorage.getSlottedQuestions();
        Logger.v(TAG, "Got {} questions from DB", slottedQuestions.size());

        // First get the positioned groups, and convert their negative indices
        HashMap<Integer, ArrayList<Question>> positionedGroups =
                slottedQuestions.getPositionedQuestionGroups();
        Logger.v(TAG, "Got {} positioned question groups",
                positionedGroups.size());
        int originalIndex;
        int convertedIndex;
        for (Map.Entry<Integer, ArrayList<Question>> groupEntry :
                positionedGroups.entrySet()) {
            originalIndex = groupEntry.getKey();
            convertedIndex = originalIndex < 0 ?
                    nSlots + originalIndex : originalIndex;
            Logger.v(TAG, "Placing group at slot {}", convertedIndex);
            slots.put(convertedIndex, groupEntry.getValue());
        }

        // Find which indices we still need to fill up
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>();
        for (int i = 0; i < nSlots; i++) {
            if (!slots.containsKey(i)) {
                remainingIndices.add(i);
            }
        }
        int nFloatingToFill = remainingIndices.size();
        Logger.v(TAG, "Still have {} slots to fill", nFloatingToFill);

        // Get as many floating groups as there remains free slots (they're
        // already randomly ordered)
        ArrayList<ArrayList<Question>> floatingGroups =
                slottedQuestions.getRandomFloatingQuestionGroups(
                        nFloatingToFill);
        Logger.v(TAG, "Got {} floating groups to fill the slots",
                floatingGroups.size());
        for (int i = 0; i < nFloatingToFill; i++) {
            Logger.v(TAG, "Putting group {0} at slot {1}",
                    floatingGroups.get(i).get(0).getSlot(),
                    remainingIndices.get(i));
            slots.put(remainingIndices.get(i), floatingGroups.get(i));
        }

        // Shuffle each group internally, and store in a two-level ArrayList
        ArrayList<ArrayList<Question>> slotsArray =
                new ArrayList<ArrayList<Question>>(nSlots);
        ArrayList<Question> slotQuestions;
        for (Map.Entry<Integer, ArrayList<Question>> groupEntry :
                slots.entrySet()) {
            Logger.v(TAG, "Shuffling slot {}", groupEntry.getKey());
            slotQuestions = groupEntry.getValue();
            util.shuffle(slotQuestions);
            slotsArray.add(groupEntry.getKey(), slotQuestions);
        }

        // Finally flatten the two-level ArrayList
        Logger.v(TAG, "Flattening the slots into an array");
        questions = new ArrayList<Question>();
        for (ArrayList<Question> slot : slotsArray) {
            for (Question q : slot) {
                addQuestion(q);
            }
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
