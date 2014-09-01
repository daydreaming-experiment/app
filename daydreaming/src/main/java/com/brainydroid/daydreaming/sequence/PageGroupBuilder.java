package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.OrderablePageGroupDescription;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class PageGroupBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupBuilder";

    @Inject private Orderer orderer;
    @Inject private PageBuilder pageBuilder;

    public PageGroup build(OrderablePageGroupDescription pageGroupDescription) {
        Logger.v(TAG, "Building pageGroup from description {}", pageGroupDescription.getName());

        ArrayList<PageDescription> pageDescriptions = pageGroupDescription.getPages();
        BuildableOrder<PageDescription, Page> buildableOrder =
                orderer.buildOrder(pageDescriptions, pageGroupDescription.getNSlots(), Page.class);

        return new PageGroup(buildableOrder.build());
    }

}
