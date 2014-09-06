package com.brainydroid.daydreaming.sequence;

import java.util.ArrayList;

abstract public class BuildableOrderable<T> {

    private static String TAG = "BuildableOrderable";

    abstract public String getName();

    abstract public Position getPosition();

    public int getFixedPosition() {
        if (isPositionFixed()) {
            return getPosition().getFixedPosition();
        } else {
            throw new RuntimeException("Fixed position asked for, but this item's "
                    + "position is floating");
        }
    }

    public boolean isPositionFixed() {
        return getPosition().isFixed();
    }

    abstract public T build(Sequence sequence);

}
