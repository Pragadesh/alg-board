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

    private static List<Short> encode(String message, String encodePattern) {
        EncodeSequence encodeSequence = new EncodeSequence(encodePattern);
        List<Short> out = new ArrayList<>();
        for (int i = 0; i < message.length(); i++) {
            short pos = encodeSequence.getIndex(message.charAt(i));
            out.add(pos);
        }
        System.out.println("Encoded Result: " + out);
        return out;
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        DecodeSequence decodeSequence = new DecodeSequence();
        int i;
        while (!BinaryStdIn.isEmpty()) {
            i = BinaryStdIn.readInt();
            BinaryStdOut.write(decodeSequence.getCharacter(i));
        }
        BinaryStdOut.close();
    }

    private static String decode(List<Short> encodedData, String encodePattern) {
        StringBuilder builder = new StringBuilder();
        DecodeSequence decodeSequence = new DecodeSequence(encodePattern);
        for (int i = 0; i < encodedData.size(); i++) {
            builder.append(decodeSequence.getCharacter(encodedData.get(i)));
        }
        System.out.println("Decoded Result :" + builder.toString());
        return builder.toString();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        testEncodeDecode("CAAABCCCACCF");
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Invalid arguments: " + args);
        }
        if ("+".equals(args[0])) {
            encode();
        } else if ("-".equals(args[0])) {
            decode();
        }
    }

    private static void testEncodeDecode(String message) {
        String encodePattern = "ABCDEF";
        List<Short> encodedData = encode(message, encodePattern);
        String result = decode(encodedData, encodePattern);
    }

    private static class DecodeSequence {
        private char[] charPos;

        public DecodeSequence() {
            charPos = new char[R];
            for (int i = 0; i < R; i++) {
                charPos[i] = (char) i;
            }
        }

        public DecodeSequence(String sequence) {
            charPos = new char[sequence.length()];
            for (int i = 0; i < sequence.length(); i++) {
                charPos[i] = sequence.charAt(i);
            }
        }

        public char getCharacter(int pos) {
            char c = charPos[pos];
            while (pos > 0) {
                exchange(pos, --pos);
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

        public EncodeSequence(String sequence) {
            init();
            char[] characterSet = sequence.toCharArray();
            for (int i = 0; i < characterSet.length; i++) {
                DNode n = new DNode(characterSet[i], i);
                nodeList.addLast(n);
                charNodeMap[(int) characterSet[i]] = n;
            }
        }

        public EncodeSequence() {
            init();
            for (int i = 0; i < R; i++) {
                char c = (char) i;
                DNode n = new DNode(c, i);
                nodeList.addLast(n);
                charNodeMap[i] = n;
            }
        }

        private void init() {
            charNodeMap = new DNode[R];
            nodeList = new DNodeList();
        }

        private DNode getNode(char c) {
            return charNodeMap[(int) c];
        }

        public short getIndex(char c) {
            DNode n = getNode(c);
            if (n == null) {
                throw new IllegalArgumentException("Missing character: " + c);
            }
            int index = n.index;
            if (index > 0) {
                nodeList.moveToFirst(n);
            }
            return (short) index;
        }
    }

    private static class DNodeList {
        private DNode head;
        private int size;

        public DNodeList() {
            head = new DNode((char) 0, -1);
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
        private int index;
        private DNode prev;
        private DNode next;

        public DNode(char c, int index) {
            this.c = c;
            this.index = index;
        }
    }
}
