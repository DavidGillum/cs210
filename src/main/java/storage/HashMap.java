package storage;

import storage.AbstractMap;

import java.util.Map;
import java.util.Iterator;

/** 
 * This is a concrete implementation of a hash map.
 */
public class HashMap<K, V> extends AbstractMap<K, V> {
	/*
	 * TODO: This implementation does not comply with
	 * the project until you implement Modules 6 to 8.
	 * 
	 * Before then, you can use this encapsulation of
	 * the built-in hash map to support Modules 1 to 5.
	 */
	private Map<K, V> capsule;
	
	public HashMap() {
		capsule = new java.util.HashMap<>();
	}
	
	public HashMap(Map<? extends K, ? extends V> copy) {
		capsule = new java.util.HashMap<>(copy);
	}

	@Override
	public void clear() {
		capsule.clear();
	}

	@Override
	public int size() {
		return capsule.size();
	}

	@Override
	public double loadFactor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return capsule.isEmpty();
	}

	@Override
	public V put(K key, V value) {
		return capsule.put(key, value);
	}

	@Override
	public V get(Object key) {
		return capsule.get(key);
	}

	@Override
	public V remove(Object key) {
		return capsule.remove(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return capsule.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return capsule.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		capsule.putAll(m);
	}

	@Override
	public String toString() {
		return capsule.toString();
	}

	@Override
	public boolean equals(Object o) {
		return capsule.equals(o);
	}

	@Override
	public int hashCode() {
		return capsule.hashCode();
	}

	@Override
	public Iterator<Map.Entry<K, V>> iterator() {
		return capsule.entrySet().iterator();
	}
}