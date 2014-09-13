package com.brainydroid.daydreaming.ui.filtering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LevenshteinDistance extends CachingDistance implements IDistance {

    private static String TAG = "LevenshteinDistance";

    protected int computeDistance(String s, String t) {
        // Lengths
        int m = s.length();
        int n = t.length();

        // Initialize distance matrix
        ArrayList<ArrayList<Integer>> distances = new ArrayList<ArrayList<Integer>>(m + 1);
        for (int i = 0; i < m + 1; i++) {
            ArrayList<Integer> row = new ArrayList<Integer>(n + 1);
            for (int j = 0; j < n + 1; j++) row.add(j, 0);
            distances.add(i, row);
        }

        // Initialize edges of distance matrix
        for (int i = 1; i < m + 1; i++) distances.get(i).add(0, i);
        for (int j = 1; j < n + 1; j++) distances.get(0).add(j, j);

        for (int j = 1; j < n + 1; j++) {
            for (int i = 1; i < m + 1; i++) {

                if (s.charAt(i - 1) == t.charAt(j - 1)) distances.get(i).add(j, distances.get(i - 1).get(j - 1));  // No operation required
                else {
                    List<Integer> values = Arrays.asList(
                            distances.get(i - 1).get(j) + 1,  // Deletion
                            distances.get(i).get(j - 1) + 1,  // Insertion
                            distances.get(i - 1).get(j - 1) + 1   // Substitution
                    );
                    distances.get(i).add(j, Collections.min(values));
                }
            }
        }

        return distances.get(m).get(n);
    }

}
