package com.brainydroid.daydreaming.sequence;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    public static final String STATUS_ASKED = "pageAsked";
    public static final String STATUS_ANSWERED = "pageAnswered";

    @Inject SequencesStorage sequencesStorage;

    @Expose private String status = null;
    @Expose private Location location = null;
    @Expose private long ntpTimestamp = -1;
    @Expose private long systemTimestamp = -1;
    @Expose private ArrayList<Question> questions = null;

    private int sequenceId = -1;
    private boolean isFirstOfSequence = false;
    private boolean isLastOfSequence = false;

    private transient Sequence sequenceCache = null;

    public Page(ArrayList<Question> questions, Sequence sequence) {
        Logger.v(TAG, "Creating page from list of questions");
        this.questions = questions;
        setSequence(sequence);
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

    private void setSequence(Sequence sequence) {
        this.sequenceCache = sequence;
        this.sequenceId = sequenceCache.getId();
    }

    private Sequence getSequence() {
        if (sequenceCache == null) {
            sequenceCache = sequencesStorage.get(sequenceId);
        }
        return sequenceCache;
    }

    private boolean hasSequence() {
        return sequenceId != -1;
    }

    public boolean isFirstOfSequence() {
        return isFirstOfSequence;
    }

    public void setIsFirstOfSequence() {
        isFirstOfSequence = true;
    }

    public boolean isLastOfSequence() {
        return isLastOfSequence;
    }

    public void setIsLastOfSequence() {
        isLastOfSequence = true;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    private synchronized void saveIfSync() {
        Logger.d(TAG, "Saving if in syncing sequence");
        if (hasSequence()) {
            getSequence().saveIfSync();
        } else {
            Logger.v(TAG, "Not saved since no sequence present");
        }
    }

}
