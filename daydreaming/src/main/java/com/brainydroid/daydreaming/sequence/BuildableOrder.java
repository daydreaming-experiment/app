package com.brainydroid.daydreaming.sequence;

import com.google.inject.Inject;

import java.util.ArrayList;

public class BuildableOrder<D extends BuildableOrderable<C>,C> {

    private static String TAG = "Order";

    @Inject private ArrayList<D> map;

    public BuildableOrder(ArrayList<ArrayList<D>> deepMap) {
        for (ArrayList<D> group : deepMap) {
            for (D item : group) {
                map.add(item);
            }
        }
    }

    public ArrayList<C> build(Sequence sequence) {
        ArrayList<C> builtOrderables = new ArrayList<C>(map.size());
        for (D item : map) {
            builtOrderables.add(item.build(sequence));
        }

        return builtOrderables;
    }
}
