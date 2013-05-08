package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
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

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getName");
        }

        return name;
    }

    public void setName(String name) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setName");
        }

        this.name = name;
        saveIfInSyncingPoll();
    }

    public String getCategory() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getCategory");
        }

        return category;
    }

    public void setCategory(String category) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setCategory");
        }

        this.category = category;
        saveIfInSyncingPoll();
    }

    public String getSubCategory() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getSubCategory");
        }

        return subCategory;
    }

    public void setSubCategory(String subCategory) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setSubCategory");
        }

        this.subCategory = subCategory;
        saveIfInSyncingPoll();
    }

    public String getDetailsAsJson() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getDetailsAsJson");
        }

        if (details != null) {
            return json.toJson(details);
        } else {
            return null;
        }
    }

    public void setDetailsFromJson(String jsonDetails) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setDetailsFromJson");
        }

        details = json.fromJson(jsonDetails, IQuestionDetails.class);
        saveIfInSyncingPoll();
    }

    public IQuestionDetails getDetails() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getDetails");
        }

        return details;
    }

    public void setDetails(IQuestionDetails details) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setDetails");
        }

        this.details = details;
        saveIfInSyncingPoll();
    }

    public String getStatus() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getStatus");
        }

        return status;
    }

    public void setStatus(String status) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setStatus");
        }

        this.status = status;
        saveIfInSyncingPoll();
    }

    public String getAnswerAsJson() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getAnswerAsJson");
        }

        if (answer != null) {
            return json.toJson(answer);
        } else {
            return null;
        }
    }

    public void setAnswerFromJson(String jsonAnswer) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setAnswerFromJson");
        }

        answer = json.fromJson(jsonAnswer, IAnswer.class);
        saveIfInSyncingPoll();
    }

    public IAnswer getAnswer() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getAnswer");
        }

        return answer;
    }

    public void setAnswer(IAnswer answer) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setAnswer");
        }

        this.answer = answer;
        saveIfInSyncingPoll();
    }

    public String getLocationAsJson() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getLocationAsJson");
        }

        return json.toJson(location);
    }

    public void setLocationFromJson(String jsonLocation) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] setLocation");
        }

        this.location = json.fromJson(jsonLocation, Location.class);
        saveIfInSyncingPoll();
    }

    public Location getLocation() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getLocation");
        }

        return location;
    }

    public void setLocation(Location location) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setLocation");
        }

        this.location = location;
        saveIfInSyncingPoll();
    }

    public long getTimestamp() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getTimestamp");
        }

        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setTimestamp");
        }

        this.timestamp = timestamp;
        saveIfInSyncingPoll();
    }

    public Poll getPoll() {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] getPoll");
        }

        return poll;
    }

    public void setPoll(Poll poll) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] setPoll");
        }

        // This method is only called from Poll.addQuestion(...), and calling
        // saveIfInSyncingPoll() would trigger an unnecessary save. So we
        // don't call it, contrary to other setters above.
        this.poll = poll;
    }

    private void saveIfInSyncingPoll() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] saveIfInSyncingPoll");
        }

        if (poll != null) {
            poll.saveIfSync();
        }
    }

}
