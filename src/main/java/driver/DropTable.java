package driver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.validation.Schema;

import model.Database;
import model.Response;
import model.Table;

public class DropTable extends AbstractDriver {
	private static final Pattern pattern;
	static {
		//Drop\s+TABLE\s+([a-z][\w]*)
		pattern = Pattern.compile(
			"Drop\\s+TABLE\\s+([a-z][\\w]*)",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches()) return null;

		Table table = new Table();
		
		String table_name = matcher.group(1);
		if(db.containsKey(table_name))
			return new Response(true, table_name+ " had "+ db.get(table_name) + "rows", db.remove(table_name));
		else return new Response(false, "Table doesn't exist.", null);
	}

}
