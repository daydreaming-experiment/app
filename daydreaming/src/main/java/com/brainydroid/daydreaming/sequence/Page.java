package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Page implements IPage {

    private static String TAG = "Page";

    public static final String STATUS_ASKED = "pageAsked";
    public static final String STATUS_ANSWERED = "pageAnswered";
    public static final String STATUS_BONUS_SKIPPED = "pageBonusSkipped";

    @JsonView(Views.Public.class)
    private String name = null;
    @JsonView(Views.Public.class)
    private boolean bonus = false;
    @JsonView(Views.Public.class)
    private String status = null;
    @JsonView(Views.Public.class)
    private Location location = null;
    @JsonView(Views.Public.class)
    private long ntpTimestamp = -1;
    @JsonView(Views.Public.class)
    private long systemTimestamp = -1;
    @JsonView(Views.Public.class)
    private ArrayList<Question> questions = null;

    @JsonView(Views.Internal.class)
    private int sequenceId = -1;
    @JsonView(Views.Internal.class)
    private boolean isNextBonus = false;
    @JsonView(Views.Internal.class)
    private boolean isLastBeforeBonuses = false;
    @JsonView(Views.Internal.class)
    private boolean isFirstOfSequence = false;
    @JsonView(Views.Internal.class)
    private boolean isLastOfSequence = false;

    private Sequence sequenceCache = null;

    @Inject @JacksonInject private SequencesStorage sequencesStorage;

    public void importFromPageDescription(PageDescription description) {
        setName(description.getName());
        setBonus(description.getPosition().isBonus());
    }

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        this.name = name;
        saveIfSync();
    }

    public synchronized boolean isBonus() {
        return bonus;
    }

    public synchronized void setBonus(boolean bonus) {
        this.bonus = bonus;
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

    public synchronized void setLocation(android.location.Location location) {
        Logger.v(TAG, "Setting location");
        this.location = new Location(location);
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

    public void setIsNextBonus(boolean isNextBonus) {
        this.isNextBonus = isNextBonus;
        saveIfSync();
    }

    public boolean isNextBonus() {
        return isNextBonus;
    }

    public void setIsLastBeforeBonuses(boolean isLastBeforeBonuses) {
        this.isLastBeforeBonuses = isLastBeforeBonuses;
        saveIfSync();
    }

    public boolean isLastBeforeBonuses() {
        return isLastBeforeBonuses;
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
