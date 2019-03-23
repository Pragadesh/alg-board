import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {

	private int capacity = 5;
	private Item[] items;

	private int size;

	// construct an empty randomized queue
	public RandomizedQueue() {
		this.items = (Item[]) new Object[capacity];
	}

	// is the randomized queue empty?
	public boolean isEmpty() {
		return (size == 0);
	}

	// return the number of items on the randomized queue
	public int size() {
		return size;
	}

	// add the item
	public void enqueue(Item item) {
		if (item == null) {
			throw new NoSuchElementException();
		}
		checkArrayCapacity();
		items[size++] = item;
	}

	// remove and return a random item
	public Item dequeue() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		shrinkIfRequired();
		int rdmIdx = StdRandom.uniform(size);
		Item item = items[rdmIdx];
		if (rdmIdx != size - 1) {
			items[rdmIdx] = items[size - 1];
		}
		items[--size] = null;
		return item;
	}

	// return a random item (but do not remove it)
	public Item sample() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		int rdmIdx = StdRandom.uniform(size);
		return items[rdmIdx];
	}

	// return an independent iterator over items in random order
	@Override
	public Iterator<Item> iterator() {
		return new RandomizedQueueItr();
	}

	private void modifyCapacity(int newCapacity) {
		Item[] newItems = (Item[]) new Object[newCapacity];
		for (int i = 0; i < size(); i++) {
			newItems[i] = items[i];
		}
		this.items = newItems;
		this.capacity = newCapacity;
	}

	private void checkArrayCapacity() {
		if (size() == items.length) {
			modifyCapacity(capacity * 2);
		}
	}

	private void shrinkIfRequired() {
		if (size() <= items.length / 4) {
			modifyCapacity(capacity / 2);
		}
	}

	private class RandomizedQueueItr implements Iterator<Item> {

		private final int[] shuffledIndex;
		private int currentIdx;

		public RandomizedQueueItr() {
			this.shuffledIndex = new int[size];
			for (int i = 0; i < size; i++) {
				shuffledIndex[i] = i;
			}
			StdRandom.shuffle(shuffledIndex);
		}

		@Override
		public boolean hasNext() {
			return (currentIdx < shuffledIndex.length);
		}

		@Override
		public Item next() {
			if (currentIdx >= shuffledIndex.length) {
				throw new NoSuchElementException();
			}
			return items[shuffledIndex[currentIdx++]];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	// unit testing (optional)
	public static void main(String[] args) {
		RandomizedQueue<String> rdmQueue = new RandomizedQueue<>();
		rdmQueue.enqueue("A");
	}
}
