package aitu.daa.experiment;

import aitu.daa.algorithmsmetrics.Metrics;
import aitu.daa.algorithmsmetrics.edit.*;
import aitu.daa.algorithmsmetrics.search.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public final class ExperimentRunner {
    private static final int RUNS = 3;
    private static final int WARMUPS = 3;
    private static volatile int blackhole;
    private record Algorithm(String name, Supplier<Object> action, Metrics metrics) {}

    private ExperimentRunner() {}

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: ExperimentRunner [results.csv]");
        }
        Path output = args.length == 0 ? Path.of("results/results.csv") : Path.of(args[0]);
        warmUpJvm();
        run(output, new int[]{2, 4, 6, 8}, new int[]{128, 256, 512, 1024},
                new int[]{1024, 4096, 16384, 65536});
        System.out.println("Saved measurements to " + output.toAbsolutePath());
    }

    static void run(Path output, int[] smallSizes, int[] dpSizes, int[] searchSizes) throws IOException {
        Files.createDirectories(output.toAbsolutePath().getParent());
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            csv.println("algorithm,input,n,m,run,seed,time_ms,comparisons,result");
            for (String type : List.of("random", "worst")) {
                for (int n : smallSizes) measure(csv, type, InputGenerator.edit(type, n), true, true);
                for (int n : dpSizes) measure(csv, type, InputGenerator.edit(type, n), true, false);
                for (int n : searchSizes) measure(csv, type, InputGenerator.search(type, n), false, false);
            }
            if (csv.checkError()) throw new IOException("Could not write measurements");
        }
    }

    private static void measure(PrintWriter csv, String type, InputGenerator.Input input,
                                boolean edit, boolean includeRecursion) {
        List<Algorithm> algorithms = algorithms(input, edit, includeRecursion);
        Object expected = edit
                ? new aitu.daa.algorithms.edit.TableEditDistance().distance(input.first(), input.second())
                : algorithms.get(0).action().get();
        if (type.equals("worst")) {
            Object known = edit ? Integer.valueOf(input.first().length()) : List.of();
            if (!expected.equals(known)) throw new IllegalStateException("Wrong worst-case answer");
        }
        for (Algorithm algorithm : algorithms) {
            for (int i = 0; i < WARMUPS; i++) checkedRun(algorithm, expected);
        }
        for (int run = 1; run <= RUNS; run++) {
            if (run > 1) Collections.reverse(algorithms);
            for (Algorithm algorithm : algorithms) {
                Object result = checkedRun(algorithm, expected);
                int value = result instanceof Integer distance ? distance : ((List<?>) result).size();
                csv.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%d,%.9f,%d,%d%n", algorithm.name(), type,
                        input.first().length(), input.second().length(), run, InputGenerator.SEED,
                        algorithm.metrics().getElapsedMillis(),
                        algorithm.metrics().getCharacterComparisons(), value);
            }
        }
    }

    private static Object checkedRun(Algorithm algorithm, Object expected) {
        Object result = algorithm.action().get();
        if (!result.equals(expected)) throw new IllegalStateException("Algorithms disagree: " + algorithm.name());
        blackhole = result.hashCode();
        return result;
    }

    private static List<Algorithm> algorithms(InputGenerator.Input input, boolean edit, boolean recursive) {
        String a = input.first(), b = input.second();
        List<Algorithm> result = new ArrayList<>();
        if (edit) {
            if (recursive) {
                RecursiveEditDistance algorithm = new RecursiveEditDistance();
                result.add(new Algorithm("Recursive", () -> algorithm.distance(a, b), algorithm.getMetrics()));
                return result;
            }
            TableEditDistance table = new TableEditDistance();
            TwoRowEditDistance rows = new TwoRowEditDistance();
            result.add(new Algorithm("Table", () -> table.distance(a, b), table.getMetrics()));
            result.add(new Algorithm("TwoRows", () -> rows.distance(a, b), rows.getMetrics()));
        } else {
            NaiveSearch naive = new NaiveSearch();
            KmpSearch kmp = new KmpSearch();
            result.add(new Algorithm("Naive", () -> naive.search(a, b), naive.getMetrics()));
            result.add(new Algorithm("KMP", () -> kmp.search(a, b), kmp.getMetrics()));
        }
        return result;
    }

    private static void warmUpJvm() {
        List<Algorithm> algorithms = algorithms(InputGenerator.edit("random", 4), true, true);
        algorithms.addAll(algorithms(InputGenerator.edit("random", 4), true, false));
        algorithms.addAll(algorithms(InputGenerator.search("random", 1024), false, false));
        for (int i = 0; i < 2000; i++) {
            for (Algorithm algorithm : algorithms) blackhole = algorithm.action().get().hashCode();
        }
    }
}
