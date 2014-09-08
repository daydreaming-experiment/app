package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.db.QuestionDescription;

import java.util.ArrayList;

public interface BuildableOrderable<D extends BuildableOrderable<D,C>,C> {

    public String getName();

    public Position getPosition();

    public C build(Sequence sequence);

    public void validateInitialization(ArrayList<D> parentArray,
                                       ArrayList<QuestionDescription> questionDescriptions);

}
