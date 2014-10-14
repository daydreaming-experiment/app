package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.brainydroid.daydreaming.db.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;

public class PageGroup implements IPageGroup, PreLoadable {

    private static String TAG = "PageGroup";

    @JsonView(Views.Public.class)
    private String name = null;
    @JsonView(Views.Internal.class)
    private String friendlyName = null;
    @JsonView(Views.Public.class)
    private ArrayList<Page> pages = null;

    @JsonView(Views.Internal.class)
    private int NPageGroups = 0;
    @JsonView(Views.Internal.class)
    private int indexInSequence = 0;

    private boolean isPreLoaded;

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
            Logger.v(TAG, "Pre-loading");

            final ArrayList<Boolean> pagesLoaded = new ArrayList<Boolean>();
            int index = 0;
            for (Page p : pages) {
                pagesLoaded.add(false);
                final int indexFinal = index;

                PreLoadCallback onPageLoaded = new PreLoadCallback() {
                    private String TAG = "PreLoadCallback onPageLoaded";
                    @Override
                    public void onPreLoaded() {
                        Logger.v(TAG, "Page loaded");
                        pagesLoaded.set(indexFinal, true);

                        // See if all pages are loaded
                        boolean foundNotLoaded = false;
                        for (boolean loaded : pagesLoaded) {
                            if (!loaded) {
                                foundNotLoaded = true;
                                break;
                            }
                        }

                        if (!foundNotLoaded) {
                            Logger.v(TAG, "All pages loaded");
                            isPreLoaded = true;
                            if (preLoadCallback != null) {
                                Logger.v(TAG, "Calling callback");
                                preLoadCallback.onPreLoaded();
                            }
                        }
                    }
                };

                p.onPreLoaded(onPageLoaded);
                index++;
            }
        }
    }

    public synchronized void importFromPageGroupDescription(PageGroupDescription description) {
        setName(description.getName());
        setFriendlyName(description.getFriendlyName());
    }

    private synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    private synchronized void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public synchronized String getFriendlyName() {
        return friendlyName;
    }

    public synchronized void setPages(ArrayList<Page> pages) {
        Logger.v(TAG, "Setting pages");
        this.pages = pages;
    }

    public synchronized ArrayList<Page> getPages() {
        return pages;
    }

    public void setIndexInSequence(int indexInSequence) {
        this.indexInSequence = indexInSequence;
    }

    public void setNPageGroups(int NPageGroups) {
        this.NPageGroups = NPageGroups;
    }

    public int getNumberOfPages(boolean withBonus) {
        int numberOfPages = 0;
        for (Page page : pages) {
            if (withBonus) {
                numberOfPages += 1;
            } else {
                if (!page.isBonus()) {
                    numberOfPages += 1;
                }
            }
        }
        return numberOfPages;
    }
}
