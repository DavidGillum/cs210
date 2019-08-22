package model;

/** 
 * This class is an encapsulation of the 
 * data for a driver response to a query.
 * 
 * Additional features may be implemented.
 */
public class Response {
	/** Do not modify. **/
	private boolean success;
	
	/** Do not modify. **/
	private String message;
	
	/** Do not modify. **/
	private Table table;
	
	/** Do not modify. **/
    public Response(boolean success, String message, Table table) {
    	this.success = success;
    	this.message = message;
    	this.table = table;
    }

	/** Do not modify. **/
    public boolean getSuccess() {
    	return success;
    }

	/** Do not modify. **/
    public String getMessage() {
    	return message;
    }

	/** Do not modify. **/
    public Table getTable() {
    	return table;
    }
}