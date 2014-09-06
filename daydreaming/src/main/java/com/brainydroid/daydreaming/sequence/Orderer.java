package com.brainydroid.daydreaming.sequence;

import android.annotation.SuppressLint;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Util;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Orderer<D extends BuildableOrderable<C>,C> {

    private static String TAG = "Orderer";

    @Inject Util util;
    @Inject BuildableOrder<D,C> buildableOrder;
    private int nSlots;

    public BuildableOrder<D,C> buildOrder(int nSlots, ArrayList<D> descriptions) {

        this.nSlots = nSlots;
        @SuppressLint("UseSparseArrays")
        HashMap<Integer,ArrayList<D>> map = new HashMap<Integer,ArrayList<D>>();

        // Place explicitly positioned groups
        putExplicits(getExplicits(descriptions), map);

        // Find which indices we still need to fill up
        ArrayList<Integer> remainingIndices = getRemainingIndices(map);
        int nFloatingToFill = remainingIndices.size();
        Logger.v(TAG, "Still have {} slots to fill", nFloatingToFill);

        // Get and place as many floating groups as there remain free slots
        putFloats(getRandomFloats(descriptions, nFloatingToFill), remainingIndices, map);

        // Shuffle groups internally, and build the resulting order
        buildableOrder.initialize(shuffleGroups(map));
        return buildableOrder;
    }

    private ArrayList<ArrayList<D>> shuffleGroups(HashMap<Integer,ArrayList<D>> map) {
        ArrayList<ArrayList<D>> shuffledGroups = new ArrayList<ArrayList<D>>(nSlots);
        ArrayList<D> currentGroup;
        for (Map.Entry<Integer, ArrayList<D>> group : map.entrySet()) {
            Logger.v(TAG, "Shuffling slot {}", group.getKey());
            currentGroup = group.getValue();
            util.shuffle(currentGroup);
            shuffledGroups.add(group.getKey(), currentGroup);
        }

        return shuffledGroups;
    }

    private void putExplicits(HashMap<Integer,ArrayList<D>> explicits,
                              HashMap<Integer,ArrayList<D>> map) {
        int originalIndex;
        int convertedIndex;
        for (Map.Entry<Integer, ArrayList<D>> explicitGroup : explicits.entrySet()) {
            originalIndex = explicitGroup.getKey();
            convertedIndex = originalIndex < 0 ?
                    nSlots + originalIndex : originalIndex;
            map.put(convertedIndex, explicitGroup.getValue());
        }
    }

    private HashMap<Integer,ArrayList<D>> getExplicits(ArrayList<D> orderables) {
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, ArrayList<D>> explicits = new HashMap<Integer, ArrayList<D>>();
        Integer position;

        for (D item : orderables) {
            if (item.isPositionFixed()) {
                position = item.getFixedPosition();
                if (!explicits.containsKey(position)) {
                    explicits.put(position, new ArrayList<D>());
                }
                explicits.get(position).add(item);
            }
        }

        return explicits;
    }

    private void putFloats(ArrayList<ArrayList<D>> floats,
                           ArrayList<Integer> availablePositions,
                           HashMap<Integer,ArrayList<D>> map) {
        int n = floats.size();
        for (int i = 0; i < n; i++) {
            Logger.v(TAG, "Putting group {0} at slot {1}",
                    floats.get(i).get(0).getPosition(),
                    availablePositions.get(i));
            map.put(availablePositions.get(i), floats.get(i));
        }
    }

    private ArrayList<ArrayList<D>> getRandomFloats(ArrayList<D> orderables, int nFloats) {
        HashMap<String, ArrayList<D>> floats = new HashMap<String, ArrayList<D>>();
        String position;

        for (D item : orderables) {
            if (!item.isPositionFixed()) {
                position = item.getPosition();
                if (!floats.containsKey(position)) {
                    floats.put(position, new ArrayList<D>());
                }
                floats.get(position).add(item);
            }
        }

        return util.sample(new ArrayList<ArrayList<D>>(floats.values()), nFloats);
    }


    private ArrayList<Integer> getRemainingIndices(HashMap<Integer, ArrayList<D>> map) {
        ArrayList<Integer> remainingIndices = new ArrayList<Integer>();
        for (int i = 0; i < nSlots; i++) {
            if (!map.containsKey(i)) {
                remainingIndices.add(i);
            }
        }

        return remainingIndices;
    }
}
