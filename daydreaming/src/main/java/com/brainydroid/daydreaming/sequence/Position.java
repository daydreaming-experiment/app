package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.JsonParametersException;

import java.util.ArrayList;

public class Position {

    private static String TAG = "Position";

    private Integer fixed = null;
    private String floating = null;
    private String after = null;
    private boolean bonus = false;

    public <T extends BuildableOrderable<T>> void validateInitialization(
            ArrayList<T> parentArray, T parent, Class<T> classOfT)
            throws JsonParametersException {
        Logger.d(TAG, "Validating initialization");

        // Only one of fixed, floating, after is defined
        int nNotNull = (fixed != null ? 1 : 0) +
                (floating != null ? 1 : 0) + (after != null ? 1 : 0);
        if (nNotNull != 1) {
            throw new JsonParametersException("Only one (and exactly one) of fixed, floating, " +
                    "and after can be specified");
        }

        // If after is defined, it's not self and the name it references exists in parentArray
        if (after != null) {
            if (after.equals(parent.getName())) {
                throw new JsonParametersException("Position.after can't reference itself");
            }

            boolean nameExistsInParentArray = false;
            for (T item : parentArray) {
                if (item.getName().equals(after)) {
                    nameExistsInParentArray = true;
                    break;
                }
            }

            if (!nameExistsInParentArray) {
                throw new JsonParametersException("Position.after references a " +
                        "BuildableOrderable that couldn't be found in the parent array");
            }
        }

        // If bonus is true, parent is either a Page or a PageGroup
        if (bonus) {
            if (!classOfT.equals(Page.class) && !classOfT.equals(PageGroup.class)) {
                throw new JsonParametersException("Only a Page or a PageGroup can be bonus");
            }
        }
    }
}
