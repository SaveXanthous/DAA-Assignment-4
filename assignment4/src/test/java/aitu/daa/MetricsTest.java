package aitu.daa;

import aitu.daa.algorithmsmetrics.edit.*;
import aitu.daa.algorithmsmetrics.search.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {
    @Test
    void recursiveComparisonsResetBetweenCalls() {
        RecursiveEditDistance algorithm = new RecursiveEditDistance();
        algorithm.distance("a", "b");
        assertEquals(1, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("ab", "ab");
        assertEquals(2, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("", "abc");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void tableCountsComparisonsAndResets() {
        TableEditDistance algorithm = new TableEditDistance();
        algorithm.distance("ab", "xyz");
        assertEquals(6, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("a", "b");
        assertEquals(1, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("abc", "");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void twoRowsCountComparisonsAndReset() {
        TwoRowEditDistance algorithm = new TwoRowEditDistance();
        algorithm.distance("ab", "xyz");
        assertEquals(6, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("a", "b");
        assertEquals(1, algorithm.getMetrics().getCharacterComparisons());
        algorithm.distance("", "abc");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void naiveCountsSuccessfulAndFailedComparisons() {
        NaiveSearch algorithm = new NaiveSearch();
        algorithm.search("AAAA", "AAB");
        assertEquals(6, algorithm.getMetrics().getCharacterComparisons());
        algorithm.search("AAAAA", "AA");
        assertEquals(8, algorithm.getMetrics().getCharacterComparisons());
        algorithm.search("A", "AA");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void kmpCountsSearchAndLpsComparisonsTogether() {
        KmpSearch algorithm = new KmpSearch();
        algorithm.search("AAAA", "AAB");
        assertEquals(9, algorithm.getMetrics().getCharacterComparisons());
        algorithm.search("AAAAA", "AA");
        assertEquals(6, algorithm.getMetrics().getCharacterComparisons());
        algorithm.search("A", "AA");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void standaloneLpsResetsPreviousSearchMetrics() {
        KmpSearch algorithm = new KmpSearch();
        algorithm.search("AAAA", "AA");
        algorithm.lps("AAA");
        assertEquals(2, algorithm.getMetrics().getCharacterComparisons());
        algorithm.lps("A");
        assertEquals(0, algorithm.getMetrics().getCharacterComparisons());
    }

    @Test
    void rejectedPatternsDoNotLeavePreviousCounters() {
        NaiveSearch naive = new NaiveSearch();
        naive.search("AAA", "A");
        assertThrows(IllegalArgumentException.class, () -> naive.search("AAA", ""));
        assertEquals(0, naive.getMetrics().getCharacterComparisons());
        KmpSearch kmp = new KmpSearch();
        kmp.search("AAA", "AA");
        assertThrows(IllegalArgumentException.class, () -> kmp.search("AAA", ""));
        assertEquals(0, kmp.getMetrics().getCharacterComparisons());
        kmp.lps("AAA");
        assertThrows(IllegalArgumentException.class, () -> kmp.lps(""));
        assertEquals(0, kmp.getMetrics().getCharacterComparisons());
    }

    @Test
    void instancesDoNotShareCounters() {
        NaiveSearch first = new NaiveSearch();
        NaiveSearch second = new NaiveSearch();
        first.search("AAA", "A");
        second.search("A", "B");
        assertEquals(3, first.getMetrics().getCharacterComparisons());
        assertEquals(1, second.getMetrics().getCharacterComparisons());
    }

}
