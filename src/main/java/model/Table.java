package model;

/** 
 * This class is an encapsulation of the
 * schema and state of a table.
 * 
 * Additional features may be implemented.
 */
public class Table {
	/**
	 * The schema of this table, which is only
	 * assigned during a constructor call.
	 * 
	 * Do not modify. 
	 **/
	private Schema schema;
	
	/** 
	 * Returns the schema of this table.
	 * 
	 * Do not modify.
	 * 
	 * @return the table schema
	 **/
	public Schema getSchema() {
		return schema;
	}
	
	/** 
	 * Returns <code>true</code> if this table
	 * is stored and therefore has a name.
	 * 
	 * Do not modify.
	 * 
	 * @return <code>true</code> if stored
	 **/
	public boolean isStored() {
		return schema.getString("table_name") != null;
	}
	
	/** 
	 * Returns <code>true</code> if this table
	 * is computed and therefore has no name.
	 * 
	 * Do not modify.
	 * 
	 * @return <code>true</code> if stored
	 **/
	public boolean isComputed() {
		return schema.getString("table_name") == null;
	}
	
	/**
	 * The state of this table, which is only
	 * assigned during a constructor call.
	 * 
	 * Do not modify. 
	 **/
	private State state;
	
	/** 
	 * Returns the state of this table.
	 * 
	 * Do not modify.
	 * 
	 * @return the table state
	 **/
	public State getState() {
		return state;
	}
	
	/** 
	 * Constructs a new table with an existing schema
	 * and a new empty state.
	 * 
	 * To use a copy of an existing schema, call the
	 * schema copy constructor on that existing schema,
	 * then pass the copy into this constructor.
	 * 
	 * For Module 7, if and only if the table is
	 * stored then the new empty state should be
	 * a persistent state.
	 * 
	 * Do not modify, except for Module 7 as above.
	 * 
	 * @param schema an existing schema
	 **/
	public Table(Schema schema) {
		this.schema = schema;
		this.state = new State();
	}
	
	/** 
	 * Constructs a new table with an existing schema
	 * and an existing state.
	 * 
	 * To use a copy of an existing schema or state,
	 * call the schema or state copy constructor on
	 * that existing schema or state, then pass the
	 * copy or copies into this constructor.
	 * 
	 * For Module 7, if and only if the table is stored
	 * then the existing state should be copied into
	 * a persistent state.
	 * 
	 * Do not modify, except for Module 7 as above.
	 * 
	 * @param schema an existing schema
	 * @param state an existing state
	**/
	public Table(Schema schema, State state) {
		this.schema = schema;
		this.state = state;
	}
}