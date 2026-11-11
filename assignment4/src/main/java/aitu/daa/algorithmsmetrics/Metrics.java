package aitu.daa.algorithmsmetrics;

public class Metrics {
    private long characterComparisons;
    private long startNanos;
    private long elapsedNanos;

    public void start() {
        characterComparisons = 0;
        elapsedNanos = 0;
        startNanos = System.nanoTime();
    }

    public void stop() { elapsedNanos = System.nanoTime() - startNanos; }
    public void countComparison() { characterComparisons++; }

    public long getCharacterComparisons() { return characterComparisons; }

    public long getElapsedNanos() { return elapsedNanos; }
    public double getElapsedMillis() { return elapsedNanos / 1_000_000.0; }
}
