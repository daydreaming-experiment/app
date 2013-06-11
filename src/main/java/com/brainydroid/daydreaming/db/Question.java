package com.brainydroid.daydreaming.db;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

// TODO: add some way to save the phone's timezone and the user's preferences
// about what times he allowed notifications to appear at.

public class Question {

    private static String TAG = "Question";

    public static final String STATUS_ASKED = "questionAsked";
    public static final String STATUS_ASKED_DISMISSED = "questionAskedDismissed";
    public static final String STATUS_ANSWERED = "questionAnswered";

    @Inject transient Json json;

    @Expose protected String name = null;
    private String category = null;
    private String subCategory = null;
    private IQuestionDetails details = null;

    @Expose private String status = null;
    @Expose private IAnswer answer = null;
    @Expose private Location location;
    @Expose private long timestamp = -1;

    private Poll poll = null;

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        Logger.v(TAG, "Setting name");
        this.name = name;
        saveIfInSyncingPoll();
    }

    public synchronized String getCategory() {
        return category;
    }

    public synchronized void setCategory(String category) {
        Logger.v(TAG, "Setting category");
        this.category = category;
        saveIfInSyncingPoll();
    }

    public synchronized String getSubCategory() {
        return subCategory;
    }

    public synchronized void setSubCategory(String subCategory) {
        Logger.v(TAG, "Setting subCategory");
        this.subCategory = subCategory;
        saveIfInSyncingPoll();
    }

    public synchronized String getDetailsAsJson() {
        if (details != null) {
            Logger.v(TAG, "Getting details as JSON");
            return json.toJson(details);
        } else {
            Logger.v(TAG, "No details to return -> returning null");
            return null;
        }
    }

    public synchronized void setDetailsFromJson(String jsonDetails) {
        Logger.v(TAG, "Setting details from JSON");
        details = json.fromJson(jsonDetails, IQuestionDetails.class);
        saveIfInSyncingPoll();
    }

    public synchronized IQuestionDetails getDetails() {
        return details;
    }

    public synchronized void setDetails(IQuestionDetails details) {
        Logger.v(TAG, "Setting details");
        this.details = details;
        saveIfInSyncingPoll();
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        Logger.v(TAG, "Setting status");
        this.status = status;
        saveIfInSyncingPoll();
    }

    public synchronized String getAnswerAsJson() {
        if (answer != null) {
            Logger.v(TAG, "Getting answer as JSON");
            return json.toJson(answer);
        } else {
            Logger.v(TAG, "No answer to return -> returning null");
            return null;
        }
    }

    public synchronized void setAnswerFromJson(String jsonAnswer) {
        Logger.v(TAG, "Setting answer from JSON");
        answer = json.fromJson(jsonAnswer, IAnswer.class);
        saveIfInSyncingPoll();
    }

    public synchronized IAnswer getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        saveIfInSyncingPoll();
    }

    public synchronized String getLocationAsJson() {
        if (location != null) {
            Logger.v(TAG, "Getting location as JSON");
            return json.toJson(location);
        } else {
            Logger.v(TAG, "No location to return -> returning null");
            return null;
        }
    }

    public synchronized void setLocationFromJson(String jsonLocation) {
        Logger.v(TAG, "Setting location from JSON");
        this.location = json.fromJson(jsonLocation, Location.class);
        saveIfInSyncingPoll();
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setLocation(Location location) {
        Logger.v(TAG, "Setting location");
        this.location = location;
        saveIfInSyncingPoll();
    }

    public synchronized long getTimestamp() {
        return timestamp;
    }

    public synchronized void setTimestamp(long timestamp) {
        Logger.v(TAG, "Setting timestamp");
        this.timestamp = timestamp;
        saveIfInSyncingPoll();
    }

    public synchronized Poll getPoll() {
        return poll;
    }

    public synchronized void setPoll(Poll poll) {
        // This method is only called from Poll.addQuestion(...), and calling
        // saveIfInSyncingPoll() would trigger an unnecessary save. So we
        // don't call it, contrary to other setters above.
        this.poll = poll;
    }

    private synchronized void saveIfInSyncingPoll() {
        if (poll != null) {
            Logger.d(TAG, "Question has a poll, saving if that poll is " +
                    "syncing");
            poll.saveIfSync();
        } else {
            Logger.v(TAG, "Question has no poll, not saving");
        }
    }

}
