package aitu.daa.algorithmsmetrics.edit;

import aitu.daa.algorithms.edit.EditDistance;
import aitu.daa.algorithmsmetrics.Metrics;

public class TableEditDistance implements EditDistance {
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
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                metrics.countComparison();
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[n][m];
    }
}
