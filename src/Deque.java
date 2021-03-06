import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private int capacity = 5;
    private Item[] items;

    private int first;
    private int last;

    // construct an empty deque
    public Deque() {
        items = (Item[]) new Object[capacity];
        first = 1;
        last = 1;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return (size() == 0);
    }

    // return the number of items on the deque
    public int size() {
        return (last + items.length - first) % items.length;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        checkArrayCapacity();
        if (first == 0) {
            first = items.length;
        }
        items[--first] = item;
    }

    // add the item to the end
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        checkArrayCapacity();
        items[last] = item;
        last = (last + 1) % items.length;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (first == last) {
            throw new NoSuchElementException();
        }
        Item item = items[first];
        items[first++] = null;
        first = first % items.length;
        shrinkIfRequired();
        return item;
    }

    // remove and return the item from the end
    public Item removeLast() {
        if (first == last) {
            throw new NoSuchElementException();
        }
        last = (last + items.length - 1) % items.length;
        Item item = items[last];
        items[last] = null;
        shrinkIfRequired();
        return item;
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeItr();
    }

    private boolean isFull() {
        return (size() >= items.length - 1);
    }

    private void modifyCapacity(int newCapacity) {
        Item[] newItems = (Item[]) new Object[newCapacity];
        for (int i = 0; i < size(); i++) {
            newItems[i] = items[(first + i) % items.length];
        }
        this.last = size();
        this.first = 0;
        this.items = newItems;
        this.capacity = newCapacity;
    }

    private void checkArrayCapacity() {
        if (isFull()) {
            modifyCapacity(capacity * 2);
        }
    }

    private void shrinkIfRequired() {
        if (size() <= items.length / 4) {
            modifyCapacity(capacity / 2);
        }
    }

    private class DequeItr implements Iterator<Item> {

        private int start;

        public DequeItr() {
            this.start = first;
        }

        @Override
        public boolean hasNext() {
            return (start != last);
        }

        @Override
        public Item next() {
            if (start == last) {
                throw new NoSuchElementException();
            }
            Item item = items[start++];
            start = start % items.length;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        deque.addFirst("A");
        deque.addFirst("Z");
        deque.addLast("B");
        deque.removeFirst();

    }

}
