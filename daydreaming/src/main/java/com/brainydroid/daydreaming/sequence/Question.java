package com.brainydroid.daydreaming.sequence;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.IAnswer;
import com.brainydroid.daydreaming.db.IQuestionDetails;
import com.google.gson.annotations.Expose;

public class Question extends AbstractQuestion {

    private static String TAG = "Question";

    public static final String STATUS_ASKED = "questionAsked";
    public static final String STATUS_ASKED_DISMISSED = "questionAskedDismissed";
    public static final String STATUS_ANSWERED = "questionAnswered";

    @Expose protected String name = null;
    private IQuestionDetails details = null;

    @Expose private IAnswer answer = null;
    @Expose private String status = null;
    @Expose private Location location;
    @Expose private long ntpTimestamp = -1;
    @Expose private long systemTimestamp = -1;

    private Sequence sequence = null;

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        Logger.v(TAG, "Setting name");
        this.name = name;
        save();
    }

    public synchronized IQuestionDetails getDetails() {
        return details;
    }

    private synchronized void setDetails(IQuestionDetails details) {
        Logger.v(TAG, "Setting details");
        this.details = details;
        save();
    }

    public synchronized IAnswer getAnswer() {
        return answer;
    }

    private synchronized void setAnswer(IAnswer answer) {
        Logger.v(TAG, "Setting answer");
        this.answer = answer;
        save();
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        Logger.v(TAG, "Setting status");
        this.status = status;
        save();
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setLocation(Location location) {
        Logger.v(TAG, "Setting location");
        this.location = location;
        save();
    }

    public synchronized long getNtpTimestamp() {
        return ntpTimestamp;
    }

    public synchronized void setNtpTimestamp(long ntpTimestamp) {
        Logger.v(TAG, "Setting ntpTimestamp");
        this.ntpTimestamp = ntpTimestamp;
        save();
    }

    public synchronized long getSystemTimestamp() {
        return systemTimestamp;
    }

    public synchronized void setSystemTimestamp(long systemTimestamp) {
        Logger.v(TAG, "Setting systemTimestamp");
        this.systemTimestamp = systemTimestamp;
        save();
    }

    public synchronized Sequence getSequence() {
        return sequence;
    }

    private synchronized void setSequence(Sequence sequence) {
        Logger.v(TAG, "Setting sequence");
        this.sequence = sequence;
        // FIXME[seb]: check if save() is necessary
    }

    private synchronized void save() {
        Logger.d(TAG, "Saving if sequence is persisted");
        sequence.saveIfPersisted();
    }

}
