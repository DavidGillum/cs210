package storage;

import java.awt.RenderingHints.Key;
import java.util.Map;
import java.util.Map.Entry;

public class KVPair<K, V> implements Map.Entry<K, V> {

	private K key;
	private V value;

	public KVPair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V v) {
		V old_value = value;
		value = v;
		return old_value;
	}

	@Override
	public int hashCode() {
		return (this.getKey() == null ? 0 : this.getKey().hashCode())
				^ (this.getValue() == null ? 0 : this.getValue().hashCode());

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Entry) {
			Entry<K, V> ob = (Entry<K, V>) o;
			return (this.getKey() == null ? ob.getKey() == null : this.getKey().equals(ob.getKey()))
					&& (this.getValue() == null ? ob.getValue() == null : this.getValue().equals(ob.getValue()));
		}
		return false;
	}

}