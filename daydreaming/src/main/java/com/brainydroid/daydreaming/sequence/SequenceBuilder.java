package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.google.inject.Inject;

import java.util.ArrayList;

public class SequenceBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceBuilder";

    @Inject private SequenceFactory sequenceFactory;
    @Inject private ParametersStorage parametersStorage;
    @Inject private PageGroupBuilder pageGroupBuilder;
    @Inject private Orderer<PageGroupDescription,PageGroup> orderer;

    public Sequence buildSave(SequenceDescription sequenceDescription) {
        Logger.v(TAG, "Building sequence from description {}", sequenceDescription.getName());

        ArrayList<PageGroupDescription> pageGroupDescriptions = sequenceDescription.getPageGroups();
        BuildableOrder<PageGroupDescription,PageGroup> buildableOrder =
                orderer.buildOrder(sequenceDescription.getNSlots(), pageGroupDescriptions);

        Sequence sequence = sequenceFactory.create();
        sequence.importFromSequenceDescription(sequenceDescription);
        sequence.save();
        sequence.retainSaves();
        sequence.setPageGroups(buildableOrder.build(sequence));

        // Set the isNextBonus and isLastBeforeBonuses flags on pages
        boolean isLastSeenPageBonus = false;
        boolean onlyBonusPagesSeen = true;

        // Iterate backwards through pages
        for (PageGroup pg : new ListReverser<PageGroup>(sequence.getPageGroups())) {
            for (Page p : new ListReverser<Page>(pg.getPages())) {

                // If the previously seen page was bonus, this one has isNextBonus
                p.setIsNextBonus(isLastSeenPageBonus);

                if (p.isBonus()) {
                    // For the following page going back up
                    isLastSeenPageBonus = true;
                } else {
                    // This page is non bonus, maybe it's the first we meet going back
                    p.setIsLastBeforeBonuses(onlyBonusPagesSeen);
                    onlyBonusPagesSeen = false;
                    isLastSeenPageBonus = false;
                }
            }
        }

        // Set the isFirstOfSequence and isLastOfSequence flags on pages
        int nPageGroups = sequence.getPageGroups().size();
        int nPagesLastGroup = sequence.getPageGroups().get(nPageGroups - 1).getPages().size();
        sequence.getPageGroups().get(0)
                .getPages().get(0).setIsFirstOfSequence();
        sequence.getPageGroups().get(nPageGroups - 1)
                .getPages().get(nPagesLastGroup - 1).setIsLastOfSequence();

        sequence.flushSaves();
        return sequence;
    }

    public Sequence buildSave(String name) {
        return buildSave(parametersStorage.getSequenceDescription(name));
    }

}
