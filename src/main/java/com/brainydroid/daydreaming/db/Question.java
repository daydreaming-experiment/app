package com.brainydroid.daydreaming.db;

import android.location.Location;
import com.brainydroid.daydreaming.background.Logger;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

// TODO: add some way to save the phone's timezone and the user's preferences
// about what times he allowed notifications to appear at.

public class Question {

    private static String TAG = "Question";

    public static final String COL_NAME = "questionName";
    public static final String COL_CATEGORY = "questionCategory";
    public static final String COL_SUB_CATEGORY = "questionSubCategory";
    public static final String COL_DETAILS = "questionDetails";

    public static final String COL_STATUS = "questionStatus";
    public static final String COL_ANSWER = "questionAnswer";
    public static final String COL_LOCATION = "questionLocation";
    public static final String COL_TIMESTAMP = "questionTimestamp";

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Logger.v(TAG, "Setting name");
        this.name = name;
        saveIfInSyncingPoll();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        Logger.v(TAG, "Setting category");
        this.category = category;
        saveIfInSyncingPoll();
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        Logger.v(TAG, "Setting subCategory");
        this.subCategory = subCategory;
        saveIfInSyncingPoll();
    }

    public String getDetailsAsJson() {
        if (details != null) {
            Logger.v(TAG, "Getting details as JSON");
            return json.toJson(details);
        } else {
            Logger.w(TAG, "No details to return -> returning null");
            return null;
        }
    }

    public void setDetailsFromJson(String jsonDetails) {
        Logger.v(TAG, "Setting details from JSON");
        details = json.fromJson(jsonDetails, IQuestionDetails.class);
        saveIfInSyncingPoll();
    }

    public IQuestionDetails getDetails() {
        return details;
    }

    public void setDetails(IQuestionDetails details) {
        Logger.v(TAG, "Setting details");
        this.details = details;
        saveIfInSyncingPoll();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        Logger.v(TAG, "Setting status");
        this.status = status;
        saveIfInSyncingPoll();
    }

    public String getAnswerAsJson() {
        if (answer != null) {
            Logger.v(TAG, "Getting answer as JSON");
            return json.toJson(answer);
        } else {
            Logger.w(TAG, "No answer to return -> returning null");
            return null;
        }
    }

    public void setAnswerFromJson(String jsonAnswer) {
        Logger.v(TAG, "Setting answer from JSON");
        answer = json.fromJson(jsonAnswer, IAnswer.class);
        saveIfInSyncingPoll();
    }

    public IAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        saveIfInSyncingPoll();
    }

    public String getLocationAsJson() {
        if (location != null) {
            Logger.v(TAG, "Getting location as JSON");
            return json.toJson(location);
        } else {
            Logger.w(TAG, "No location to return -> returning null");
            return null;
        }
    }

    public void setLocationFromJson(String jsonLocation) {
        Logger.v(TAG, "Setting location from JSON");
        this.location = json.fromJson(jsonLocation, Location.class);
        saveIfInSyncingPoll();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        Logger.v(TAG, "Setting location");
        this.location = location;
        saveIfInSyncingPoll();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        Logger.v(TAG, "Setting timestamp");
        this.timestamp = timestamp;
        saveIfInSyncingPoll();
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        // This method is only called from Poll.addQuestion(...), and calling
        // saveIfInSyncingPoll() would trigger an unnecessary save. So we
        // don't call it, contrary to other setters above.
        this.poll = poll;
    }

    private void saveIfInSyncingPoll() {
        if (poll != null) {
            Logger.d(TAG, "Question has a poll, saving if that poll is " +
                    "syncing");
            poll.saveIfSync();
        } else {
            Logger.v(TAG, "Question has no poll, not saving");
        }
    }

}
