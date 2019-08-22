package driver;

import model.Database;
import model.Response;

/** 
 * This abstract class defines the protocol for
 * query drivers compatible with the Server.
 * 
 * Additional features may be implemented.
 */
public abstract class AbstractDriver {
	/** Do not modify. **/
	public abstract Response execute(String query, Database db);

	/** Do not modify. **/
	public final Response execute(String query) {
		return execute(query, null);
	}
}
