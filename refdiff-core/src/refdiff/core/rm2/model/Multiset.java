package refdiff.core.rm2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


public class Multiset<E> implements Collection<E> {

	private HashMap<E, Integer> map;
	private HashMap<E, List<Long>> submap;
	private int count;
	
	public Multiset() {
		this.map = new HashMap<E, Integer>();
		this.submap = new HashMap<E, List<Long>>();
		this.count = 0;
	}
	
	public E getFirst() {
		return map.keySet().iterator().next();
	}

	public Multiset<E> suchThat(Filter<E> filter) {
		Multiset<E> result = new Multiset<E>(); 
		for (Entry<E, Integer> e : map.entrySet()) {
			if (filter.accept(e.getKey())) {
				result.add(e.getKey(), e.getValue());
			}
		}
		return result;
	}

	public Multiset<E> minus(Multiset<E> other) {
		Multiset<E> result = new Multiset<E>(); 
		for (Entry<E, Integer> e : map.entrySet()) {
			Integer thisCount = e.getValue();
			Integer otherCount = other.map.get(e.getKey());
			Integer diff;
			if (otherCount == null) {
				diff = thisCount;
			} else {
				diff = thisCount - otherCount;
			}
			if (diff > 0) {
				result.add(e.getKey(), diff);
			}
		}
		return result;
	}
	
	public Multiset<E> plus(Multiset<E> other) {
        Multiset<E> result = new Multiset<E>();
        for (Entry<E, Integer> e : other.map.entrySet()) {
            E key = e.getKey();
            if (map.containsKey(key)) {
                result.add(key, map.get(key) + e.getValue());
            } else {
                result.add(key, e.getValue());
            }
        }
        return result;
    }

	public Multiset<E> minus(E entity) {
		if (map.containsKey(entity)) {
			Multiset<E> result = new Multiset<E>();
			for (Entry<E, Integer> e : map.entrySet()) {
				if (!e.getKey().equals(entity)) {
					result.add(e.getKey(), e.getValue());
				}
			}
			return result;
		}
		return this;
	}

	public boolean add(E e, int cardinality) {
		Integer value = map.get(e);
		this.count += cardinality;
		if (value != null) {
			map.put(e, value + cardinality);
			return true;
		} else {
			map.put(e, cardinality);
			return false;
		}
	}
	
	public boolean add(E e, int cardinality, long line) {
		boolean result = this.add(e,cardinality);		
		List<Long> lines = submap.get(e);
		if (lines != null) {
			lines.add(line);
			result = (result && true);
		} else {
			lines = new ArrayList<Long>();
			lines.add(line);
			submap.put(e, lines);
			result = (result && false);
		}
		return result;
	}
	
	public boolean add(E e, long line) {
		return this.add(e, 1, line);
	}
	
	public int getMultiplicity(E entity) {
		Integer value = map.get(entity);
		return value == null ? 0 : value.intValue();
	}
	
	public List<Long> getCallerLines(E entity) {
		List<Long> value = submap.get(entity);
		return value == null ? new ArrayList<Long>() : value;
	}
	
	public int getCountLines(E entity) {
		List<Long> value = submap.get(entity);
		return value == null ? 0 : value.size();
	}
	
	public int size() {
		return count;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	public Object[] toArray() {
		return map.keySet().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return map.keySet().toArray(a);
	}

	public boolean add(E e) {
		return this.add(e, 1);
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		return map.keySet().containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			this.add(e);
		}
		return true;
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		map.clear();
		count = 0;
	}

	public String toString() {
		return map.toString();
	}
	
	public Set<E> asSet() {
	    return map.keySet();
	}

}
