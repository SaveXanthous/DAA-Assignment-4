package aitu.daa.experiment;

import java.util.Random;

public final class InputGenerator {
    public static final long SEED = 42;
    public record Input(String first, String second) {}

    private InputGenerator() {}

    public static Input edit(String type, int n) {
        validate(type, n);
        if (type.equals("worst")) {
            return new Input("A".repeat(n), "B".repeat(n));
        }
        Random random = new Random(SEED + 31L * n);
        return new Input(randomString(random, n), randomString(random, n));
    }

    public static Input search(String type, int n) {
        validate(type, n);
        int m = Math.max(1, n / 64);
        if (type.equals("worst")) {
            return new Input("A".repeat(n), "A".repeat(m - 1) + "B");
        }
        Random random = new Random(SEED + 1_000_003L + 31L * n);
        return new Input(randomString(random, n), randomString(random, m));
    }

    private static void validate(String type, int n) {
        if (n < 1 || !("random".equals(type) || "worst".equals(type))) {
            throw new IllegalArgumentException("Use a positive size and random or worst input");
        }
    }

    private static String randomString(Random random, int n) {
        StringBuilder text = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            text.append("ABCD".charAt(random.nextInt(4)));
        }
        return text.toString();
    }
}
