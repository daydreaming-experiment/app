package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PollsStorage;
import com.brainydroid.daydreaming.db.ProbeFactory;
import com.brainydroid.daydreaming.db.ProbesStorage;
import com.brainydroid.daydreaming.db.StatusModel;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

public class Probe extends StatusModel<Probe,ProbesStorage,ProbeFactory> {

    private static String TAG = "Probe";

    @Expose private Sequence sequence;
    @Expose private long notificationNtpTimestamp;
    @Expose private long notificationSystemTimestamp;

    public static final String STATUS_PENDING = "probePending"; // Notification has appeared
    public static final String STATUS_RUNNING = "probeRunning"; // ProbeActivity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "probePartiallyCompleted"; // ProbeActivity was stopped, and Probe expired
    public static final String STATUS_COMPLETED = "probeCompleted"; // ProbeActivity completed

    @Inject transient ProbesStorage probesStorage;

    public synchronized long getNotificationNtpTimestamp() {
        return notificationNtpTimestamp;
    }

    public synchronized void setNotificationNtpTimestamp(
            long notificationNtpTimestamp) {
        Logger.v(TAG, "Setting notification ntpTimestamp");
        this.notificationNtpTimestamp = notificationNtpTimestamp;
        saveIfSync();
    }

    public synchronized long getNotificationSystemTimestamp() {
        return notificationSystemTimestamp;
    }

    public synchronized void setNotificationSystemTimestamp(
            long notificationSystemTimestamp) {
        Logger.v(TAG, "Setting notification systemTimestamp");
        this.notificationSystemTimestamp = notificationSystemTimestamp;
        saveIfSync();
    }

    @Override
    protected synchronized Probe self() {
        return this;
    }

    @Override
    protected synchronized ProbesStorage getStorage() {
        return probesStorage;
    }
}
