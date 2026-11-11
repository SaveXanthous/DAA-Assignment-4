package aitu.daa;

import aitu.daa.algorithms.search.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringSearchTest {
    static Stream<StringSearch> algorithms() {
        return Stream.of(new NaiveSearch(), new KmpSearch(),
                new aitu.daa.algorithmsmetrics.search.NaiveSearch(),
                new aitu.daa.algorithmsmetrics.search.KmpSearch());
    }

    static Stream<Function<String, int[]>> lpsAlgorithms() {
        return Stream.of(new KmpSearch()::lps, new aitu.daa.algorithmsmetrics.search.KmpSearch()::lps);
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void referenceMatchesIncludingOverlaps(StringSearch algorithm) {
        assertEquals(List.of(10), algorithm.search("ABABDABACDABABCABAB", "ABABCABAB"));
        assertEquals(List.of(0, 1, 2, 3), algorithm.search("AAAAA", "AA"));
        assertEquals(List.of(0, 2, 4), algorithm.search("ABABABA", "ABA"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void handlesBoundariesAndMissingMatches(StringSearch algorithm) {
        assertEquals(List.of(), algorithm.search("", "A"));
        assertEquals(List.of(), algorithm.search("AB", "ABC"));
        assertEquals(List.of(), algorithm.search("ABC", "D"));
        assertEquals(List.of(0), algorithm.search("ABC", "ABC"));
        assertEquals(List.of(2), algorithm.search("ABC", "C"));
        assertEquals(List.of(0, 2), algorithm.search("ABA", "A"));
        assertThrows(IllegalArgumentException.class, () -> algorithm.search("ABC", ""));
        assertThrows(IllegalArgumentException.class, () -> algorithm.search("", ""));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void agreesWithReferenceOnOneHundredRandomTexts(StringSearch algorithm) {
        Random random = new Random(123);
        for (int run = 0; run < 100; run++) {
            String text = randomString(random, random.nextInt(41));
            String pattern = randomString(random, 1 + random.nextInt(8));
            assertEquals(referenceMatches(text, pattern), algorithm.search(text, pattern),
                    "text=" + text + ", pattern=" + pattern);

            if (!text.isEmpty()) {
                int start = random.nextInt(text.length());
                pattern = text.substring(start, start + 1 + random.nextInt(text.length() - start));
                assertEquals(referenceMatches(text, pattern), algorithm.search(text, pattern));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("lpsAlgorithms")
    void referenceLpsArrays(Function<String, int[]> lps) {
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, lps.apply("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, lps.apply("ABABCABAB"));
        assertArrayEquals(new int[]{0}, lps.apply("A"));
        assertArrayEquals(new int[]{0, 0, 0}, lps.apply("ABC"));
        assertArrayEquals(new int[]{0, 1, 2, 3}, lps.apply("AAAA"));
        assertThrows(IllegalArgumentException.class, () -> lps.apply(""));
    }

    @ParameterizedTest
    @MethodSource("lpsAlgorithms")
    void lpsAgreesWithPrefixSuffixDefinition(Function<String, int[]> lps) {
        Random random = new Random(99);
        for (int run = 0; run < 100; run++) {
            String pattern = randomString(random, 1 + random.nextInt(20));
            int[] expected = new int[pattern.length()];
            for (int i = 0; i < pattern.length(); i++) {
                for (int length = 1; length <= i; length++) {
                    if (pattern.substring(0, length).equals(pattern.substring(i + 1 - length, i + 1))) {
                        expected[i] = length;
                    }
                }
            }
            assertArrayEquals(expected, lps.apply(pattern), pattern);
        }
    }

    private static List<Integer> referenceMatches(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        for (int i = text.indexOf(pattern); i >= 0; i = text.indexOf(pattern, i + 1)) {
            matches.add(i);
        }
        return matches;
    }

    private static String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append("ABC".charAt(random.nextInt(3)));
        }
        return result.toString();
    }
}
