import java.util.Iterator;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException();
        }
        int k = Integer.parseInt(args[0]);
        if (k <= 0) {
            throw new IllegalArgumentException("argument must be positive");
        }
        RandomizedQueue<String> rdmQueue = new RandomizedQueue<>();

        int count = 0;
        while (!StdIn.isEmpty()) {
            String word = StdIn.readString();
            if (rdmQueue.size() < k) {
                rdmQueue.enqueue(word);
            } else if (StdRandom.uniform(count) % 2 == 0) {
                rdmQueue.dequeue();
                rdmQueue.enqueue(word);
            }
            count++;
        }
        printQueue(rdmQueue);
    }

    private static void printQueue(RandomizedQueue<String> rdmQueue) {
        Iterator<String> itr = rdmQueue.iterator();
        while (itr.hasNext()) {
            StdOut.println(itr.next());
        }
    }
}
