package driver;

import model.Database;
import model.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Do not remove this driver.
 * 
 * Example:
 * 	ECHO "Hello, world!"
 * 
 * Response:
 * 	success flag
 * 	message "Hello, world!"
 * 	no result table
 */
public class Echo extends AbstractDriver {
	private static final Pattern pattern;
	static {
		pattern = Pattern.compile(
			"ECHO\\s+\"([^\"]*)\"",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches()) return null;

		String text = matcher.group(1);
		
		return new Response(true, text, null);
	}
}
