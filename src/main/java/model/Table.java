package model;

/** 
 * This class is an encapsulation of the
 * schema and state of a table.
 * 
 * Additional features may be implemented.
 */
public class Table {
	/** Do not modify. **/
	private Schema schema;
	
	/** Do not modify. **/
	public Schema getSchema() {
		return schema;
	}
	
	/** Do not modify. **/
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	/** Do not modify. **/
	private State state;
	
	/** Do not modify. **/
	public State getState() {
		return state;
	}
	
	/** Do not modify. **/
	public void setState(State state) {
		this.state = state;
	}
	
	/** Do not modify. **/
	public Table() {
		setSchema(new Schema());
		setState(new State());
	}
	
	/** Do not modify. **/
	public Table(Schema schema, State state) {
		setSchema(new Schema(schema));
		setState(new State(state));
	}
}