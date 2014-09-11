package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageGroupBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupBuilder";

    @Inject private PageGroupFactory pageGroupFactory;
    @Inject private PageBuilder pageBuilder;
    @Inject private Orderer<PageDescription,Page> orderer;

    public PageGroup build(PageGroupDescription pageGroupDescription, Sequence sequence) {
        Logger.v(TAG, "Building pageGroup from description {}", pageGroupDescription.getName());

        ArrayList<PageDescription> pageDescriptions = pageGroupDescription.getPages();
        BuildableOrder<PageDescription,Page> buildableOrder =
                orderer.buildOrder(pageGroupDescription.getNSlots(), pageDescriptions);

        PageGroup pageGroup = pageGroupFactory.create();
        pageGroup.importFromPageGroupDescription(pageGroupDescription);
        pageGroup.setPages(buildableOrder.build(sequence));

        // If we're bonus, all contained pages are bonus
        if (pageGroupDescription.getPosition().isBonus()) {
            for (Page p : pageGroup.getPages()) {
                p.setBonus(true);
            }
        }

        return pageGroup;
    }

}
