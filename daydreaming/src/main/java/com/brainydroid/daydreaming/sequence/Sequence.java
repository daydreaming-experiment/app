package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.SequenceService;
import com.brainydroid.daydreaming.background.ErrorHandler;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.brainydroid.daydreaming.db.SequenceJsonFactory;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.TypedStatusModel;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Sequence extends TypedStatusModel<Sequence,SequencesStorage,SequenceJsonFactory>
        implements ISequence, PreLoadable {

    private static String TAG = "Sequence";

    public static String TYPE_PROBE = "probe";
    public static String TYPE_BEGIN_END_QUESTIONNAIRE = "beginEndQuestionnaire";
    public static String TYPE_BEGIN_QUESTIONNAIRE = "beginQuestionnaire";
    public static String TYPE_END_QUESTIONNAIRE = "endQuestionnaire";
    public static String TYPE_MORNING_QUESTIONNAIRE = "morningQuestionnaire";
    public static String TYPE_EVENING_QUESTIONNAIRE = "eveningQuestionnaire";

    public static String END_PREFIX = "end";
    public static String BEGIN_PREFIX = "begin";

    public static String[] AVAILABLE_REAL_TYPES = new String[] {
            TYPE_PROBE, TYPE_BEGIN_QUESTIONNAIRE, TYPE_END_QUESTIONNAIRE,
            TYPE_MORNING_QUESTIONNAIRE, TYPE_EVENING_QUESTIONNAIRE};

    /* Probes/Questionnaires: notification has appeared */
    public static final String STATUS_PENDING = "pending";
    /* Probes: notification was dismissed, and probe not yet re-suggested nor dropped */
    public static final String STATUS_RECENTLY_DISMISSED = "recentlyDismissed";
    /* Probes: notification died away, and probe not yet re-suggested nor dropped */
    public static final String STATUS_RECENTLY_MISSED = "recentlyMissed";
    /* Probes/Questionnaires: activity is running */
    public static final String STATUS_RUNNING = "running";
    /**
     * Probes/Questionnaires: Activity was stopped in the middle of the sequence,
     * and probe not yet re-suggested nor dropped
     */
    public static final String STATUS_RECENTLY_PARTIALLY_COMPLETED = "recentlyPartiallyCompleted";
    /**
     * Probes: was re-suggested after miss/dismiss/partialCompletion, and was refused.
     * Or, dropped after too long missed/dismissed/partialCompletion.
     */
    public static final String STATUS_MISSED_OR_DISMISSED_OR_INCOMPLETE = "missedOrDismissedOrIncomplete";
    /* Probes/Questionnaires: sequence completely answered */
    public static final String STATUS_COMPLETED = "completed";
    /* Questionnaires: sequence completely answered, uploaded, and must be kept locally */
    public static final String STATUS_UPLOADED_AND_KEEP = "uploadedAndKeep";

    public static HashSet<String> PAUSED_STATUSES = new HashSet<String>(Arrays.asList(new String[]{
            STATUS_RECENTLY_DISMISSED,
            STATUS_RECENTLY_MISSED,
            STATUS_RECENTLY_PARTIALLY_COMPLETED}));

    public static long EXPIRY_DELAY = 3 * 60 * 1000;  // 3 minutes

    @JsonView(Views.Public.class)
    private String name = null;
    @SuppressWarnings("UnusedDeclaration")
    @JsonView(Views.Public.class)
    private long notificationNtpTimestamp = -1;
    @JsonView(Views.Public.class)
    private long notificationSystemTimestamp = -1;
    @JsonView(Views.Public.class)
    private ArrayList<PageGroup> pageGroups = null;

    @JsonView(Views.Internal.class)
    private String intro = null;
    @JsonView(Views.Internal.class)
    private boolean showProgressHeader = true;
    @JsonView(Views.Internal.class)
    private boolean skipBonuses = true;
    @JsonView(Views.Internal.class)
    private boolean skipBonusesAsked = false;
    @JsonView(Views.Public.class)
    private boolean selfInitiated = false;
    @JsonView(Views.Public.class)
    private boolean wasMissedOrDismissedOrPaused = false;

    @Inject private SequencesStorage sequencesStorage;
    @Inject private ErrorHandler errorHandler;

    private boolean isPreLoaded = false;
    private boolean isPreLoading = false;
    @Inject private HashSet<PreLoadCallback> preLoadCallbacks;

    public static int getRecurrentRequestCode(String sequenceType, String action) {
        if (sequenceType.equals(TYPE_PROBE)) {
            if (action != null) {
                if (action.equals(SequenceService.EXPIRE_PROBE)) {
                    return 0;
                } else if (action.equals(SequenceService.DISMISS_PROBE)) {
                    return 1;
                } else {
                    throw new RuntimeException("Asked for request code for type probe and action "
                            + action + " but we don't have one");
                }
            } else {
                return 2;
            }
        } else if (sequenceType.equals(TYPE_MORNING_QUESTIONNAIRE)) {
            return 3;
        } else if (sequenceType.equals(TYPE_EVENING_QUESTIONNAIRE)) {
            return 4;
        } else {
            throw new RuntimeException("Asked for request code for type " + sequenceType
                    + " but we don't have one.");
        }
    }

    public static int getRecurrentRequestCode(String sequenceType) {
        return getRecurrentRequestCode(sequenceType, null);
    }

    public int getRecurrentNotificationId() {
        if (type.equals(TYPE_PROBE)) {
            return getId();
        } else if (type.equals(TYPE_MORNING_QUESTIONNAIRE)) {
            return 0;
        } else if (type.equals(TYPE_EVENING_QUESTIONNAIRE)) {
            return 0;
        } else {
            throw new RuntimeException("Asked for notification id for type " + type
                    + " but we don't have one.");
        }
    }

    @Override
    public synchronized boolean isPreLoaded() {
        return isPreLoaded;
    }

    @Override
    public synchronized void onPreLoaded(final PreLoadCallback preLoadCallback) {
        if (isPreLoaded) {
            if (preLoadCallback != null) {
                Logger.v(TAG, "Already pre-loaded, calling callback");
                preLoadCallback.onPreLoaded();
            } else {
                Logger.v(TAG, "Already pre-loaded, but no callback to call");
            }
        } else {
            if (preLoadCallback != null) {
                preLoadCallbacks.add(preLoadCallback);
            }

            if (isPreLoading) {
                Logger.v(TAG, "Already pre-loading, recorded potential additional callback");
            } else {
                Logger.v(TAG, "Pre-loading");
                isPreLoading = true;

                final ArrayList<Boolean> pageGroupsLoaded = new ArrayList<Boolean>();
                int index = 0;
                for (PageGroup pg : pageGroups) {
                    pageGroupsLoaded.add(false);
                    final int indexFinal = index;

                    PreLoadCallback onPageGroupLoaded = new PreLoadCallback() {
                        private String TAG = "PreLoadCallback onPageGroupLoaded";

                        @Override
                        public void onPreLoaded() {
                            Logger.v(TAG, "PageGroup loaded");
                            pageGroupsLoaded.set(indexFinal, true);

                            // See if all page groups are loaded
                            boolean foundNotLoaded = false;
                            for (boolean loaded : pageGroupsLoaded) {
                                if (!loaded) {
                                    foundNotLoaded = true;
                                    break;
                                }
                            }

                            if (!foundNotLoaded) {
                                Logger.v(TAG, "All page groups loaded -> calling possible callbacks");
                                isPreLoaded = true;
                                isPreLoading = false;

                                // Only non-null callbacks are stored
                                for (PreLoadCallback storedCallback : preLoadCallbacks) {
                                    storedCallback.onPreLoaded();
                                }
                                preLoadCallbacks = new HashSet<PreLoadCallback>();
                            }
                        }
                    };

                    pg.onPreLoaded(onPageGroupLoaded);
                    index++;
                }
            }
        }
    }

    public synchronized String getIntro() {
        return intro;
    }

    public synchronized boolean isShowProgressHeader() {
        return showProgressHeader;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
        saveIfSync();
    }

    public synchronized void setType(String type) {
        this.type = type;
        saveIfSync();
    }

    private synchronized void setIntro(String intro) {
        this.intro = intro;
        saveIfSync();
    }

    public void setShowProgressHeader(boolean showProgressHeader) {
        this.showProgressHeader = showProgressHeader;
        saveIfSync();
    }

    public synchronized void importFromSequenceDescription(SequenceDescription description) {
        setName(description.getName());
        setType(description.getType());
        setIntro(description.getIntro());
        setShowProgressHeader(description.isShowProgressHeader());
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

    public synchronized long getNotificationSystemTimestamp() {
        return notificationSystemTimestamp;
    }

    public synchronized boolean isSelfInitiated() {
        return selfInitiated;
    }

    public synchronized void setSelfInitiated(boolean selfInitiated) {
        this.selfInitiated = selfInitiated;
    }

    @Override
    public synchronized void setStatus(String status) {
        if (PAUSED_STATUSES.contains(status)) {
            Logger.v(TAG, "Remembering that sequence went through a paused state (recently*)");
            wasMissedOrDismissedOrPaused = true;
        }
        // Save occurs in super.setStatus()
        super.setStatus(status);
    }

    public boolean wasMissedOrDismissedOrPaused() {
        return wasMissedOrDismissedOrPaused;
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

    public int getIdTicker() {
        if (type.equals(TYPE_PROBE)) {
            return R.string.probeNotification_ticker;
        } else if (type.equals(TYPE_MORNING_QUESTIONNAIRE)) {
            return R.string.mqNotification_ticker;
        } else if (type.equals(TYPE_EVENING_QUESTIONNAIRE)) {
            return R.string.eqNotification_ticker;
        }
        return -1;
    }

    public int getIdTitle() {
        if (type.equals(TYPE_PROBE)) {
            return R.string.probeNotification_title;
        } else if (type.equals(TYPE_MORNING_QUESTIONNAIRE)) {
            return R.string.mqNotification_title;
        } else if (type.equals(TYPE_EVENING_QUESTIONNAIRE)) {
            return R.string.eqNotification_title;
        }
        return -1;
    }

    public int getIdText() {
        if (type.equals(TYPE_PROBE)) {
            return R.string.probeNotification_text;
        } else if (type.equals(TYPE_MORNING_QUESTIONNAIRE)) {
            return R.string.mqNotification_text;
        } else if (type.equals(TYPE_EVENING_QUESTIONNAIRE)) {
            return R.string.eqNotification_text;
        }
        return -1;
    }

    public int getTotalPageCount(boolean withBonus) {
        int numberOfPages = 0;
        for (PageGroup pageGroup : pageGroups) {
            numberOfPages += pageGroup.getNumberOfPages(withBonus);
        }
        return numberOfPages;
    }

    public int getIndexOfPage(Page searchedPage, boolean withBonus) {
        int indexOfPage = 0;
        for (PageGroup pageGroup : pageGroups) {
            for (Page page : pageGroup.getPages()) {
                if (withBonus) {
                    indexOfPage += 1;
                } else {
                    if (!page.isBonus()) {
                        indexOfPage += 1;
                    }
                }
                if (page == searchedPage) {
                    return indexOfPage;
                }
            }
        }
        return indexOfPage;
    }

}
