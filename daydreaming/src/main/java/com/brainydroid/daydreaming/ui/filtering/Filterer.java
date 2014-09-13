package com.brainydroid.daydreaming.ui.filtering;

import android.widget.Filter;

import com.brainydroid.daydreaming.background.Logger;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Filterer extends Filter {

    private static String TAG = "Filterer";
    
    private static HashSet<String> stopwords = new HashSet<String>(
            Arrays.asList(new String[]{"'yourself", "yourselves", "would", "wouldn", "wouldn't",
                    "yes", "yet", "you", "your", "yours", "whomever", "whose", "why", "will",
                    "with", "within", "without", "won", "whether", "which", "while", "whither",
                    "who", "whoever", "whole", "whom", "whereafter", "whereas", "whereby",
                    "wherein", "whereupon", "wherever", "weren't", "what", "whatever", "when",
                    "whence", "whenever", "where", "ve", "very", "via", "was", "wasn", "we", "we",
                    "well", "were", "weren", "unless", "unlike", "unlikely", "until", "up", "upon",
                    "us", "used", "using", "together", "too", "toward", "towards", "trillion",
                    "twenty", "two", "under", "thousand", "three", "through", "throughout", "thru",
                    "thus", "to", "thereupon", "these", "they", "thirty", "this", "those",
                    "though", "thence", "there", "thereafter", "thereby", "therefore", "therein",
                    "ten", "than", "that", "the", "their", "them", "themselves", "then",
                    "sometime", "sometimes", "somewhere", "still", "stop", "such", "taking",
                    "since", "six", "sixty", "so", "some", "somehow", "someone", "something",
                    "seven", "seventy", "several", "she", "should", "shouldn", "shouldn't", "re",
                    "recent", "recently", "same", "seem", "seemed", "seeming", "seems",
                    "ourselves", "out", "over", "overall", "own", "per", "perhaps", "rather",
                    "only", "onto", "or", "others", "otherwise", "our", "ours", "nothing", "now",
                    "nowhere", "of", "off", "often", "on", "once", "one", "ninety", "no", "nobody",
                    "none", "nonetheless", "noone", "nor", "not", "my", "myself", "namely",
                    "neither", "never", "nevertheless", "next", "nine", "miss", "more", "moreover",
                    "most", "mostly", "mr", "mrs", "much", "must", "makes", "many", "maybe", "me",
                    "meantime", "meanwhile", "might", "million", "least", "less", "let", "like",
                    "likely", "ll", "ltd", "made", "make", "isn", "isn't", "it", "its", "itself",
                    "last", "later", "latter", "latterly", "ie", "i.e.", "if", "in", "inc", "inc.",
                    "indeed", "instead", "into", "is", "hers", "herself", "him", "himself", "his",
                    "how", "however", "hundred", "he", "hence", "her", "here", "hereafter",
                    "hereby", "herein", "hereupon", "further", "had", "has", "hasn", "hasn't",
                    "have", "haven", "haven't", "five", "for", "former", "formerly", "forty",
                    "found", "four", "from", "everyone", "everything", "everywhere", "except",
                    "few", "fifty", "first", "elsewhere", "end", "ending", "enough", "etc", "even",
                    "ever", "every", "don't", "down", "during", "each", "eg", "eight", "eighty",
                    "either", "else", "couldn't", "did", "didn", "didn't", "do", "does",
                    "doesn", "doesn't", "don", "but", "by", "can", "can't", "cannot",
                    "caption", "co", "could", "couldn", "being", "below", "beside", "besides",
                    "between", "beyond", "billion", "both", "becoming", "been", "before",
                    "beforehand", "begin", "beginning", "behind", "aren't", "around", "as",
                    "at", "be", "became", "because", "become", "becomes", "another", "any",
                    "anyhow", "anyone", "anything", "anywhere", "are", "aren", "already", "also",
                    "although", "always", "among", "amongst", "an", "and", "afterwards", "again",
                    "against", "all", "almost", "alone", "along", "a", "above", "according",
                    "across", "actually", "adj", "after'"}));

    private AutoCompleteAdapter adapter;

    @Inject private Timer timer;
    @Inject private HashSet<String> possibilities;
    @Inject private HashMap<String, HashSet<MetaString>> tokenMap;
    @Inject private HashMap<String, HashSet<String>> matchMap;
    @Inject private Tree<String> bkTree;
    @Inject private LevenshteinDistance distance;

    public void addPossibility(String possibility) {
        if (!possibilities.contains(possibility)) {
            Logger.d(TAG, "Adding possibility {}", possibility);
            possibilities.add(possibility);
            HashMap<String, HashSet<String>> newMatches = addToMatchMap(possibility);
            updateBKTree(newMatches);
        } else {
            Logger.d(TAG, "Possibility already present, not adding it again");
        }
    }

    public void initialize(AutoCompleteAdapter adapter, ArrayList<String> possibilitiesArray) {
        Logger.d(TAG, "Initializing");
        timer.start("Filterer initialization");

        this.adapter = adapter;
        possibilities.addAll(possibilitiesArray);

        buildMatchMap();
        buildBKTree();

        Logger.i(TAG, "Finished initializing ({}ms)", timer.finish("Filterer initialization"));
    }

    public ArrayList<MetaString> search(String query) {
        int distance = 0;
        if (query.length() >= 2) distance = 1;
        return search(query.toLowerCase(), distance);
    }

    private ArrayList<MetaString> search(final String query, int radius) {
        // Get the results from the BK tree
        HashSet<String> bkResults = new HashSet<String>();
        _searchBKTree(bkTree, query, radius, bkResults);
        HashSet<String> filteredResults = filterExcludedDifferences(query, bkResults);

        // Convert to original tokens
        HashSet<String> tokens = new HashSet<String>();
        for (String subString : filteredResults) {
            tokens.addAll(matchMap.get(subString));
        }

        // Convert back to original strings
        HashSet<MetaString> preResults = new HashSet<MetaString>();
        for (String suffix : tokens) {
            preResults.addAll(tokenMap.get(suffix));
        }

        // Re-order
        ArrayList<MetaString> results = new ArrayList<MetaString>(preResults);
        Comparator<MetaString> comparator = new Comparator<MetaString>() {
            @Override
            public int compare(MetaString ms1, MetaString ms2) {
                return distance.metaDistance(query, ms1) -
                        distance.metaDistance(query, ms2);
            }
        };

        Collections.sort(results, comparator);
        return results;
    }

    private static HashSet<String> filterExcludedDifferences(String query, HashSet<String> bkResults) {
        HashSet<String> filteredResults = new HashSet<String>();
        char firstQueryChar = query.charAt(0);
        for (String bkResult : bkResults) {
            if (bkResult.charAt(0) == firstQueryChar) {
                filteredResults.add(bkResult);
            }
        }
        return filteredResults;
    }

    private void _searchBKTree(Tree<String> tree, String query, int radius, HashSet<String> results) {
        // Get root string and distance to query
        String root = tree.getData();
        int d = distance.distance(root, query);

        // Add root if we can
        if (d <= radius) {
            results.add(root);
        }

        // Search all sub-trees at the right distance
        for (Map.Entry<Integer, Tree<String>> child : tree.getChildren().entrySet()) {
            if (child.getKey() <= d + radius) {
                _searchBKTree(child.getValue(), query, radius, results);
            }
        }
    }

    private void buildMatchMap() {
        buildTokenMap();
        Logger.d(TAG, "Building match map");

        for (String token : tokenMap.keySet()) {
            for (int i = 2; i <= token.length(); i++) {
                String prefix = token.substring(0, i);
                if (!matchMap.containsKey(prefix)) {
                    matchMap.put(prefix, new HashSet<String>());
                }
                matchMap.get(prefix).add(token);
            }
        }
    }

    private HashMap<String, HashSet<String>> addToMatchMap(String newString) {
        HashMap<String, HashSet<MetaString>> newTokens = addToTokenMap(newString);

        Logger.d(TAG, "Adding {} to matchMap", newString);
        HashMap<String, HashSet<String>> newMatches = new HashMap<String, HashSet<String>>();

        for (String token : newTokens.keySet()) {
            for (int i = 2; i <= token.length(); i++) {
                String prefix = token.substring(0, i);
                if (!matchMap.containsKey(prefix)) {
                    matchMap.put(prefix, new HashSet<String>());
                }
                if (!newMatches.containsKey(prefix)) {
                    newMatches.put(prefix, new HashSet<String>());
                }
                matchMap.get(prefix).add(token);
                newMatches.get(prefix).add(token);
            }
        }

        return newMatches;
    }

    private void buildTokenMap() {
        Logger.d(TAG, "Building token map");

        for (String s : possibilities) {
            MetaString ms = MetaString.getInstance(s);

            // Add tokens
            for (String token : ms.getTokens()) {
                if (stopwords.contains(token)) continue;
                if (!tokenMap.containsKey(token)) {
                    tokenMap.put(token, new HashSet<MetaString>());
                }
                tokenMap.get(token).add(ms);
            }

            // Add tags
            for (String tag : ms.getTags()) {
                if (!tokenMap.containsKey(tag)) {
                    tokenMap.put(tag, new HashSet<MetaString>());
                }
                tokenMap.get(tag).add(ms);
            }

            // Finally, add full text
            if (!tokenMap.containsKey(ms.getLower())) {
                tokenMap.put(ms.getLower(), new HashSet<MetaString>());
            }
            tokenMap.get(ms.getLower()).add(ms);
        }
    }

    private HashMap<String, HashSet<MetaString>> addToTokenMap(String newString) {
        Logger.d(TAG, "Adding {} to tokenMap", newString);

        MetaString ms = MetaString.getInstance(newString);
        HashMap<String, HashSet<MetaString>> newTokens = new HashMap<String, HashSet<MetaString>>();

        // Add tokens
        for (String token : ms.getTokens()) {
            if (stopwords.contains(token)) continue;
            if (!tokenMap.containsKey(token)) {
                tokenMap.put(token, new HashSet<MetaString>());
            }
            if (!newTokens.containsKey(token)) {
                newTokens.put(token, new HashSet<MetaString>());
            }
            tokenMap.get(token).add(ms);
            newTokens.get(token).add(ms);
        }

        // Add tags
        for (String tag : ms.getTags()) {
            if (!tokenMap.containsKey(tag)) {
                tokenMap.put(tag, new HashSet<MetaString>());
            }
            if (!newTokens.containsKey(tag)) {
                newTokens.put(tag, new HashSet<MetaString>());
            }
            tokenMap.get(tag).add(ms);
            newTokens.get(tag).add(ms);
        }

        // Finally, add full text
        if (!tokenMap.containsKey(ms.getLower())) {
            tokenMap.put(ms.getLower(), new HashSet<MetaString>());
        }
        if (!newTokens.containsKey(ms.getLower())) {
            newTokens.put(ms.getLower(), new HashSet<MetaString>());
        }
        tokenMap.get(ms.getLower()).add(ms);
        newTokens.get(ms.getLower()).add(ms);

        return newTokens;
    }

    private void buildBKTree() {
        Logger.d(TAG, "Building BK Tree");

        Set<String> items = matchMap.keySet();
        String firstItem = null;
        for (String item : items) {
            if (firstItem == null) {
                firstItem = item;
                bkTree.setData(firstItem);
            } else {
                _insertInBKTree(bkTree, item);
            }
        }
    }

    private void updateBKTree(HashMap<String, HashSet<String>> newMatches) {
        Logger.d(TAG, "Updating BK Tree");
        for (String item : newMatches.keySet()) {
            _insertInBKTree(bkTree, item);
        }
    }

    private void _insertInBKTree(Tree<String> tree, String item) {
        // item is our root
        if (item.equals(tree.getData())) return;

        // Else, insert item in an existing subtree or as a new child
        int d = distance.distance(tree.getData(), item);
        if (tree.hasEdge(d)) {
            _insertInBKTree(tree.getChild(d), item);
        } else {
            tree.addChild(d, item);
        }
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Logger.d(TAG, "Performing filter operation for {}", constraint);
        timer.start("filter");

        FilterResults filterResults = new FilterResults();
        if (constraint != null) {
            ArrayList<MetaString> results = search(constraint.toString());
            // We artificially set this to a minimum of 1 because it always transits by Android,
            // which will unregister its observers if it thinks there are no results.
            // This will cause the complete list to flicker and be recreated each time we type
            // a new letter, even if the "Nothing found" text doesn't change. This value isn't
            // used elsewhere anyway.
            filterResults.count = Math.max(results.size(), 1);
            filterResults.values = results;
        } else {
            filterResults.count = 0;
            filterResults.values = null;
        }

        Logger.i(TAG, "Filtered for {0} in {1}ms", constraint, timer.finish("filter"));
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Logger.d(TAG, "Publishing results");
        //noinspection unchecked
        adapter.setResults((ArrayList<MetaString>)results.values);
    }
}
