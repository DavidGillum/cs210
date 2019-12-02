package driver;

import model.Database;
import model.Table;
import model.Response;
import model.Schema;

import java.util.List;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Do not remove this driver.
 * 
 * Examples:
 * 	RANGE 5
 * 	RANGE 3 AS x
 * 
 * Response 1:
 * 	success flag
 * 	no message
 * 	result table
 * 		primary integer column "number"
 *		rows [0]; [1]; [2]; [3]; [4]
 * 
 * Response 2:
 * 	success flag
 * 	no message
 * 	result table
 * 		primary integer column "x"
 *		rows [0]; [1]; [2]
 */
public class Range extends AbstractDriver {
	private static final Pattern pattern;
	static {
		pattern = Pattern.compile(
			"RANGE\\s+([0-9]+)(?:\\s+AS\\s+(\\w+))?",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches()) return null;

		int upper = Integer.parseInt(matcher.group(1));
		String name = matcher.group(2) != null ? matcher.group(2) : "number";
		
		Schema schema = new Schema();
		schema.put("table_name", null);
		
		List<String> names = new LinkedList<>();
		names.add(name);
		schema.put("column_names", names);
		
		List<String> types = new LinkedList<>();
		types.add("integer");
		schema.put("column_types", types);
		
		schema.put("primary_column", 0);
		
		Table table = new Table(schema);
		for (int i = 0; i < upper; i++) {
			List<Object> row = new LinkedList<>();
			row.add(i);
			table.getState().put(i, row);
		}
		
		return new Response(true, null, table);
	}
}
