package com.brainydroid.daydreaming.sequence;

import android.location.Location;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    public static final String STATUS_ASKED = "pageAsked";
    public static final String STATUS_ANSWERED = "pageAnswered";

    @Expose private String name = null;
    @Expose private String status = null;
    @Expose private Location location = null;
    @Expose private long ntpTimestamp = -1;
    @Expose private long systemTimestamp = -1;
    @Expose private ArrayList<Question> questions = null;

    private int sequenceId = -1;

    private transient boolean isFirstOfSequence = false;
    private transient boolean isLastOfSequence = false;
    private transient Sequence sequenceCache = null;
    @Inject private transient SequencesStorage sequencesStorage;

    public void importFromPageDescription(PageDescription description) {
        setName(description.getName());
    }

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        this.name = name;
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

    public synchronized void setSequence(Sequence sequence) {
        this.sequenceCache = sequence;
        this.sequenceId = sequenceCache.getId();
        if (sequenceId == -1) {
            String msg = "Can't set sequence in a page if the sequence that has no id " +
                    "(i.e. it hasn't been saved yet)";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        saveIfSync();
    }

    private synchronized Sequence getSequence() {
        if (sequenceCache == null) {
            sequenceCache = sequencesStorage.get(sequenceId);
        }
        return sequenceCache;
    }

    private synchronized boolean hasSequence() {
        return sequenceId != -1;
    }

    public synchronized boolean isFirstOfSequence() {
        return isFirstOfSequence;
    }

    public synchronized void setIsFirstOfSequence() {
        isFirstOfSequence = true;
    }

    public synchronized boolean isLastOfSequence() {
        return isLastOfSequence;
    }

    public synchronized void setIsLastOfSequence() {
        isLastOfSequence = true;
    }

    public synchronized void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
        saveIfSync();
    }

    public synchronized ArrayList<Question> getQuestions() {
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
