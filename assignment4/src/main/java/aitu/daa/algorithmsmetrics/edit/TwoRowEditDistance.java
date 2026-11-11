package aitu.daa.algorithmsmetrics.edit;

import aitu.daa.algorithms.edit.EditDistance;
import aitu.daa.algorithmsmetrics.Metrics;

public class TwoRowEditDistance implements EditDistance {
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
        int n = a.length();
        int m = b.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                metrics.countComparison();
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(previous[j],
                            Math.min(current[j - 1], previous[j - 1]));
                }
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[m];
    }
}
