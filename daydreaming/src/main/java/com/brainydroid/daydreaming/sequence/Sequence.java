package com.brainydroid.daydreaming.sequence;

import android.util.Pair;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequenceJsonFactory;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.TypedStatusModel;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Sequence extends TypedStatusModel<Sequence,SequencesStorage,SequenceJsonFactory>
        implements ISequence {

    private static String TAG = "Sequence";

    public static String TYPE_PROBE = "probe";
    public static String TYPE_BEGIN_QUESTIONNAIRE = "begin_questionnaire";

    public static String[] AVAILABLE_TYPES = new String[] {TYPE_PROBE,TYPE_BEGIN_QUESTIONNAIRE};

    public static final String STATUS_PENDING = "pending"; // Notification has appeared
    public static final String STATUS_RUNNING = "running"; // Activity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "partiallyCompleted"; // Activity was stopped (if a probe, it expired, if a questionnaire, can be resumed)
    public static final String STATUS_COMPLETED = "completed"; // Activity completed

    public static String[] AVAILABLE_STATUSES = new String[] {STATUS_PENDING,STATUS_RUNNING,STATUS_PARTIALLY_COMPLETED,STATUS_COMPLETED};

    @JsonView(Views.Public.class)
    private String name = null;
    @JsonView(Views.Public.class)
    private String intro = null;
    @JsonView(Views.Public.class)
    private long notificationNtpTimestamp = -1;
    @JsonView(Views.Public.class)
    private long notificationSystemTimestamp = -1;
    @JsonView(Views.Public.class)
    private ArrayList<PageGroup> pageGroups = null;

    @Inject @JacksonInject private SequencesStorage sequencesStorage;

    public synchronized String getIntro() {
        return intro;
    }

    public synchronized String getName() {
        return name;
    }

    private synchronized void setName(String name) {
        this.name = name;
        saveIfSync();
    }

    private synchronized void setIntro(String intro) {
        this.intro = intro;
        saveIfSync();
    }

    public synchronized void importFromSequenceDescription(SequenceDescription description) {
        setName(description.getName());
        setType(description.getType());
        setIntro(description.getIntro());
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

    public synchronized Pair<Pair<Page,Page>,PageGroup> getRelevantPagesAndGroup() {
        Logger.d(TAG, "Getting current page");

        // Get last not answered page
        Page currentPage = null;
        Page nextPage = null;
        PageGroup currentGroup = null;
        int globalIndex = 0;
        int groupIndex = 0;
        int indexInGroup;
        int currentGlobalIndex = -1;
        int currentIndexInGroup = -1;
        int currentGroupIndex = -1;
        String status;
        for (PageGroup pg : pageGroups) {

            indexInGroup = 0;
            for (Page p : pg.getPages()) {

                status = p.getStatus();
                if (status != null && (status.equals(Page.STATUS_ANSWERED) ||
                        status.equals(Page.STATUS_BONUS_SKIPPED))) {
                    if (currentPage != null) {
                        // Oops, we have a problem
                        String msg = "Found a page with status STATUS_ANSWERED or" +
                                " STATUS_BONUS_SKIPPED after a page with different status " +
                                "(i.e. an answered page after the current one)";
                        Logger.e(TAG, msg);
                        throw new RuntimeException(msg);
                    }
                } else {
                    if (currentPage == null) {
                        // It's the first non-answered page, ergo the current page
                        currentPage = p;
                        currentGroup = pg;
                        currentGlobalIndex = globalIndex;
                        currentIndexInGroup = indexInGroup;
                        currentGroupIndex = groupIndex;
                    } else if (nextPage == null) {
                        // It's the next page
                        nextPage = p;
                    }
                }

                globalIndex++;
                indexInGroup++;
            }

            groupIndex++;
        }

        if (currentPage == null) {
            String msg = "Asked for a current page, but none found (all pages answered)";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }
        if (currentGlobalIndex == globalIndex - 1) {
            currentPage.setIsLastOfSequence();
        } else if (currentGlobalIndex == globalIndex - 2) {
            // Will not NullPointerException since we're at end-2 so we found a next page
            //noinspection ConstantConditions
            nextPage.setIsLastOfSequence();
        }
        if (currentGlobalIndex == 0) {
            currentPage.setIsFirstOfSequence();
        }
        if (currentIndexInGroup == currentGroup.getPages().size() - 1) {
            currentPage.setIsLastOfPageGroup();
        }

        PageGroup nextGroup = null;
        if (currentGroupIndex <= pageGroups.size() - 2) {
            nextGroup = pageGroups.get(currentGroupIndex + 1);
        }
        if (currentGroupIndex == pageGroups.size() - 2) {
            // Will not NullPointerException since we're at end-2 (in groups)
            // so we found a next group
            //noinspection ConstantConditions
            nextGroup.setIsLastOfSequence();
        }

        // TODO: also record if the next group is bonus and/or last

        return new Pair<Pair<Page,Page>,PageGroup>(
                new Pair<Page,Page>(currentPage, nextPage), nextGroup);
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
