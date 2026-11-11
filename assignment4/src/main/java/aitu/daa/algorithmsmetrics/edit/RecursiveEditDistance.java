package aitu.daa.algorithmsmetrics.edit;

import aitu.daa.algorithms.edit.EditDistance;
import aitu.daa.algorithmsmetrics.Metrics;

public class RecursiveEditDistance implements EditDistance {
    private final Metrics metrics = new Metrics();

    public Metrics getMetrics() { return metrics; }

    @Override
    public int distance(String a, String b) {
        metrics.start();
        try {
            return computeDistance(a, b);
        } finally {
            metrics.stop();
        }
    }

    private int computeDistance(String a, String b) {
        return distance(a, b, a.length(), b.length());
    }

    private int distance(String a, String b, int i, int j) {
        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }
        metrics.countComparison();
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return distance(a, b, i - 1, j - 1);
        }

        int delete = distance(a, b, i - 1, j);
        int insert = distance(a, b, i, j - 1);
        int replace = distance(a, b, i - 1, j - 1);
        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}
