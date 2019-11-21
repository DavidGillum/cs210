package driver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Database;
import model.Response;
import model.Table;

public class InsertIntoTable extends AbstractDriver {
	private static final Pattern pattern;
	static {
		// (INSERT|REPLACE)\s+INTO\s+([a-z][\w]*)\s+(?:\(((?:[\w]+(?:\s*\,\s*[\w]*)*))?\))?\s*VALUES\s*\(((?:[\"\w\W]+(?:\s*\,\s*[\"\w\W]*)*))?\)
		pattern = Pattern.compile(
				"(INSERT|REPLACE)\\s+INTO\\s+([a-z][\\w]*)\\s+(?:\\(((?:[\\w]+(?:\\s*\\,\\s*[\\w]*)*))?\\))?\\s*VALUES\\s*\\(((?:[\\\"\\w\\W]+(?:\\s*\\,\\s*[\\\"\\w\\W]*)*))?\\)",
				Pattern.CASE_INSENSITIVE);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches())
			return null;

		String table_name = matcher.group(2);
		Table table = db.get(table_name);
		Table newTable = new Table();
		
		if (!db.containsKey(table_name)) {
			return new Response(false, "Table does not exist", null);
		}

		
		List<String> column_names = table.getSchema().getStringList("column_names");
		
		List<String> column_types = table.getSchema().getStringList("column_types");

		ArrayList<String> input_names = new ArrayList<String>();
		ArrayList<String> input_values = new ArrayList<String>();

		

		int pindex = (int) table.getSchema().get("primary_column");

		String primary = table.getSchema().getStringList("column_names").get(pindex);
		
		int pCount = 0;
		String pValue = null;

		ArrayList<Object> row = new ArrayList<Object>();

		if (matcher.group(3) != null) {
			String[] columns = matcher.group(3).split(",");
			String[] vals = matcher.group(4).split(",");
			if (columns.length == vals.length) {
				for (int i = 0; i < columns.length; i++) {
					if (!input_names.contains(columns[i].trim())) {

						input_names.add(columns[i].trim());
						input_values.add(vals[i].trim());
					} else
						return new Response(false, "The column name was repeated", null);
				}
			}
		}
		if(matcher.group(3) == null && !(matcher.group(4) == null)) {
			String[] vals = matcher.group(4).split(",");
			for(int i = 0; i < vals.length; i++) {
				input_values.add(vals[i].trim());
			}
		}

