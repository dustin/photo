package net.spy.photo.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A Set that is backed by a fixed-size array.
 */
public class ImmutableArraySet<E> extends Object implements Set<E> {

	E[] elements=null;

	@SuppressWarnings("unchecked") // unchecked conversion
	public ImmutableArraySet(Set<? extends E> el) {
		super();
		elements=(E[])new Object[el.size()];
		el.toArray(elements);
	}

	public boolean add(E o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		boolean rv=false;
		for(int i=0; rv==false && i<elements.length; i++) {
			rv |= o == null ? elements[i] == null : o.equals(elements[i]);
		}
		return rv;
	}

	public boolean containsAll(Collection<?> c) {
		boolean rv=true;
		for(Object o : c) {
			rv &= contains(o);
		}
		return rv;
	}

	public boolean isEmpty() {
		return elements.length == 0;
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int idx=0;

			public boolean hasNext() {
				return idx < elements.length;
			}

			public E next() {
				if(idx >= elements.length) {
					throw new NoSuchElementException();
				}
				return elements[idx++];
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return elements.length;
	}

	public Object[] toArray() {
		Object[] rv=new Object[elements.length];
		return toArray(rv);
	}

	@SuppressWarnings("unchecked") // unchecked array type conversion
	public <T> T[] toArray(T[] a) {
		T[] toFill=a;
		if(toFill.length < elements.length) {
			toFill=(T[])new Object[elements.length];
		}
		if(toFill.length > elements.length) {
			Arrays.fill(toFill, null);
		}
		System.arraycopy(elements, 0, toFill, 0, elements.length);
		return toFill;
	}
}
