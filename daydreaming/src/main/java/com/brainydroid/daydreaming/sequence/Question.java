package com.brainydroid.daydreaming.sequence;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.IQuestionDescriptionDetails;
import com.google.gson.annotations.Expose;

// TODO: add some way to saveIfSync the phone's timezone and the user's
// preferences_appSettings
// about what times he allowed notifications to appear at.
public class Question extends AbstractQuestion {

    private static String TAG = "Question";

    public static final String STATUS_ASKED = "questionAsked";
    public static final String STATUS_ASKED_DISMISSED = "questionAskedDismissed";
    public static final String STATUS_ANSWERED = "questionAnswered";

    @Expose protected String name = null;
    private IQuestionDescriptionDetails details = null;

    @Expose private IAnswer answer = null;
    @Expose private String status = null;
    @Expose private Location location;
    @Expose private long ntpTimestamp = -1;
    @Expose private long systemTimestamp = -1;

    private Probe probe = null;

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        Logger.v(TAG, "Setting name");
        this.name = name;
        saveIfSync();
    }

    public synchronized IQuestionDescriptionDetails getDetails() {
        return details;
    }

    private synchronized void setDetails(IQuestionDescriptionDetails details) {
        Logger.v(TAG, "Setting details");
        this.details = details;
        saveIfSync();
    }

    public synchronized IAnswer getAnswer() {
        return answer;
    }

    private synchronized void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        saveIfSync();
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        Logger.v(TAG, "Setting status");
        this.status = status;
        saveIfSync();
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setLocation(Location location) {
        Logger.v(TAG, "Setting location");
        this.location = location;
        saveIfSync();
    }

    public synchronized long getNtpTimestamp() {
        return ntpTimestamp;
    }

    public synchronized void setNtpTimestamp(long ntpTimestamp) {
        Logger.v(TAG, "Setting ntpTimestamp");
        this.ntpTimestamp = ntpTimestamp;
        saveIfSync();
    }

    public synchronized long getSystemTimestamp() {
        return systemTimestamp;
    }

    public synchronized void setSystemTimestamp(long systemTimestamp) {
        Logger.v(TAG, "Setting systemTimestamp");
        this.systemTimestamp = systemTimestamp;
        saveIfSync();
    }

    public synchronized Probe getProbe() {
        return probe;
    }

    private synchronized void setProbe(Probe probe) {
        Logger.v(TAG, "Setting probe");
        this.probe = probe;
        // FIXME[seb]: check if saveIfSync() is necessary
    }

    private synchronized void saveIfSync() {
        Logger.d(TAG, "Saving if probe is persisted");
        probe.saveIfSync();
    }

}
