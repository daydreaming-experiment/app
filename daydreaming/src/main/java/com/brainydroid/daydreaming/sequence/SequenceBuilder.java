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

    public static String SEQUENCE_PROBE = "probe";

    @Inject private ParametersStorage parametersStorage;
    @Inject private PageGroupBuilder pageGroupBuilder;

    public Sequence build(SequenceDescription sequenceDescription) {
        Logger.v(TAG, "Building sequence from description {}", sequenceDescription.getName());

        Orderer<PageGroupDescription,PageGroup> orderer =
                new Orderer<PageGroupDescription, PageGroup>(sequenceDescription.getNSlots());
        ArrayList<PageGroupDescription> pageGroupDescriptions = sequenceDescription.getPageGroups();
        BuildableOrder<PageGroupDescription,PageGroup> buildableOrder =
                orderer.buildOrder(pageGroupDescriptions);

        Sequence sequence = new Sequence();
        sequence.setPageGroups(buildableOrder.build(sequence));
        return sequence;
    }

    public Sequence build(String name) {
        return build(parametersStorage.getSequenceDescription(name));
    }

}
