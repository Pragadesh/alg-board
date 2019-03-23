import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;

public class Deque<T> implements Iterable<T>{

	private int capacity = 5;
	private T[] items;
	
	private int first;
	private int last;
	
	private static final String LINE_SEPARATOR = "line.separator";
	
	// construct an empty deque
	@SuppressWarnings("unchecked")
	public Deque() {
		items = (T[]) new Object[capacity];
		first = 1; last = 1;
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
	public void addFirst(T item) {
		if(item == null) {
			throw new NoSuchElementException();
		}
		checkArrayCapacity();
		if(first == 0) {
			first = items.length;
		}
		items[--first] = item;
	}

	// add the item to the end
	public void addLast(T item) {
		if(item == null) {
			throw new NoSuchElementException();
		}
		checkArrayCapacity();
		items[last] = item;
		last = (last + 1) % items.length;
	}

	// remove and return the item from the front
	public T removeFirst() {
		if(first == last) {
			throw new NoSuchElementException();
		}
		T item = items[first];
		items[first++] = null;
		first = first % items.length;
		shrinkIfRequired();
		return item;
	}

	// remove and return the item from the end
	public T removeLast() {
		if(first == last) {
			throw new NoSuchElementException();
		}
		last = (last + items.length - 1) % items.length;
		T item = items[last];
		items[last] = null;
		shrinkIfRequired();
		return item;
	}

	// return an iterator over items in order from front to end
	public Iterator<T> iterator() {
		return new DequeItr();
	}
	
	private boolean isFull() {
		return (size() >= items.length-1);
	}
	
	private void modifyCapacity(int newCapacity) {
		@SuppressWarnings("unchecked")
		T[] newItems = (T[]) new Object[newCapacity];
		for(int i=0; i<size(); i++) {
			newItems[i] = items[(first+i) % items.length];
		}
		this.last = size();
		this.first = 0;
		this.items = newItems;
		this.capacity = newCapacity;
	}
	
	private void checkArrayCapacity() {
		if(isFull()) {
			modifyCapacity(capacity * 2);
		}
	}
	
	private void shrinkIfRequired() {
		if(size() <= items.length/4) {
			modifyCapacity(capacity / 2);
		}
	}
	
	private void printArray() {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<items.length; i++) {
			builder.append(items[i]).append("\t");
		}
		builder.append(System.getProperty(LINE_SEPARATOR));
		int numOfTabs = first;
		while(numOfTabs-- > 0) {
			builder.append("\t");
		}
		builder.append("\u2191");
		builder.append(System.getProperty(LINE_SEPARATOR));
		numOfTabs = last;
		while(numOfTabs-- > 0) {
			builder.append("\t");
		}
		builder.append("\u21A1");
		builder.append(System.getProperty(LINE_SEPARATOR));
		StdOut.println(builder.toString());
	}
	
	private class DequeItr implements Iterator<T>{

		private int start;
		
		public DequeItr() {
			this.start = first;
		}
		
		@Override
		public boolean hasNext() {
			return (start != last);
		}

		@Override
		public T next() {
			if(start == last) {
				throw new NoSuchElementException();
			}
			T item = items[start++];
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
		deque.printArray();

	}

}
