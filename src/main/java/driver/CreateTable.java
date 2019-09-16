package driver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Database;
import model.Response;
import model.Table;

public class CreateTable extends AbstractDriver {
	private static final Pattern pattern;
// CREATE\s+TABLE\s+([a-z][\w]*)\s+\(\s*([\w]+\s+(?:INTEGER|STRING|BOOLEAN)\s*(?:PRIMARY)?(?:\s*\,\s*[\w]+\s+(?:INTEGER|STRING|BOOLEAN)\s*(?:PRIMARY)?)*)\)
	static {
		pattern = Pattern.compile(
			"CREATE\\s+TABLE\\s+([a-z][\\w]*)\\s+\\(\\s*([\\w]+\\s+(?:INTEGER|STRING|BOOLEAN)\\s*(?:PRIMARY)?(?:\\s*\\,\\s*[\\w]+\\s+(?:INTEGER|STRING|BOOLEAN)\\s*(?:PRIMARY)?)*)\\)",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches()) return null;
	
		Table table =new Table();
		
		String table_name = matcher.group(1);
		
		if(db.containsKey(table_name))
			return new Response(false, "Table already exists", null);
		
		String[] col_defn = matcher.group(2).split(",");
		
		if(table.getSchema().get(table_name)== null)
		table.getSchema().put("table_name", table_name);
		else return null;
		
		LinkedList<String> col_names = new LinkedList<String>();
		LinkedList<String> col_types = new LinkedList<String>();
		int primary_col = 0;
		int primary_count = 0;
		
		for(int i = 0; i < col_defn.length; i++) {
			col_defn[i] = col_defn[i].trim();
		String[] temp = col_defn[i].split(" ");
		if(temp.length == 3) {
			primary_col = i;
			primary_count++;
		}
		col_names.add(temp[0]);
		col_types.add(temp[1].toLowerCase());
		}
		if(primary_count ==0)
			return null;
		if(primary_count > 1)
			return null;
		table.getSchema().put("column_names", col_names);
		table.getSchema().put("column_types", col_types);
		table.getSchema().put("primary_column", primary_col);
		db.put(table_name, table);
		
		return new Response(true, null, table);
	}
}

