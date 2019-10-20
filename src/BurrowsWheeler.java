
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/*
 * https://coursera.cs.princeton.edu/algs4/assignments/burrows/specification.php
 */
public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder builder = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            builder.append(BinaryStdIn.readChar());
        }
        BinaryStdOut.write(_transform(builder.toString()));
        BinaryStdOut.close();
    }

    private static String _transform(String message) {
        CircularSuffixArray suffixArray = new CircularSuffixArray(message);
        int first = -1;
        int length = message.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int origIndex = suffixArray.index(i);
            if (origIndex == 0) {
                first = i;
            }
            builder.append(message.charAt((length - 1 + origIndex) % length));
        }
        return first + builder.toString();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int pos = BinaryStdIn.readInt();
        StringBuilder builder = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            builder.append(BinaryStdIn.readChar());
        }
        char[] r = _inverseTransform(builder.toString(), pos);
        for (int i = 0; i < r.length; i++) {
            BinaryStdOut.write(r[i]);
        }
        BinaryStdOut.close();
    }

    private static char[] _inverseTransform(String transformedMessage, int sortedIndex) {
        char[] t = transformedMessage.toCharArray();
        Map<Integer, LinkedList<Integer>> charPositionMap = new HashMap<>();
        for (int i = 0; i < t.length; i++) {
            int c = (int) t[i];
            LinkedList<Integer> charPositions = charPositionMap.get(c);
            if (charPositions == null) {
                charPositions = new LinkedList<>();
                charPositionMap.put(c, charPositions);
            }
            charPositions.addLast(i);
        }
        char[] s = t.clone();
        Arrays.sort(s);
        int[] next = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            next[i] = charPositionMap.get((int) s[i]).removeFirst();
        }
        char[] r = new char[s.length];
        r[r.length - 1] = t[sortedIndex];
        sortedIndex = next[sortedIndex];
        for (int i = 0; i < r.length - 1; i++) {
            r[i] = t[sortedIndex];
            sortedIndex = next[sortedIndex];
        }
        return r;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String msg = "ABRACADABRA!";
        String response = _transform(msg);
        System.out.println("Transformed: " + response);
        String orig = new String (_inverseTransform(response.substring(1), Integer.parseInt("" + response.charAt(0))));
        System.out.println("Inversed: " + orig);
    }
}
