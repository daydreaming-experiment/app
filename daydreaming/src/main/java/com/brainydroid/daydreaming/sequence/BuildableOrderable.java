package com.brainydroid.daydreaming.sequence;

abstract public class BuildableOrderable<T> {

    private static String TAG = "BuildableOrderable";

    private transient Integer explicitPosition = null;

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

    abstract public T build();

}
