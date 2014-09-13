package com.brainydroid.daydreaming.ui.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class CachingDistance implements IDistance {

    private HashMap<DoubleStringKey,Integer> cache = new HashMap<DoubleStringKey,Integer>();

    public int distance(String s, String t) {
        DoubleStringKey st = new DoubleStringKey(s ,t);
        if (!cache.containsKey(st)) {
            cache.put(st, computeDistance(s, t));
        }

        return cache.get(st);
    }

    public int metaDistance(String s, MetaString ms) {
        ArrayList<Integer> distances = new ArrayList<Integer>();

        distances.add(distance(s, ms.getLower()));
        // If we're a substring, consider it exact
        if (isSubString(s, ms.getLower()))
            return 0;
        for (String token : ms.getTokens()) {
            if (isSubString(s, token))
                return 0;
            distances.add(distance(s, token));
        }
        for (String tag : ms.getTags()) {
            if (isSubString(s, tag))
                return 0;
            distances.add(distance(s, tag));
        }
        return Collections.min(distances);
    }

    private static boolean isSubString(String s, String t) {
        return s.length() <= t.length() && s.equals(t.substring(0, s.length()));
    }

    protected abstract int computeDistance(String s, String t);
}
