package model;

import storage.HashMap;

import java.util.List;

/** 
 * This class is a hash map alias providing
 * a Property Name -> Property Value mapping.
 * 
 * Additional features may be implemented.
 */
@SuppressWarnings("unchecked")
public class Schema extends HashMap<String, Object> {
	/** Do not modify. **/
    public Schema() {
    	super();
    }
    
    /** Do not modify. **/
    public Schema(Schema schema) {
    	super(schema);
    }
    
    /** Do not modify. **/
    public String getString(String key) {
    	return (String) get(key);
    }
    
    /** Do not modify. **/
	public List<String> getStringList(String key) {
    	return (List<String>) get(key);
    }
    
    /** Do not modify. **/
    public Integer getInteger(String key) {
    	return (Integer) get(key);
    }
}