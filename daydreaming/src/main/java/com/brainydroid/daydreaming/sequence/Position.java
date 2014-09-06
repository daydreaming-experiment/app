package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.JsonParametersException;
import com.brainydroid.daydreaming.db.PageDescription;
import com.brainydroid.daydreaming.db.PageGroupDescription;

import java.util.ArrayList;

public class Position {

    private static String TAG = "Position";

    private Integer fixed = null;
    private String floating = null;
    private String after = null;
    private boolean bonus = false;

    public <D extends BuildableOrderable<C>, C> void validateInitialization(
            ArrayList<D> parentArray, D parent, Class<D> classOfD)
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
            for (D item : parentArray) {
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
            if (!classOfD.equals(PageDescription.class) &&
                    !classOfD.equals(PageGroupDescription.class)) {
                throw new JsonParametersException("Only a PageDescription or a " +
                        "PageGroupDescription can be bonus");
            }
        }
    }

    public boolean isFixed() {
        return fixed != null;
    }

    public Integer getFixedPosition() {
        return fixed;
    }
}
