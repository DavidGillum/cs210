
//package storage;
//
//
//
//import storage.AbstractMap;
//
//
//
//import java.util.Map;
//
//import java.util.Iterator;
//
//
//
///** 
//
// * This is a concrete implementation of a hash map.
//
// */
//
//public class HashMap<K, V> extends AbstractMap<K, V> {
//
//	/*
//
//	 * TODO: This implementation does not comply with
//
//	 * the project until you implement Modules 6 to 8.
//
//	 * 
//
//	 * Before then, you can use this encapsulation of
//
//	 * the built-in hash map to support Modules 1 to 5.
//
//	 */
//
//	private Map<K, V> capsule;
//
//	
//
//	public HashMap() {
//
//		capsule = new java.util.HashMap<>();
//
//	}
//
//	
//
//	public HashMap(Map<? extends K, ? extends V> copy) {
//
//		capsule = new java.util.HashMap<>(copy);
//
//	}
//
//
//
//	@Override
//
//	public void clear() {
//
//		capsule.clear();
//
//	}
//
//
//
//	@Override
//
//	public int size() {
//
//		return capsule.size();
//
//	}
//
//
//
//	@Override
//
//	public double loadFactor() {
//
//		throw new UnsupportedOperationException();
//
//	}
//
//
//
//	@Override
//
//	public boolean isEmpty() {
//
//		return capsule.isEmpty();
//
//	}
//
//
//
//	@Override
//
//	public V put(K key, V value) {
//
//		return capsule.put(key, value);
//
//	}
//
//
//
//	@Override
//
//	public V get(Object key) {
//
//		return capsule.get(key);
//
//	}
//
//
//
//	@Override
//
//	public V remove(Object key) {
//
//		return capsule.remove(key);
//
//	}
//
//
//
//	@Override
//
//	public boolean containsKey(Object key) {
//
//		return capsule.containsKey(key);
//
//	}
//
//
//
//	@Override
//
//	public boolean containsValue(Object value) {
//
//		return capsule.containsValue(value);
//
//	}
//
//
//
//	@Override
//
//	public void putAll(Map<? extends K, ? extends V> m) {
//
//		capsule.putAll(m);
//
//	}
//
//
//
//	@Override
//
//	public String toString() {
//
//		return capsule.toString();
//
//	}
//
//
//
//	@Override
//
//	public boolean equals(Object o) {
//
//		return capsule.equals(o);
//
//	}
//
//
//
//	@Override
//
//	public int hashCode() {
//
//		return capsule.hashCode();
//
//	}
//
//
//
//	@Override
//
//	public Iterator<Map.Entry<K, V>> iterator() {
//
//		return capsule.entrySet().iterator();
//
//	}
//
//}



package storage;



import storage.AbstractMap;



import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;



/** 

 * This is a concrete implementation of a hash map.

 */

public class HashMap<K, V> extends AbstractMap<K, V> {


	Object[] capsule;
	int capacity;
	double loadFactor;
	int size;
	

	public HashMap() {
		this.capacity = 97;
		this.loadFactor = 0.75;
		this.capsule = new Entry[capacity];
	}

	

	public HashMap(Map<? extends K, ? extends V> copy) {
		
		this.capacity = 97;
		this.loadFactor = 0.75;
		this.capsule = new Object[capacity];

	}

	public HashMap(int initial_capacity) {
		this.capacity = initial_capacity;
		this.loadFactor = 0.75;
		this.capsule = new Object[capacity];
	}
	
	public HashMap(int initial_capacity, double initial_load) {
		this.capacity = initial_capacity;
		this.loadFactor = initial_load;
		this.capsule = new Object[capacity];
	}

	
	private int firstHash(Object hash) {
		if (hash instanceof String) {
			int hash_num = 0;
			for (int i = 0; i < String.valueOf(hash).length(); i++) {
				char current_char = String.valueOf(hash).charAt(i);
				if (Character.getNumericValue(current_char) == -1) {
					hash_num += i + 3 * i + 3;
				} else {
					hash_num += Character.getNumericValue(current_char) * i + 1;
				}
			}
			return hash_num;
		} else
			return hash.hashCode();
	}
	
	private int secondHash(Object hash) {
		if (hash instanceof String) {
			int hash_num = 0;
			for (int i = 0; i < String.valueOf(hash).length(); i++) {
				char currentChar = String.valueOf(hash).charAt(i);
				hash_num += (53 - Character.getNumericValue(currentChar)) * i;
			}
			return hash_num;
		} else
			return hash.hashCode();
	}
	
	@Override
	public int size() {

		return size;

	}

	@Override
	public boolean isEmpty() {
		if (size() == 0)
			return true;
		else
			return false;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return (get(key) != null);
	}
	
	
	
	
	@Override
	public void clear() {
//TODO
		//capsule.clear();

	}

	@Override

