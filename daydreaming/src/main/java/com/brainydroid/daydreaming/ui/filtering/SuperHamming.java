package com.brainydroid.daydreaming.ui.filtering;

public class SuperHamming extends CachingDistance implements IDistance {

    private static String TAG = "SuperHamming";

    @Override
    protected int computeDistance(String s, String t) {
        String shortest, longest;
        if (s.length() <= t.length()) {
            shortest = s;
            longest = t;
        } else {
            shortest = t;
            longest = s;
        }

        int l = shortest.length();
        int realHamming = 0;
        for (int i = 0; i < l; i++)
            realHamming += shortest.charAt(i) == longest.charAt(i) ? 0 : 1;

        return realHamming + longest.length() - l;
    }
}
