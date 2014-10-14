package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.SequencesStorage;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

public class Page implements IPage, PreLoadable {

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

    @Inject private SequencesStorage sequencesStorage;

    @JsonView(Views.Internal.class)
    private int indexInPageGroup = 0;
    @JsonView(Views.Internal.class)
    private int nPages = 0;
    @JsonView(Views.Internal.class)
    private int indexOfParentPageGroupInSequence = 0;
    @JsonView(Views.Internal.class)
    private int nPageGroupsInSequence = 0;

    private boolean isPreLoaded = false;
    private boolean isPreLoading = false;
    @Inject private HashSet<PreLoadCallback> preLoadCallbacks;

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

                final ArrayList<Boolean> questionsLoaded = new ArrayList<Boolean>();
                int index = 0;
                for (Question q : questions) {
                    questionsLoaded.add(false);
                    final int indexFinal = index;

                    PreLoadCallback onQuestionLoaded = new PreLoadCallback() {
                        private String TAG = "PreLoadCallback onQuestionLoaded";

                        @Override
                        public void onPreLoaded() {
                            Logger.v(TAG, "Question loaded");
                            questionsLoaded.set(indexFinal, true);

                            // See if all questions are loaded
                            boolean foundNotLoaded = false;
                            for (boolean loaded : questionsLoaded) {
                                if (!loaded) {
                                    foundNotLoaded = true;
                                    break;
                                }
                            }

                            if (!foundNotLoaded) {
                                Logger.v(TAG, "All questions loaded -> calling possible callbacks");
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

                    q.onPreLoaded(onQuestionLoaded);
                    index++;
                }
            }
        }
    }

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

    public synchronized Sequence getSequence() {
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

    public void setIndexInPageGroup(int indexInPageGroup) {
        this.indexInPageGroup = indexInPageGroup;
    }

    public void setnPages(int nPages) {
        this.nPages = nPages;
    }

    public void setIndexOfParentPageGroupInSequence(int indexOfParentPageGroupInSequence) {
        this.indexOfParentPageGroupInSequence = indexOfParentPageGroupInSequence;
    }

    public void setnPageGroupsInSequence(int nPageGroupsInSequence) {
        this.nPageGroupsInSequence = nPageGroupsInSequence;
    }

}
