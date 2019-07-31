package org.pg.datastructures;

import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>> {

    private int size;

    private Key[] index;

    // create indexed priority queue with indices 0, 1, ..., N-1
    @SuppressWarnings("unchecked")
    public IndexMinPQ(int N) {
        index = (Key[]) new Object[N + 1];
    }

    // associate key with index i
    public void insert(int i, Key key) {

    }

    // decrease the key associated with index i
    public void decreaseKey(int i, Key key) {

    }

    // is i an index on the priority queue?
    public boolean contains(int i) {
        return false;
    }

    // remove a minimal key and return its associated index
    public Key delMin() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        Key minValue = index[1];
        index[1] = index[size--];
        sink(1);
        return minValue;
    }

    public void add(Key key) {
        index[++size] = key;
        swim(size);
    }

    // is the priority queue empty?
    public boolean isEmpty() {
        return (size == 0);
    }

    // number of entries in the priority queue
    public int size() {
        return size;
    }

    private boolean greater(int i, int j) {
        return index[i].compareTo(index[j]) > 0;
    }

    private void swim(int k) {
        while (k > 1 && greater(k, k / 2)) {
            exchange(k, k / 2);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= size) {
            int child = k * 2;
            if (child < size && greater(child, child + 1)) {
                child++;
            }
            if (greater(k, child)) {
                exchange(k, child);
                k = child;
            } else {
                break;
            }
        }
    }

    private void exchange(int i, int j) {

    }
}
