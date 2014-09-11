package com.brainydroid.daydreaming.sequence;

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
    public static String TYPE_BEGIN_QUESTIONNAIRE = "beginQuestionnaire";

    public static String[] AVAILABLE_TYPES = new String[] {TYPE_PROBE, TYPE_BEGIN_QUESTIONNAIRE};

    public static final String STATUS_PENDING = "pending"; // Notification has appeared
    public static final String STATUS_RUNNING = "running"; // Activity is running
    public static final String STATUS_PARTIALLY_COMPLETED = "partiallyCompleted"; // Activity was stopped (if a probe, it expired, if a questionnaire, can be resumed)
    public static final String STATUS_COMPLETED = "completed"; // Activity completed

    public static String[] AVAILABLE_STATUSES = new String[] {STATUS_PENDING,STATUS_RUNNING,STATUS_PARTIALLY_COMPLETED,STATUS_COMPLETED};

    @JsonView(Views.Public.class)
    private String name = null;
    @JsonView(Views.Public.class)
    private long notificationNtpTimestamp = -1;
    @JsonView(Views.Public.class)
    private long notificationSystemTimestamp = -1;
    @JsonView(Views.Public.class)
    private ArrayList<PageGroup> pageGroups = null;

    @JsonView(Views.Internal.class)
    private String intro = null;
    @JsonView(Views.Internal.class)
    private boolean skipBonuses = true;
    @JsonView(Views.Internal.class)
    private boolean skipBonusesAsked = false;

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

    public synchronized void setSkipBonuses(boolean skip) {
        Logger.v(TAG, "Setting skipBonuses to {}", skip);
        skipBonuses = skip;
        skipBonusesAsked = true;
        saveIfSync();
    }

    public synchronized boolean isSkipBonuses() {
        return skipBonuses;
    }

    public synchronized boolean isSkipBonusesAsked() {
        return skipBonusesAsked;
    }

    public synchronized Page getCurrentPage() {
        Logger.d(TAG, "Getting current page");

        // Get last not answered page
        Page currentPage = null;
        String status;
        for (PageGroup pg : pageGroups) {

            for (Page p : pg.getPages()) {

                status = p.getStatus();
                if (status != null && (status.equals(Page.STATUS_ANSWERED) ||
                        status.equals(Page.STATUS_BONUS_SKIPPED))) {

                    // We're at a page with status answered or skipped

                    if (currentPage != null) {

                        // We already found a current page before this page! Something is wrong

                        String msg = "Found a page with status STATUS_ANSWERED or" +
                                " STATUS_BONUS_SKIPPED after the current page " +
                                "(i.e. an answered page after the current one)";
                        Logger.e(TAG, msg);
                        throw new RuntimeException(msg);
                    }

                } else {

                    if (currentPage == null) {

                        // This could be our current page

                        if (p.isBonus() && skipBonusesAsked && skipBonuses) {
                            // No it's not, this page is bonus and we're asked to skip it
                            p.setStatus(Page.STATUS_BONUS_SKIPPED);
                        } else {
                            // It's the first non-answered and non-skipped page,
                            // ergo the current page
                            currentPage = p;
                        }
                    }
                }
            }
        }

        if (currentPage == null) {
            String msg = "Asked for a current page, but none found (all pages answered or skipped)";
            Logger.e(TAG, msg);
            throw new RuntimeException(msg);
        }

        return currentPage;
    }

    public synchronized void skipRemainingBonuses() {
        Logger.v(TAG, "Skipping all remaining bonus pages");

        for (PageGroup pg : pageGroups) {

            for (Page p : pg.getPages()) {

                String status = p.getStatus();
                if (status == null || !(status.equals(Page.STATUS_ANSWERED) ||
                        status.equals(Page.STATUS_BONUS_SKIPPED))) {

                    // This page has either null status, or something else than answered or skipped

                    if (p.isBonus()) {
                        // This is one of the remaining bonus pages
                        p.setStatus(Page.STATUS_BONUS_SKIPPED);
                    } else {
                        // We have a problem: there should be only bonus pages here
                        // (otherwise we wouldn't be skipping them all in one go)
                        String msg = "Found a non-bonus non-answered (and non-skipped) page " +
                                "while skipping remaining bonus pages. Something is wrong.";
                        Logger.e(TAG, msg);
                        throw new RuntimeException(msg);
                    }
                }
            }
        }
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
