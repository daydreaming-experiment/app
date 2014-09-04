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
        sequence.flushSaves();
        return sequence;
    }

    public Sequence buildSave(String name) {
        return buildSave(parametersStorage.getSequenceDescription(name));
    }

}
