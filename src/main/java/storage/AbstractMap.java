package storage;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** 
 * This abstract class defines the protocols for 
 * a concrete hash map complying with the project.
 * 
 * Do not modify the protocols.
 * 
 * You do not need to change any implementation
 * that is already provided for you.
 * 
 * Additional features should be implemented in a
 * concrete subclass, not in this abstract class.
 */
public abstract class AbstractMap<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>> {
	@Override public abstract void clear();

	@Override public abstract int size();
	@Override public abstract boolean isEmpty();
			  public abstract double loadFactor();

	@Override public abstract V put(K key, V value);
	@Override public abstract V get(Object key);
	@Override public abstract V remove(Object key);

	@Override public abstract boolean containsKey(Object key);
	@Override public abstract boolean containsValue(Object value);
	
	@Override public abstract void putAll(Map<? extends K, ? extends V> m);
	
	@Override public abstract String toString();
	@Override public abstract boolean equals(Object o);
	@Override public abstract int hashCode();

	@Override public abstract Iterator<Map.Entry<K, V>> iterator();
	
	/** An implementation is already provided. */
	@Override
	public final Set<Map.Entry<K, V>> entrySet() {
		return new java.util.AbstractSet<Map.Entry<K, V>>() {
			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return AbstractMap.this.iterator();
			}

			@Override
			public int size() {
				return AbstractMap.this.size();
			}
		};
	}
	
	/** An implementation is already provided. */
	@Override
	public final Set<K> keySet() {
		return new java.util.AbstractSet<K>() {
			@Override
			public Iterator<K> iterator() {
				return new Iterator<K>() {
					Iterator<Map.Entry<K, V>> entryIterator = AbstractMap.this.entrySet().iterator();
					
					@Override
					public boolean hasNext() {
						return entryIterator.hasNext();
					}

					@Override
					public K next() {
						return entryIterator.next().getKey();
					}
				};
			}

			@Override
			public int size() {
				return AbstractMap.this.size();
			}
		};
	}
	
	/** An implementation is already provided. */
	@Override
	public final Collection<V> values() {
		return new java.util.AbstractCollection<V>() {
			@Override
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					Iterator<Map.Entry<K, V>> entryIterator = AbstractMap.this.entrySet().iterator();
					
					@Override
					public boolean hasNext() {
						return entryIterator.hasNext();
					}

					@Override
					public V next() {
						return entryIterator.next().getValue();
					}
				};
			}

			@Override
			public int size() {
				return AbstractMap.this.size();
			}
		};
	}
}