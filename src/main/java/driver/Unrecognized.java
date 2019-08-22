package driver;

import model.Database;
import model.Response;

/** 
 * Do not remove this driver.
 * 
 * Example:
 * 	NONSENSE QUERY ASDF
 * 
 * Response:
 * 	failure flag
 * 	message "Unrecognized query"
 * 	no result table
 */
public class Unrecognized extends AbstractDriver {
	@Override
	public Response execute(String query, Database db) {
		return new Response(false, "Unrecognized query", null);
	}
}
