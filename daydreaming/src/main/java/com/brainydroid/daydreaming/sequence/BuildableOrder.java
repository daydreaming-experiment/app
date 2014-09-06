package com.brainydroid.daydreaming.sequence;

import com.brainydroid.daydreaming.background.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class BuildableOrder<D extends BuildableOrderable<C>,C> {

    private static String TAG = "BuildableOrder";

    private ArrayList<D> map;
    private boolean isConsumed = false;

    public void initialize(ArrayList<ArrayList<D>> deepMap, HashMap<String,Node<D>> afters) {
        Logger.d(TAG, "Initializing");

        map = new ArrayList<D>();
        for (ArrayList<D> group : deepMap) {
            for (D item : group) {
                map.add(item);
                if (afters.containsKey(item.getName())) {
                    appendAfters(afters.get(item.getName()));
                }
            }
        }

        isConsumed = false;
    }

    public void appendAfters(Node<D> afters) {
        // First add the root item
        map.add(afters.getData());

        // Then its children with their children
        for (Node<D> child : afters.getChildren()) {
            appendAfters(child);
        }
    }

    public ArrayList<C> build(Sequence sequence) {
        Logger.d(TAG, "Building order from map");
        if (isConsumed) {
            throw new RuntimeException("This BuildableOrder has already been used to build " +
                    "an order and not reinitialized. You should reinitialize it before using " +
                    "it again.");
        }

        ArrayList<C> builtOrderables = new ArrayList<C>(map.size());
        for (D item : map) {
            builtOrderables.add(item.build(sequence));
        }

        isConsumed = true;
        map = null;
        return builtOrderables;
    }
}
