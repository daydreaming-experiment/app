package com.brainydroid.daydreaming.sequence;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

abstract public class BuildableOrderable<T> {

    private static String TAG = "BuildableOrderable";

    @JsonIgnore private Integer explicitPosition = null;

    abstract public String getName();

    abstract public String getPosition();

    public int getExplicitPosition() {
        if (isPositionExplicit()) {
            return explicitPosition;
        } else {
            throw new RuntimeException("Explicit position asked for, but this item's "
                    + "position is implicit");
        }
    }

    public boolean isPositionExplicit() {
        // Was our position already parsed?
        if (explicitPosition != null) {
            return true;
        }

        // If not, do it
        try {
            //noinspection ResultOfMethodCallIgnored
            explicitPosition = Integer.parseInt(getPosition());
            return true;
        } catch (Exception e) {
            // Our position did not represent an integer
            return false;
        }
    }

    abstract public T build(Sequence sequence);

    public void validateInitialization(ArrayList<T> parentArray, Class<T> classOfT) {
        // TODO: check all position.afters exist in parentArray
    }

}
