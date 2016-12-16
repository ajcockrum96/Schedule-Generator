import java.awt.*;
import java.io.*;
import java.util.*;

public class LinkedList<E> implements List<E> {
	E value;
	LinkedList<E> next;
	public LinkedList<E>() {
		value = null;
		next  = null;
	}

	boolean add(E e) {
		if(value == null) {
			value = e;
			return true;
		}
		else if(next == null) {
			next = new LinkedList<E>();
		}
		return next.add(e);
	}

	boolean add(int index, E element) {
		if(index > 0) {
			return next.add(index - 1, element);
		}
		else if(index == 0) {
			LinkedList<E> temp = next;
			next = new LinkedList<E>();
			next.value = e;
			next.next  = temp;
			return true;
		}
		else {
			return false;
		}
	}

	boolean addAll(Collection<? extends E> c) {
		return false;
	}

	boolean contains(Object o) {
	}

	boolean containsAll(Collection<?> c) {
		return false;
	}

	boolean equals(Object o) {
	}

	E get(int index) {
	}

	int hashCode() {
	}

	int indexOf(Object o) {
	}

	boolean isEmpty() {
	}

	Iterator<E> iterator() {
	}

	int lastIndexOf(object o) {
	}

	ListIterator<E> listIterator() {
	}

	ListIterator<E> listIterator(int index) {
	}

	E remove(int index) {
	}

	E remove(Object o) {
	}

	E removeAll(Collection<?> c) {
	}

	E retainAll(Collection<?> c) {
	}

	E set(int index, E element) {
	}

	int size() {
	}

	List<E> subList(int fromIndex, int toIndex) {
	}

	Object[] toArray() {
	}
	
	<T> T[] toArray(T[] a) {
	}
}