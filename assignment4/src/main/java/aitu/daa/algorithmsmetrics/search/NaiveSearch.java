package aitu.daa.algorithmsmetrics.search;

import aitu.daa.algorithms.search.StringSearch;
import aitu.daa.algorithmsmetrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class NaiveSearch implements StringSearch {
    private final Metrics metrics = new Metrics();

    public Metrics getMetrics() { return metrics; }

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
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int j = 0;
            while (j < pattern.length()) {
                metrics.countComparison();
                if (text.charAt(start + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == pattern.length()) {
                matches.add(start);
            }
        }
        return matches;
    }
}
