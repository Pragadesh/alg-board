import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/*
 * https://coursera.cs.princeton.edu/algs4/assignments/burrows/specification.php
 */
public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        char c;
        EncodeSequence encodeSequence = new EncodeSequence();
        while (!BinaryStdIn.isEmpty()) {
            c = BinaryStdIn.readChar();
            BinaryStdOut.write(encodeSequence.getIndex(c));
        }
        BinaryStdOut.close();
    }

    private static List<Character> encode(String message) {
        EncodeSequence encodeSequence = new EncodeSequence();
        List<Character> out = new ArrayList<>();
        for (int i = 0; i < message.length(); i++) {
            char pos = encodeSequence.getIndex(message.charAt(i));
            out.add(pos);
        }
        System.out.println("Encoded Result: " + out);
        return out;
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        DecodeSequence decodeSequence = new DecodeSequence();
        char i;
        while (!BinaryStdIn.isEmpty()) {
            i = BinaryStdIn.readChar();
            BinaryStdOut.write(decodeSequence.getCharacter(i));
        }
        BinaryStdOut.close();
    }

    private static String decode(String message) {
        StringBuilder encodeBuilder = new StringBuilder();
        DecodeSequence decodeSequence = new DecodeSequence();
        for (int i = 0; i < message.length(); i++) {
            encodeBuilder.append(decodeSequence.getCharacter(message.charAt(i)));
        }
        System.out.println("Decoded Result :" + encodeBuilder.toString());
        return encodeBuilder.toString();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
//        testEncodeDecode("zebra");
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Invalid arguments: " + args);
        }
        if ("-".equals(args[0])) {
            encode();
        } else if ("+".equals(args[0])) {
            decode();
        }
    }

    private static void testEncodeDecode(String message) {
//        List<Character> encodedData = encode(message);
        String result = decode(message);
    }

    private static class DecodeSequence {
        private char[] charPos;

        public DecodeSequence() {
            charPos = new char[R];
            for (char i = 0; i < R; i++) {
                charPos[i] = i;
            }
        }

        public char getCharacter(char pos) {
            char c = charPos[pos];
            while (pos > 0) {
                charPos[pos] = charPos[--pos];
            }
            charPos[0] = c;
            return c;
        }

        private void exchange(int i, int j) {
            char t = charPos[i];
            charPos[i] = charPos[j];
            charPos[j] = t;
        }
    }

    private static class EncodeSequence {

        private DNode[] charNodeMap;
        private DNodeList nodeList;

        public EncodeSequence() {
            init();
            for (char i = 0; i < R; i++) {
                DNode n = new DNode(i, i);
                nodeList.addLast(n);
                charNodeMap[i] = n;
            }
        }

        private void init() {
            charNodeMap = new DNode[R];
            nodeList = new DNodeList();
        }

        private DNode getNode(char c) {
            return charNodeMap[c];
        }

        public char getIndex(char c) {
            DNode n = getNode(c);
            if (n == null) {
                throw new IllegalArgumentException("Missing character: " + c);
            }
            char index = n.index;
            if (index > 0) {
                nodeList.moveToFirst(n);
            }
            return index;
        }
    }

    private static class DNodeList {
        private DNode head;
        private int size;

        public DNodeList() {
            head = new DNode((char) 0, (char) 0);
            head.next = head;
            head.prev = head;
        }

        public void addLast(DNode n) {
            if (n != null) {
                DNode last = head.prev;
                n.prev = last;
                n.next = last.next;
                last.next.prev = n;
                last.next = n;
                size++;
            }
        }

        private void addFirst(DNode n) {
            if (n != null) {
                n.next = head.next;
                n.prev = head;
                head.next.prev = n;
                head.next = n;
            }
        }

        public void moveToFirst(DNode n) {
            if (n != null) {
                updateCountsBeforeNode(n.prev);
                delete(n);
                addFirst(n);
                n.index = 0;
            }
        }

        public void delete(DNode n) {
            if (n != null) {
                n.prev.next = n.next;
                n.next.prev = n.prev;
            }
        }

        private void updateCountsBeforeNode(DNode n) {
            while (n != null && n != head) {
                n.index++;
                n = n.prev;
            }
        }
    }

    private static class DNode {
        private char c;
        private char index;
        private DNode prev;
        private DNode next;

        public DNode(char c, char index) {
            this.c = c;
            this.index = index;
        }
    }
}