	public double loadFactor() {
		
		return 0.75;

	}


	@Override
	public V put(K key, V value) {
		if (size() >= loadFactor * capacity) {
			rebuildMap();
		}
		int hash = firstHash(key);
		int index = hash % capacity;
		int hash2 = secondHash(key);
		if (capsule[index] == null) {
			capsule[index] = new KVPair(key, value);
			size++;
			return null;
		} else if (((KVPair) capsule[index]).getKey().equals(key)) {
			if (((KVPair) capsule[index]).getValue() == null)
				size++;
			V prev = (V) ((KVPair) capsule[index]).getValue();
			((KVPair) capsule[index]).setValue(value);
			return prev;
		} else {
			for (int i = 0; i < capacity; i++) {
				int index2 = Math.abs(hash + (i + hash2)) % capacity;
				if (capsule[index2] == null) {
					capsule[index2] = new KVPair(key, value);
					size++;
					return null;
				} else if (((KVPair) capsule[index2]).getKey().equals(key)) {
					if (((KVPair) capsule[index2]).getValue() == null)
						size++;
					V prev = (V) ((KVPair) capsule[index2]).getValue();
					capsule[index2] = new KVPair(key, value);
					return prev;
				}
			}
		}
		return null;
	}



	@Override
	public V get(Object key) {
		int hash = firstHash(key);
		
		int index = hash % capacity;
		
		int hash2 = secondHash(key);
		
		if (capsule[index] == null) {
			return null;
		} else {
			
			if (((KVPair) capsule[index]).getKey().equals((K) key)) {
			
				if (((KVPair) capsule[index]).getValue() != null) {
					return (V) ((KVPair) capsule[index]).getValue();
				}
			} else {
				for (int i = 0; i < capacity; i++) {
					int index1 = Math.abs(hash + (i + hash2)) % capacity;
					if (capsule[index1] != null) {
						if (((KVPair) capsule[index1]).getKey().equals((K) key)) {
							if (((KVPair) capsule[index1]).getValue() != null) {
								return (V) ((KVPair) capsule[index1]).getValue();
							}
						}
					}
				}
			}
		}
		return null;
	}



	@Override
	public V remove(Object key) {
		int hash = firstHash(key);
		int index;
		if (size() == 0) {
			return null;
		} else
			index = hash % capacity;
		if (capsule[index] == null) {
			return null;
		} else if (((KVPair) capsule[index]).getKey().equals(key)) {
			if (((KVPair) capsule[index]).getValue() != null) {
				V return_value = (V) ((KVPair) capsule[index]).getValue();
				((KVPair) capsule[index]).setValue(null);
				size--;
				return return_value;
			}
		} else {
			int hash2 = secondHash(key);
			for (int i = 0; i < capacity; i++) {
				int index1 = Math.abs(hash + (i + hash2)) % capacity;
				if (capsule[index1] != null) {
					if (((KVPair) capsule[index1]).getKey().equals(key)) {
						if (((KVPair) capsule[index1]).getValue() != null) {
							V return_value = (V) ((KVPair) capsule[index1]).getValue();
							((KVPair) capsule[index1]).setValue(null);
							size--;
							return (V) return_value;
						}
					}
				}
			}
		}
		return null;
	}


	
	public void rebuildMap() {
		Object[] tempMap = Arrays.copyOf(capsule, capacity);
		capacity = capacity * 2;
		capsule = new Object[capacity];
		for (int i = 0; i < tempMap.length; i++) {
			if (tempMap[i] != null) {
				if (((KVPair) tempMap[i]).getValue() != null) {
					int rehash = firstHash(((KVPair) tempMap[i]).getKey());
					int new_index = rehash % capacity;
					if (capsule[new_index] == null) {
						capsule[new_index] = tempMap[i];
					} else {
						int reHash2 = secondHash(((KVPair) tempMap[i]).getKey());
						for (int j = 0; j < capacity; j++) {
							int reIndex2 = Math.abs(rehash + (j + reHash2)) % capacity;
							if (capsule[reIndex2] == null) {
								capsule[reIndex2] = tempMap[i];
								break;
							}
						}
					}
				}
			}
		}
	}
	
	@Override

	public boolean containsValue(Object value) {
//TODO
		return false;

	}



	@Override

	public void putAll(Map<? extends K, ? extends V> m) {

		Object[] keys = m.keySet().toArray();
		
		for(int i = 0; i < m.size(); i++) {
			put((K) keys[i], m.get(keys[i]));
		}

	}



	@Override

	public String toString() {

		return capsule.toString();

	}



	@Override

	public boolean equals(Object o) {

		if(capsule.toString().equals(o.toString()))
		return true;
		
		return false;

	}



	@Override

	public int hashCode() {

		return capsule.hashCode();

	}



	@Override
	public Iterator<Entry<K, V>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}


}





