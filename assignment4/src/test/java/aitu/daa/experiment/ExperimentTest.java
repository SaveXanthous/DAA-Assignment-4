package aitu.daa.experiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentTest {
    @TempDir Path directory;

    @Test
    void generatorIsReproducibleAndHasExpectedShapes() {
        for (String type : new String[]{"random", "worst"}) {
            assertEquals(InputGenerator.edit(type, 128), InputGenerator.edit(type, 128));
            assertEquals(InputGenerator.search(type, 1024), InputGenerator.search(type, 1024));
            var edit = InputGenerator.edit(type, 128);
            assertEquals(128, edit.first().length());
            assertEquals(128, edit.second().length());
            var search = InputGenerator.search(type, 1024);
            assertEquals(1024, search.first().length());
            assertEquals(16, search.second().length());
        }
        assertEquals(new InputGenerator.Input("AAAA", "BBBB"), InputGenerator.edit("worst", 4));
        var search = InputGenerator.search("worst", 1024);
        assertEquals("A".repeat(1024), search.first());
        assertEquals("A".repeat(15) + "B", search.second());
        assertFalse(search.first().contains(search.second()));
        assertThrows(IllegalArgumentException.class, () -> InputGenerator.edit("random", 0));
        assertThrows(IllegalArgumentException.class, () -> InputGenerator.search("unknown", 64));
    }

    @Test
    void csvContainsThreeActualRunsPerCaseAndReproducibleCounts() throws Exception {
        Path first = directory.resolve("first.csv");
        Path second = directory.resolve("second.csv");
        ExperimentRunner.run(first, new int[]{2}, new int[]{8}, new int[]{128});
        ExperimentRunner.run(second, new int[]{2}, new int[]{8}, new int[]{128});
        var lines = Files.readAllLines(first);
        var repeat = Files.readAllLines(second);
        assertEquals("algorithm,input,n,m,run,seed,time_ms,comparisons,result", lines.get(0));
        assertEquals(31, lines.size());
        assertEquals(lines.size(), repeat.size());
        Map<String, Set<Integer>> sizes = new HashMap<>();
        Map<String, Set<Integer>> runs = new HashMap<>();
        Map<String, Long> comparisons = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] row = lines.get(i).split(",");
            String[] again = repeat.get(i).split(",");
            assertEquals(9, row.length);
            assertTrue(Double.parseDouble(row[6]) >= 0);
            for (int column = 0; column < row.length; column++) {
                if (column != 6) assertEquals(row[column], again[column]);
            }
            sizes.computeIfAbsent(row[0], ignored -> new HashSet<>()).add(Integer.parseInt(row[2]));
            String key = row[0] + ":" + row[1] + ":" + row[2];
            assertTrue(runs.computeIfAbsent(key, ignored -> new HashSet<>()).add(Integer.parseInt(row[4])));
            long count = Long.parseLong(row[7]);
            Long previous = comparisons.putIfAbsent(key, count);
            if (previous != null) assertEquals(previous.longValue(), count);
            if (row[0].equals("Table") || row[0].equals("TwoRows")) {
                assertEquals(Long.parseLong(row[2]) * Long.parseLong(row[3]), count);
            }
            if (row[1].equals("worst")) {
                boolean search = row[0].equals("Naive") || row[0].equals("KMP");
                assertEquals(search ? "0" : row[2], row[8]);
            }
        }
        assertEquals(Set.of(2), sizes.get("Recursive"));
        assertEquals(Set.of(8), sizes.get("Table"));
        assertEquals(Set.of(8), sizes.get("TwoRows"));
        for (Set<Integer> values : runs.values()) assertEquals(Set.of(1, 2, 3), values);
    }
}
