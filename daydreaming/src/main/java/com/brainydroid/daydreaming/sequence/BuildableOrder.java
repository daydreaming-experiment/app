package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;

public class BuildableOrder<D extends BuildableOrderable<C>,C> {

    private static String TAG = "Order";

    @Inject private ArrayList<D> map;
    private boolean isInitialized = false;
    private boolean isBuilt = false;

    public void initialize(ArrayList<ArrayList<D>> deepMap) {
        Logger.d(TAG, "Initializing");
        if (isInitialized) {
            throw new RuntimeException("BuildableOrder already initialized");
        }

        for (ArrayList<D> group : deepMap) {
            for (D item : group) {
                map.add(item);
            }
        }

        isInitialized = true;
    }

    public ArrayList<C> build(Sequence sequence) {
        Logger.d(TAG, "Building order from sequence");
        if (isBuilt) {
            throw new RuntimeException("This BuildableOrder has already been used to build " +
                    "an order. You shouldn't reuse it.");
        }

        ArrayList<C> builtOrderables = new ArrayList<C>(map.size());
        for (D item : map) {
            builtOrderables.add(item.build(sequence));
        }

        isBuilt = true;
        return builtOrderables;
    }
}
