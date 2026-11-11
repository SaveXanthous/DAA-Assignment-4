package aitu.daa;

import aitu.daa.algorithms.edit.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditDistanceTest {
    static Stream<EditDistance> algorithms() {
        return Stream.of(new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance(),
                new aitu.daa.algorithmsmetrics.edit.RecursiveEditDistance(),
                new aitu.daa.algorithmsmetrics.edit.TableEditDistance(),
                new aitu.daa.algorithmsmetrics.edit.TwoRowEditDistance());
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void referenceValues(EditDistance algorithm) {
        assertEquals(3, algorithm.distance("kitten", "sitting"));
        assertEquals(2, algorithm.distance("flaw", "lawn"));
        assertEquals(5, algorithm.distance("intention", "execution"));
        assertEquals(0, algorithm.distance("abc", "abc"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void emptyStringsAndSingleCharacters(EditDistance algorithm) {
        assertEquals(0, algorithm.distance("", ""));
        assertEquals(3, algorithm.distance("", "abc"));
        assertEquals(3, algorithm.distance("abc", ""));
        assertEquals(0, algorithm.distance("a", "a"));
        assertEquals(1, algorithm.distance("a", "b"));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void agreesOnOneHundredRandomShortPairs(EditDistance algorithm) {
        Random random = new Random(42);
        EditDistance reference = new TableEditDistance();
        for (int run = 0; run < 100; run++) {
            String a = randomString(random, 6);
            String b = randomString(random, 6);
            int expected = reference.distance(a, b);
            assertEquals(expected, algorithm.distance(a, b), a + " -> " + b);
            assertEquals(expected, algorithm.distance(b, a), "Distance must be symmetric");
        }
    }

    private static String randomString(Random random, int maxLength) {
        StringBuilder result = new StringBuilder();
        int length = random.nextInt(maxLength + 1);
        for (int i = 0; i < length; i++) {
            result.append("abc".charAt(random.nextInt(3)));
        }
        return result.toString();
    }
}
