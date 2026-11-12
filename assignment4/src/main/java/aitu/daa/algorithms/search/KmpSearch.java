package aitu.daa.algorithms.search;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch implements StringSearch {

    public int[] lps(String p) {

        int[] lps = new int[p.length()];
        int length = 0;
        int i = 1;
        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern) {

        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) {
            return matches;
        }

        int[] lps = lps(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);

                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }
}
