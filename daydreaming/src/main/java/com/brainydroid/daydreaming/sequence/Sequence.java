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
    @Expose private ArrayList<PageGroup> pageGroups;

    public static String TYPE_PROBE = "typeProbe";
    public static String[] AVAILABLE_TYPES = new String[] {TYPE_PROBE};

    public static final String STATUS_PENDING = "pending"; // Notification has appeared
    public static final String STATUS_RUNNING = "running"; // Activity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "partiallyCompleted"; // Activity was stopped (if a probe, it expired, if a questionnaire, can be resumed)
    public static final String STATUS_COMPLETED = "completed"; // Activity completed

    @Inject transient SequencesStorage sequencesStorage;
    private transient ArrayList<Page> allPagesCache = null;

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

    private void populateAllPagesCache() {
        Logger.v(TAG, "Populating pages cache");
        if (allPagesCache == null) {
            // Get all pages
            allPagesCache = new ArrayList<Page>();
            for (PageGroup pg : pageGroups) {
                allPagesCache.addAll(pg.getPages());
            }
        }
    }

    public Page getCurrentPage() {
        Logger.d(TAG, "Getting current page");

        populateAllPagesCache();

        // Get last not answered page
        Page current = null;
        int currentIndex = -1;
        for (Page p : allPagesCache) {
            if (p.getStatus().equals(Page.STATUS_ANSWERED)) {
                if (current != null) {
                    // Oops, we have a problem
                    String msg = "Found a page with status STATUS_ANSWERED after a page with "
                            + "different status (i.e. an answered page after the current one)";
                    Logger.e(TAG, msg);
                    throw new RuntimeException(msg);
                }
            } else {
                if (current == null) {
                    // It's the first non-answered page, ergo the current page
                    current = p;
                    currentIndex = allPagesCache.indexOf(current);
                }
            }
        }

        if (current == null) {
            String msg = "Asked for a current page, but none found (all pages answered";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        if (currentIndex == allPagesCache.size() - 1) {
            current.setIsLastOfSequence();
        }
        if (currentIndex == 0) {
            current.setIsFirstOfSequence();
        }

        return current;
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
