import java.util.Arrays;

/*
 * https://coursera.cs.princeton.edu/algs4/assignments/burrows/specification.php
 */
public class CircularSuffixArray {

    private String s;
    private int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Invalid input: " + s);
        }
        this.s = s;
        if(!s.isEmpty()) {
            sortSuffixes(s);
        }
    }

    private void sortSuffixes(String s) {
        IndexedString[] sortedSuffixes = new IndexedString[s.length()];
        sortedSuffixes[0] = new IndexedString(s, 0);
        for (int i = 1; i < s.length(); i++) {
            String rotated = s.substring(i) + s.substring(0, i);
            sortedSuffixes[i] = new IndexedString(rotated, i);
        }
        Arrays.sort(sortedSuffixes);
        index = new int[sortedSuffixes.length];
        for (int i = 0; i < sortedSuffixes.length; i++) {
            index[i] = sortedSuffixes[i].index;
        }
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || index == null || i >= index.length) {
            throw new IllegalArgumentException(String.format("Index %d is outside boundary (0, %d)", i, index == null? 0 : index.length));
        }
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        testSorting();
    }

    private static void testSorting() {
//        String message = "ABRACADABRA!";
        String message = "";
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(message);
        for (int i = 0; i < message.length(); i++) {
            System.out.println(circularSuffixArray.index(i));
        }
    }

    private static class IndexedString implements Comparable<IndexedString> {
        private String value;
        private int index;

        public IndexedString(String s, int index) {
            this.value = s;
            this.index = index;
        }

        @Override
        public int compareTo(IndexedString other) {
            if (other == null || other.value == null) {
                return 1;
            }
            return this.value.compareTo(other.value);
        }
    }
}
