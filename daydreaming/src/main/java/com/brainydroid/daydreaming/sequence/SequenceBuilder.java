package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.PageGroupDescription;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.db.SequenceDescription;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class SequenceBuilder {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "SequenceBuilder";

    @Inject private ParametersStorage parametersStorage;
    @Inject private PageGroupBuilder pageGroupBuilder;
    @Inject private Orderer<PageGroupDescription,PageGroup> orderer;

    public Sequence build(SequenceDescription sequenceDescription) {
        Logger.v(TAG, "Building sequence from description {}", sequenceDescription.getName());

        ArrayList<PageGroupDescription> pageGroupDescriptions = sequenceDescription.getPageGroups();
        BuildableOrder<PageGroupDescription,PageGroup> buildableOrder =
                orderer.buildOrder(sequenceDescription.getNSlots(), pageGroupDescriptions);

        Sequence sequence = new Sequence();
        sequence.setPageGroups(buildableOrder.build(sequence));
        return sequence;
    }

    public Sequence build(String name) {
        return build(parametersStorage.getSequenceDescription(name));
    }

}
