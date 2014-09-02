package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequenceFactory;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.TypedStatusModel;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Sequence extends TypedStatusModel<Sequence,SequencesStorage,SequenceFactory>
        implements ISequence {

    private static String TAG = "Sequence";

    @Expose private long notificationNtpTimestamp;
    @Expose private long notificationSystemTimestamp;
    private ArrayList<PageGroup> pageGroups;

    public static String TYPE_PROBE = "typeProbe";
    public static String[] AVAILABLE_TYPES = new String[] {TYPE_PROBE};

    public static final String STATUS_PENDING = "pending"; // Notification has appeared
    public static final String STATUS_RUNNING = "running"; // Activity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "partiallyCompleted"; // Activity was stopped (if a probe, it expired, if a questionnaire, can be resumed)
    public static final String STATUS_COMPLETED = "completed"; // Activity completed

    @Inject transient SequencesStorage sequencesStorage;

    public Sequence() {
        Logger.v(TAG, "Creating empty sequence");
    }

    public void setPageGroups(ArrayList<PageGroup> pageGroups) {
        Logger.v(TAG, "Setting pageGroups");
        this.pageGroups = pageGroups;
    }

    public ArrayList<PageGroup> getPageGroups() {
        return pageGroups;
    }

    public synchronized void setNotificationNtpTimestamp(
            long notificationNtpTimestamp) {
        Logger.v(TAG, "Setting notification ntpTimestamp");
        this.notificationNtpTimestamp = notificationNtpTimestamp;
        saveIfSync();
    }

    public synchronized void setNotificationSystemTimestamp(
            long notificationSystemTimestamp) {
        Logger.v(TAG, "Setting notification systemTimestamp");
        this.notificationSystemTimestamp = notificationSystemTimestamp;
        saveIfSync();
    }

    @Override
    protected synchronized Sequence self() {
        return this;
    }

    @Override
    protected synchronized SequencesStorage getStorage() {
        return sequencesStorage;
    }

}
