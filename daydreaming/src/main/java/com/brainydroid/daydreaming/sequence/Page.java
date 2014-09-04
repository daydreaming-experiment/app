package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    public static final String STATUS_ASKED = "pageAsked";
    public static final String STATUS_ANSWERED = "pageAnswered";

    @JsonProperty private String name = null;
    @JsonProperty private String status = null;
    @JsonProperty private Location location = null;
    @JsonProperty private long ntpTimestamp = -1;
    @JsonProperty private long systemTimestamp = -1;
    @JsonProperty private ArrayList<Question> questions = null;

    private int sequenceId = -1;

    @JsonIgnore private boolean isFirstOfSequence = false;
    @JsonIgnore private boolean isLastOfSequence = false;
    @JsonIgnore private Sequence sequenceCache = null;
    @Inject @JacksonInject @JsonIgnore private SequencesStorage sequencesStorage;

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

    public synchronized android.location.Location getLocation() {
        return location;
    }

    public synchronized void setLocation(android.location.Location location) {
        Logger.v(TAG, "Setting location");
        this.location = (Location)location;
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