		if (matcher.group(4) == null)
			return new Response(false, "there are no columns", null);

		
		if (matcher.group(1).equals("INSERT")) {

			if (!(matcher.group(3) == null)) {
				
				boolean flag = false;
				for (int i = 0; i < column_names.size(); i++) {
					for (int j = 0; j < input_names.size(); j++) {

						if (input_names.get(j).trim().equals(column_names.get(i).trim())) {

							flag = true;

							String currentVal = input_values.get(j).trim();
							

							if (input_names.get(j).equals(primary)) {
								
								if (currentVal.equals(null))
									return new Response(false, "must have primary value", null);
								if (table.getState().keySet().contains(currentVal.replace("\"", "")) && column_types.get(i).toLowerCase().equals("string"))
									return new Response(false, "primary value cannot be repeated", null);
								else if(column_types.get(i).toLowerCase().equals("integer") && !currentVal.equals("null") && table.getState().keySet().contains(Integer.valueOf(currentVal)))
									return new Response(false, "primary value cannot be repeated", null);
								else if(column_types.get(i).toLowerCase().equals("boolean") && table.getState().keySet().contains(Boolean.parseBoolean(currentVal)))
										return new Response(false, "primary value cannot be repeated", null);
								pValue = currentVal;
								pCount++;
								if (pCount > 1)
									return new Response(false, "can only have one primary column", null);
								
					
							}

							if (currentVal.toLowerCase().equals("null")) {
								row.add(null);
							} else if (column_types.get(i).toLowerCase().equals("boolean")) {
								if (currentVal.toLowerCase().equals("true"))
									row.add(true);
								else if (currentVal.toLowerCase().equals("false"))
									row.add(false);
								else
									return new Response(false, "cannot add non boolean", null);
							}

							else if (column_types.get(i).toLowerCase().equals("integer")) {

								if (currentVal.trim().charAt(0) == '0' && currentVal.length() > 1)
									return new Response(false, "can't have leading zeros", null);
								else if (currentVal.contains("."))
									return new Response(false, "can't have decimal points", null);
								else
									try {

										row.add(Integer.valueOf(currentVal));
									} catch (NumberFormatException e) {
										return new Response(false, "Only integers can be put in an integer column",
												null);
									}
							}

							if (column_types.get(i).toLowerCase().equals("string")) {

								if (currentVal.charAt(0) == '"') {
									if (currentVal.substring(1, currentVal.length() - 1).contains("\""))
										return new Response(false, "Cannot contail \"", null);
									if (currentVal.contains("\"")) {
										row.add(currentVal.substring(1, currentVal.length() - 1));
									}
								} else
									return new Response(false, "Can only put a string in the string column ", null);
							}

						}
					}
					if (flag == false) {
						row.add(null);
					}
					flag = false;

				}
				if (pCount == 0)
					return new Response(false, "Must have a primary column", null);

			} 
			
			if(matcher.group(3) == null) {
				
				boolean flag = false;
				if(input_values.size() != column_names.size())
					return new Response(false, "Must have equal values to schema columns", null);
				for (int i = 0; i < column_names.size(); i++) {
					
					String currentVal = input_values.get(i).trim();
					if (table.getState().keySet().contains(currentVal.replace("\"", "")) && column_types.get(i).toLowerCase().equals("string"))
						return new Response(false, "primary value cannot be repeated", null);
					if (currentVal.toLowerCase().equals("null")) {
						row.add(null);
					} else if (column_types.get(i).toLowerCase().equals("boolean")) {
						if (currentVal.toLowerCase().equals("true"))
							row.add(true);
						else if (currentVal.toLowerCase().equals("false"))
							row.add(false);
						else
							return new Response(false, "cannot add non boolean", null);
					}

					else if (column_types.get(i).toLowerCase().equals("integer")) {

						if (currentVal.trim().charAt(0) == '0' && currentVal.length() > 1)
							return new Response(false, "can't have leading zeros", null);
						else if (currentVal.contains("."))
							return new Response(false, "can't have decimal points", null);
						else
							try {

								row.add(Integer.valueOf(currentVal));
							} catch (NumberFormatException e) {
								return new Response(false, "Only integers can be put in an integer column",
										null);
							}
					}

					if (column_types.get(i).toLowerCase().equals("string")) {

						if (currentVal.charAt(0) == '"') {
							if (currentVal.substring(1, currentVal.length() - 1).contains("\""))
								return new Response(false, "Cannot contain \"", null);
							if (currentVal.contains("\"")) {
								row.add(currentVal.substring(1, currentVal.length() - 1));
							}
						} else
							return new Response(false, "Can only put a string in the string column ", null);
					}	
					
					
				}
				
			}
		}
		if (matcher.group(1).equals("REPLACE")) {

			if (!(matcher.group(3) == null)) {
				
				
				String primary_name = "";
				for (int i = 0; i < column_names.size(); i++) {
					for (int j = 0; j < input_names.size(); j++) {
						
						if (input_names.get(j).trim().equals(column_names.get(i).trim())) {

						
							
							String currentVal = input_values.get(j).trim();
							
							if(column_types.get(pindex).toLowerCase().equals("string")) {
								row = (ArrayList<Object>) table.getState().get(input_values.get(pindex).substring(1, input_values.get(pindex).length()-1));
							}
							else if(column_types.get(pindex).toLowerCase().equals("integer"))
									row = (ArrayList<Object>) table.getState().get(Integer.parseInt(input_values.get(pindex)));
							else if(column_types.get(pindex).toLowerCase().equals("boolean"))
								row = (ArrayList<Object>) table.getState().get(Boolean.parseBoolean(input_values.get(pindex)));
						
							
							
							if (input_names.get(j).equals(primary)) {
								
								pCount++;
								
							}
							
								
							else if (column_types.get(i).toLowerCase().equals("boolean")) {
								if (currentVal.toLowerCase().equals("true")) {
									
									row.set(i, true);
								}
								else if (currentVal.toLowerCase().equals("false")) {
									
									row.set(i, false);
								}
								else
									return new Response(false, "cannot add non boolean", null);
							}

							else if (column_types.get(i).toLowerCase().equals("integer")) {

								if (currentVal.trim().charAt(0) == '0' && currentVal.length() > 1)
									return new Response(false, "can't have leading zeros", null);
								else if (currentVal.contains("."))
									return new Response(false, "can't have decimal points", null);
								else
									try {
										//table.getState().get(primary_name).set(i, Integer.valueOf(currentVal));
										row.set(i, Integer.valueOf(currentVal));
										
									} catch (NumberFormatException e) {
										return new Response(false, "Only integers can be put in an integer column",
												null);
									}
							}

							else if (column_types.get(i).toLowerCase().equals("string")) {
								
								if (currentVal.charAt(0) == '"') {
									if (currentVal.substring(1, currentVal.length() - 1).contains("\""))
										return new Response(false, "Cannot contail \"", null);
									if (currentVal.contains("\"")) {
										row.set(i, currentVal.substring(1, currentVal.length() - 1));
									}
									
								} else
									return new Response(false, " ", null);
								
							}
							
					}
				}
			}
				if (pCount == 0)
					return new Response(false, "Must have a primary column", null);
		}
			if (matcher.group(3) == null) {
				
				if(input_values.size() != column_names.size())
					return new Response(false, "Must have equal values to schema columns", null);
				for (int i = 0; i < column_names.size(); i++) {
					
					String currentVal = input_values.get(i).trim();
					
					if(column_types.get(pindex).toLowerCase().equals("string")) {
						row = (ArrayList<Object>) table.getState().get(input_values.get(pindex).substring(1, input_values.get(pindex).length()-1));
					}
					else if(column_types.get(pindex).toLowerCase().equals("integer"))
							row = (ArrayList<Object>) table.getState().get(Integer.parseInt(input_values.get(pindex)));
					else if(column_types.get(pindex).toLowerCase().equals("boolean"))
						row = (ArrayList<Object>) table.getState().get(Boolean.parseBoolean(input_values.get(pindex)));
				
					if (column_names.get(i).equals(primary)) {
						
						pCount++;
						
					}
					else if (column_types.get(i).toLowerCase().equals("boolean")) {
						if (currentVal.toLowerCase().equals("true")) {
							
							row.set(i, true);
						}
						else if (currentVal.toLowerCase().equals("false")) {
							
							row.set(i, false);
						}
						else
							return new Response(false, "cannot add non boolean", null);
					}

					else if (column_types.get(i).toLowerCase().equals("integer")) {

						if (currentVal.trim().charAt(0) == '0' && currentVal.length() > 1)
							return new Response(false, "can't have leading zeros", null);
						else if (currentVal.contains("."))
							return new Response(false, "can't have decimal points", null);
						else
							try {
								//table.getState().get(primary_name).set(i, Integer.valueOf(currentVal));
								row.set(i, Integer.valueOf(currentVal));
								
							} catch (NumberFormatException e) {
								return new Response(false, "Only integers can be put in an integer column",
										null);
							}
					}

					else if (column_types.get(i).toLowerCase().equals("string")) {
						
						if (currentVal.charAt(0) == '"') {
							if (currentVal.substring(1, currentVal.length() - 1).contains("\""))
								return new Response(false, "Cannot contail \"", null);
							if (currentVal.contains("\"")) {
								row.set(i, currentVal.substring(1, currentVal.length() - 1));
							}
							
						} else
							return new Response(false, " ", null);
						
					}
				}
				
			}
		}
		
		
		if(row.get(pindex) == null)
			return new Response(false, "Primary value cannot be null", null);
		newTable.getSchema().put("column_names", column_names);
		table.getState().put(row.get(pindex), row);
		newTable.getState().put(row.get(pindex), row);
		newTable.getSchema().put("column_types", column_types);
		newTable.getSchema().put("table_name", null);
		newTable.getSchema().put("primary_column", pindex);

		return new Response(true, "Row was inserted into the table", newTable);
	}
}
