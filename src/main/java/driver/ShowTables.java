package driver;



import java.util.ArrayList;

import java.util.LinkedList;

import java.util.List;

import java.util.regex.Matcher;

import java.util.regex.Pattern;



import model.Database;

import model.Response;
import model.Schema;
import model.Table;



public class ShowTables extends AbstractDriver {

	private static final Pattern pattern;

	static {

		

		pattern = Pattern.compile(

			"SHOW\\s+TABLES",

			Pattern.CASE_INSENSITIVE

		);

	}



	@Override

	public Response execute(String query, Database db) {

		

		Matcher matcher = pattern.matcher(query.trim());

		if(!matcher.matches())

			return null;

		Schema table_schema = new Schema();
		Table table = new Table(table_schema);

		List<String> columnNames = new ArrayList<String>();

		List<String> columnTypes = new ArrayList<String>();

		columnNames.add("table_name");

		columnNames.add("row_count");

		columnTypes.add("string");

		columnTypes.add("integer");

		table.getSchema().put("column_names", columnNames);

		table.getSchema().put("column_types", columnTypes);

		table.getSchema().put("primary_column", 0);

		table.getSchema().put("table_name", null);

		

		for(Table t : db.values()) {

			List<Object> row = new LinkedList<>();

			row.add(t.getSchema().get("table_name"));

			row.add(t.getState().size());

			table.getState().put(t.getSchema().get("table_name"), row);

		

		}



		

		return new Response(true, " ", table);

	}



}