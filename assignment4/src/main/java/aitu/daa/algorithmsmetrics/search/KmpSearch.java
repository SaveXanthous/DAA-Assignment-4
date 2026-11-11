package aitu.daa.algorithmsmetrics.search;

import aitu.daa.algorithms.search.StringSearch;
import aitu.daa.algorithmsmetrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch implements StringSearch {
    private final Metrics metrics = new Metrics();

    public Metrics getMetrics() { return metrics; }

    public int[] lps(String p) {
        metrics.start();
        try {
            return buildLps(p);
        } finally {
            metrics.stop();
        }
    }

    private int[] buildLps(String p) {
        if (p.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        int[] lps = new int[p.length()];
        int length = 0;
        int i = 1;
        while (i < p.length()) {
            metrics.countComparison();
            if (p.charAt(i) == p.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        metrics.start();
        try {
            return findMatches(text, pattern);
        } finally {
            metrics.stop();
        }
    }

    private List<Integer> findMatches(String text, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) {
            return matches;
        }

        int[] lps = buildLps(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            metrics.countComparison();
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);

                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }
}
