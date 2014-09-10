package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.sequence.BuildableOrderable;
import com.brainydroid.daydreaming.sequence.IPageGroup;
import com.brainydroid.daydreaming.sequence.Page;
import com.brainydroid.daydreaming.sequence.PageGroup;
import com.brainydroid.daydreaming.sequence.PageGroupBuilder;
import com.brainydroid.daydreaming.sequence.Position;
import com.brainydroid.daydreaming.sequence.Sequence;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;

import java.util.ArrayList;

public class PageGroupDescription extends DescriptionArrayContainer<PageDescription,Page>
        implements BuildableOrderable<PageGroupDescription,PageGroup>,
        IPageGroup {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "PageGroupDescription";

    @JsonView(Views.Internal.class)
    private String name = null;
    @JsonView(Views.Internal.class)
    private String friendlyName = null;
    @JsonView(Views.Internal.class)
    private Position position = null;
    @JsonView(Views.Internal.class)
    private int nSlots = -1;
    @JsonView(Views.Internal.class)
    private ArrayList<PageDescription> pages = null;

    @Inject @JacksonInject private PageGroupBuilder pageGroupBuilder;

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Position getPosition() {
        return position;
    }

    public int getNSlots() {
        return nSlots;
    }

    public ArrayList<PageDescription> getPages() {
        return pages;
    }

    protected ArrayList<PageDescription> getContainedArray() {
        return getPages();
    }

    public void validateInitialization(ArrayList<PageGroupDescription> parentArray,
                                       ArrayList<QuestionDescription> questionsDescriptions) {
        Logger.d(TAG, "Validating initialization");

        // Check name
        if (name == null) {
            throw new JsonParametersException("name in pageGroup can't be null");
        }

        // Check friendlyName
        if (friendlyName == null) {
            throw new JsonParametersException("friendlyName in pageGroup can't be null");
        }

        // Check position
        if (position == null) {
            throw new JsonParametersException("position in pageGroup can't be null");
        }
        position.validateInitialization(parentArray, this, PageGroupDescription.class);

        validateContained(questionsDescriptions);

        // If we're bonus, no page inside can be bonus
        if (position.isBonus()) {
            for (PageDescription p : pages) {
                if (p.getPosition().isBonus()) {
                    throw new JsonParametersException("A page can't be bonus inside " +
                            "a bonus pageGroup");
                }
            }
        }

        // TODO[now]: if we're bonus, check not all siblings in parentArray are bonus (sequence only bonuses, forbidden)
    }

    @Override
    public PageGroup build(Sequence sequence) {
         return pageGroupBuilder.build(this, sequence);
    }

}
