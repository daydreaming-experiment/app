package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class PageGroupBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupBuilder";

    @Inject private PageBuilder pageBuilder;

    public PageGroup build(PageGroupDescription pageGroupDescription, Probe probe) {
        Logger.v(TAG, "Building pageGroup from description {}", pageGroupDescription.getName());

        Orderer<PageDescription,Page> orderer =
                new Orderer<PageDescription, Page>(pageGroupDescription.getNSlots());
        ArrayList<PageDescription> pageDescriptions = pageGroupDescription.getPages();
        BuildableOrder<PageDescription,Page> buildableOrder = orderer.buildOrder(pageDescriptions);

        return new PageGroup(buildableOrder.build(probe));
    }

}
