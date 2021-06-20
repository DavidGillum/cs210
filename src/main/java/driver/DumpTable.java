package driver;



import java.util.regex.Matcher;

import java.util.regex.Pattern;



import model.Database;

import model.Response;
import model.Schema;
import model.Table;



public class DumpTable extends AbstractDriver {



	private static final Pattern pattern;

	static {

		//DUMP\s+TABLE\s+([a-z][\w]*)

		pattern = Pattern.compile(

			"DUMP\\s+TABLE\\s+([a-z][\\w]*)",

			Pattern.CASE_INSENSITIVE

		);

	}

	

	@Override

	public Response execute(String query, Database db) {

		Matcher matcher = pattern.matcher(query.trim());

		if (!matcher.matches()) return null;

		if(!db.containsKey(matcher.group(1))){

			return new Response(false, "Table does not exist", null);

		}
		Schema table_schema = new Schema();
		Table table = new Table(table_schema);

		

		table = db.get(matcher.group(1));

		

		return new Response(true, " ", table);

	}



}
