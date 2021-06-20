package driver;

import model.Database;
import model.Table;
import model.Response;

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
public class SquaresBelow extends AbstractDriver {
	private static final Pattern pattern;
	static {
		//SQUARES\s+BELOW\s+([0-9]+)(?:\s+AS\s+([a-z])(?:\s*,\s*([a-z]))?)?
		pattern = Pattern.compile(
			"SQUARES\\s+BELOW\\s+([0-9]+)(?:\\s+AS\\s+([a-z])(?:\\s*,\\s*([a-z]))?)?",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches()) return null;

		int upper = Integer.parseInt(matcher.group(1));
		
		Table table = new Table(null);
		String column_number;
		String column_square;
		
		
		
		if(matcher.group(2) == null && matcher.group(3) == null) {			
		column_number = "x";
		column_square = "x_squared";
		}
		
		else if(matcher.group(2) != null && matcher.group(3) == null) {
			column_number = matcher.group(2);
			column_square = matcher.group(2) + "_squared";
		}
		else if(matcher.group(2).equals(matcher.group(3))){
		return new Response(false, "Column one and two cannot have same name", table);
				}
		else {
			column_number = matcher.group(2);	
			column_square = matcher.group(3);
		}
		
		
		
		table.getSchema().put("table_name", null);
		
		List<String> names = new LinkedList<>();
		names.add(column_number);
		names.add(column_square);
		table.getSchema().put("column_names", names);
		
		List<String> types = new LinkedList<>();
		types.add("integer");
		types.add("integer");
		table.getSchema().put("column_types", types);
		
		table.getSchema().put("primary_column", 0);
		
		for (int i = 0; i*i < upper; i++) {
			List<Object> row = new LinkedList<>();
			row.add(i);
			row.add(i*i);
			table.getState().put(i, row);
		}
		
		return new Response(true, null, table);
	}

}
