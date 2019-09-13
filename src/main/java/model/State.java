package model;

import storage.HashMap;

import java.util.List;

/** 
 * This class is a hash map alias providing
 * a Primary Column Value -> Row of Values mapping.
 * 
 * Additional features may be implemented.
 */
public class State extends HashMap<Object, List<Object>> {
	/** Do not modify. **/
	public State() {
		super();
	}
	
	/** Do not modify. **/
	public State(State state) {
		super(state);
	}
}