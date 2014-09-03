package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequenceJsonFactory;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.TypedStatusModel;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Sequence extends TypedStatusModel<Sequence,SequencesStorage,SequenceJsonFactory>
        implements ISequence {

    private static String TAG = "Sequence";

    @Expose private String name = null;
    @Expose private long notificationNtpTimestamp = -1;
    @Expose private long notificationSystemTimestamp = -1;
    @Expose private ArrayList<PageGroup> pageGroups = null;

    public static String TYPE_PROBE = "probe";
    public static String[] AVAILABLE_TYPES = new String[] {TYPE_PROBE};

    public static final String STATUS_PENDING = "pending"; // Notification has appeared
    public static final String STATUS_RUNNING = "running"; // Activity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "partiallyCompleted"; // Activity was stopped (if a probe, it expired, if a questionnaire, can be resumed)
    public static final String STATUS_COMPLETED = "completed"; // Activity completed

    @Inject transient SequencesStorage sequencesStorage;
    private transient ArrayList<Page> allPagesCache = null;

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        this.name = name;
        saveIfSync();
    }

    public synchronized void importFromSequenceDescription(SequenceDescription description) {
        setName(description.getName());
        setType(description.getType());
    }

    public synchronized void setPageGroups(ArrayList<PageGroup> pageGroups) {
        Logger.v(TAG, "Setting pageGroups");
        this.pageGroups = pageGroups;
        saveIfSync();
    }

    public synchronized ArrayList<PageGroup> getPageGroups() {
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

    private synchronized void populateAllPagesCache() {
        Logger.v(TAG, "Populating pages cache");
        if (allPagesCache == null) {
            // Get all pages
            allPagesCache = new ArrayList<Page>();
            for (PageGroup pg : pageGroups) {
                allPagesCache.addAll(pg.getPages());
            }
        }
    }

    public synchronized Page getCurrentPage() {
        Logger.d(TAG, "Getting current page");

        populateAllPagesCache();

        // Get last not answered page
        Page current = null;
        int currentIndex = -1;
        for (Page p : allPagesCache) {
            if (p.getStatus() != null && p.getStatus().equals(Page.STATUS_ANSWERED)) {
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
