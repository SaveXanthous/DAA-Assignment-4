package aitu.daa.algorithms.search;

import java.util.ArrayList;
import java.util.List;

public class NaiveSearch implements StringSearch {
    @Override
    public List<Integer> search(String text, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int j = 0;
            while (j < pattern.length() && text.charAt(start + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                matches.add(start);
            }
        }
        return matches;
    }
}
