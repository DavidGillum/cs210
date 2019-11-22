package storage;

import java.util.Map;
import java.util.List;

/** 
 * This abstract class defines the protocols for 
 * a page of records complying with the project.
 * 
 * Do not modify the protocols.
 * 
 * You do not need to change any implementation
 * that is already provided for you.
 * 
 * Additional features should be implemented in a
 * concrete subclass, not in this abstract class.
 */
public abstract class AbstractPage {
	/**
	 * Returns the length of this page. The length is
	 * the number of records allocated in the file.
	 * 
	 * @return the length of this page
	 */
	public abstract int length();
	
	/**
	 * Updates the length of this page. The length is
	 * the number of records allocated in the file.
	 * 
	 * For all methods taking an index as a parameter,
	 * the index must be less than the length.
	 * 
	 * @param length the new length of this page
	 */
	public abstract void length(int length);
	
	/**
	 * Returns the size of this page. The size is
	 * the number of records that store entries.
	 * 
	 * @return the size of this page
	 */
	public abstract int size();
	
	/**
	 * Updates the size of this page. The size is
	 * the number of records that store entries.
	 * 
	 * @param size the new size of this page
	 * 
	 * @throws IllegalArgumentException if
	 * the size exceeds the length
	 */
	public abstract void size(int size);

	/**
	 * Stores an entry in the record at
	 * the given index.
	 * 
	 * @param index the index of the record
	 * @param entry the entry to store in the record
	 * 
	 * @throws IllegalArgumentException if
	 * the index is invalid
	 * 
	 * @throws IllegalArgumentException if
	 * the entry or its value are null; 
	 * its value size doesn't match the number of fields; or
	 * its key doesn't match its value at the key index
	 */
	public abstract void write(int index, Map.Entry<Object, List<Object>> entry);
	
	/**
	 * Marks the record at the given index as null.
	 * 
	 * @param index the index of the record
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 */
	public abstract void writeNull(int index);
	
	/**
	 * Marks the record at the given index as removed.
	 * 
	 * If your collision resolution technique marks
	 * removed entries with a non-null sentinel,
	 * support this operation by overriding it.
	 * Otherwise an implementation is already provided.
	 * 
	 * @param index the index of the record
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 */
	public void writeRemoved(int index) {
		throw new UnsupportedOperationException();
	};
	
	/**
	 * Returns the entry stored in the record at
	 * the given index.
	 * 
	 * @param index the index of the record
	 * @return the entry stored in the record
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 * 
	 * @throws IllegalStateException if
	 * there is no entry stored in the record
	 */
	public abstract Map.Entry<Object, List<Object>> read(int index);

	/**
	 * Returns <code>true</code> if the record at
	 * the given index stores an entry.
	 * 
	 * @param index the index of the record
	 * @return <code>true</code> if the record stores an entry
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 */
	public abstract boolean isEntry(int index);
	
	/**
	 * Returns <code>true</code> if the record at
	 * the given index is null.
	 * 
	 * @param index the index of the record
	 * @return <code>true</code> if the record is null
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 */
	public abstract boolean isNull(int index);
	
	/**
	 * Returns <code>true</code> if the record at
	 * the given index is removed.
	 * 
	 * If your collision resolution technique marks
	 * removed entries with a non-null sentinel,
	 * support this operation by overriding it.
	 * Otherwise an implementation is already provided.
	 * 
	 * @param index the index of the record
	 * @return <code>true</code> if the record is removed
	 * 
	 * @throws IndexOutOfBoundsException if
	 * the index is invalid
	 */
	public boolean isRemoved(int index) {
		throw new UnsupportedOperationException();
	};
}