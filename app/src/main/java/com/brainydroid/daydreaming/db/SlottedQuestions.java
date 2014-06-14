package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.*;

public class SlottedQuestions extends ArrayList<Question> {

    public static String TAG = "SlottedQuestions";
    @Inject Util util;

    public HashMap<Integer, ArrayList<Question>> getPositionedQuestionGroups() {
        Logger.d(TAG, "Getting positioned question groups");

        HashMap<Integer, ArrayList<Question>> positionedQuestionGroups =
                new HashMap<Integer, ArrayList<Question>>();
        Integer slot;
        for (Question q : this) {
            Logger.v(TAG, "Testing question {}", q.getName());
            try {
                slot = Integer.parseInt(q.getSlot());
                Logger.v(TAG, "Question {0} is positioned at {1}",
                        q.getName(), slot);
                if (!positionedQuestionGroups.containsKey(slot)) {
                    positionedQuestionGroups.put(slot,
                            new ArrayList<Question>());
                }
                positionedQuestionGroups.get(slot).add(q);
            } catch (Exception e) {
                // Do nothing, just continue
                Logger.v(TAG, "Question {} is only grouped", q.getName());
            }
        }

        return positionedQuestionGroups;
    }

    private HashMap<String, ArrayList<Question>> getFloatingQuestionGroups() {
        Logger.d(TAG, "Getting floating question groups");

        HashMap<String, ArrayList<Question>> floatingQuestionGroups =
                new HashMap<String, ArrayList<Question>>();
        String slot;
        for (Question q : this) {
            Logger.v(TAG, "Testing question {}", q.getName());
            try {
                Integer.parseInt(q.getSlot());
                Logger.v(TAG, "Question {} is positioned", q.getName());
                // If the above statement works, just continue
            } catch (Exception e) {
                Logger.v(TAG, "Question {} is only grouped", q.getName());
                slot = q.getSlot();
                if (!floatingQuestionGroups.containsKey(slot)) {
                    floatingQuestionGroups.put(slot, new ArrayList<Question>());
                }
                floatingQuestionGroups.get(slot).add(q);
            }
        }

        return floatingQuestionGroups;
    }

    public ArrayList<ArrayList<Question>> getRandomFloatingQuestionGroups(
            int nGroups) {
        Logger.d(TAG, "Getting {} random question groups", nGroups);

        ArrayList<ArrayList<Question>> floatingQuestionGroups =
                new ArrayList<ArrayList<Question>>(
                        getFloatingQuestionGroups().values());

        return util.sample(floatingQuestionGroups, nGroups);
    }

}
