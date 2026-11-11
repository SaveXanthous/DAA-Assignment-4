package aitu.daa.algorithms.edit;

public class RecursiveEditDistance implements EditDistance {

    @Override
    public int distance(String a, String b) {
        return distance(a, b, a.length(), b.length());
    }

    private int distance(String a, String b, int i, int j) {
        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return distance(a, b, i - 1, j - 1);
        }

        int delete = distance(a, b, i - 1, j);
        int insert = distance(a, b, i, j - 1);
        int replace = distance(a, b, i - 1, j - 1);
        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}
