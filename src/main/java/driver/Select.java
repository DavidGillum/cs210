package driver;



import java.util.ArrayList;

import java.util.List;

import java.util.regex.Matcher;

import java.util.regex.Pattern;



import model.Database;

import model.Response;
import model.Schema;
import model.Table;



public class Select  extends AbstractDriver{

	private static final Pattern pattern;

	static {

		//SELECT\s+((?:\*|[a-z1-9_]+(?:\,\s*[a-z1-9_]*)*\s*(?:AS)?\s*(?:[a-z1-9_]*(?:\,\s*[a-z1-9_]*\s*(?:(?:AS)\s*[a-z1-9_]*)*)*)\s*))\s(?:FROM\s([a-z_]+))?\s*((?:WHERE\s+[a-z_]+\s+(?:<>|<=|>=|=|<|>)\s+\"?[a-z1-9]*\"?)*)

		pattern = Pattern.compile(

				"SELECT\\s+((?:\\*|[a-z1-9_]+(?:\\,\\s*[a-z1-9_]*)*\\s*(?:AS)?\\s*(?:[a-z1-9_]*(?:\\,\\s*[a-z1-9_]*\\s*(?:(?:AS)\\s*[a-z1-9_]*)*)*)\\s*))\\s(?:FROM\\s([a-z_]+))?\\s*((?:WHERE\\s+[a-z_]+\\s+(?:<>|<=|>=|=|<|>)\\s+\\\"?[a-z1-9]*\\\"?)*)",

				Pattern.CASE_INSENSITIVE);

	}

	@Override

	public Response execute(String query, Database db) {

		

		Matcher matcher = pattern.matcher(query.trim());

		if (!matcher.matches())

			return null;



		

		String table_name = matcher.group(2);

		

		if(table_name == null) {

			return new Response(false, "Table does not exist", null);

		}

		

		Table table = db.get(table_name);

		Schema table_schema = new Schema();
		Table newTable = new Table(table_schema
				);

		try {

		List<String> column_names = table.getSchema().getStringList("column_names");

		}catch(NullPointerException e) {

			return new Response(false, "Colimns must be valid", null);

		}

		List<String> column_names = table.getSchema().getStringList("column_names");

		List<String> column_types = table.getSchema().getStringList("column_types");

		int pindex = (int) table.getSchema().get("primary_column");

	

		if (!db.containsKey(table_name)) {

			return new Response(false, "Table does not exist", null);

		}

		

		String[] holder = matcher.group(1).split(",");

		for(int i = 0; i <holder.length; i++) {

			holder[i] = holder[i].trim();

		}

		

		ArrayList<String> input_names = new ArrayList<String>();

		ArrayList<String> alias_names = new ArrayList<String>();

		

		

		ArrayList<String> newTable_names = new ArrayList<String>();

		ArrayList<String> newTable_types = new ArrayList<String>();

		

		for(int i = 0; i < holder.length; i++) {

			if(holder[i].contains("AS") || holder[i].contains("as")) {

				String[] temp = holder[i].split(" ");

				input_names.add(temp[0]);

				alias_names.add(temp[2]);

			}

			else {

				input_names.add(holder[i]);

				alias_names.add(null);

			}

		}



		String primary = table.getSchema().getStringList("column_names").get(pindex);

		if(matcher.group(3).isEmpty()) {

			

			if(input_names.contains("*")) {

				newTable_names.addAll(column_names);

				newTable_types.addAll(column_types);

				newTable.getState().putAll(table.getState());

			}

			else {

				for(int l = 0; l < input_names.size(); l++) {

					for(int m = 0; m < column_names.size(); m++) {						

					if(input_names.get(l).equals(column_names.get(m).trim())) {

						newTable_types.add(column_types.get(m));

					}

					}

				}

			for(int i = 0; i < input_names.size(); i++) {

				

				if(alias_names.get(i) == null) {		

					newTable_names.add(input_names.get(i).trim());

				}

				else {

					newTable_names.add(alias_names.get(i).trim());

				}

					

			}

			

			int pcount = 0;

			for(int i = 0; i < newTable_names.size(); i++ ) {

				if(pcount == 0) {

				if(alias_names.get(i) == null && newTable_names.get(i).contentEquals(primary)) {

					pindex = i;

					pcount++;

				}else if(alias_names.get(i) != null && input_names.get(i).contentEquals(primary)) {

					pindex = i;

					pcount++;

			}

				}

				

				for(int j = i + 1; j < newTable_names.size(); j++ ) {

				if(newTable_names.get(i).equals(newTable_names.get(j)))	

					return new Response(false, "Cannot repeat columns without aliasing", null);

				}

			}

			

			if(pcount == 0)

			return new Response(false, "Must have a primary column", null);

		



			Object[] str = table.getState().entrySet().toArray();



			for (int j = 0; j < str.length; j++) {

				String string = str[j].toString();

				int a = string.indexOf("=");

				String temp = string.substring(0, a);

				int openBraket = string.indexOf("[");

				int closeBraket = string.indexOf("]");

				String dn = string.substring(openBraket + 1, closeBraket);

				String[] rowValues = dn.split(",");

				ArrayList<Object> row = new ArrayList<Object>();

				

				for (int k = 0; k < input_names.size(); k++) {

					for(int l = 0; l < column_names.size(); l++) {

						if(input_names.get(k).equals(column_names.get(l))) {

					rowValues[l] = rowValues[l].trim();

					if (rowValues[l].equals("null")) {

						row.add(k, null);

					} else if (rowValues[l].equals("true")) {

						row.add(k, true);

					} else if (rowValues[l].equals("false")) {

						row.add(k, false);

					} else if (column_types.get(l).toLowerCase().equals("integer")) {

						row.add(k, Integer.valueOf(rowValues[l].trim()));

					} else

						row.add(k, rowValues[l]);

					}

					}

					newTable.getState().put(temp, row);

				

				}



			}

			}

		}

		

		else {

			String[] comparisson_values = new String[3];

			String where = matcher.group(3).trim();

			String[] temp = where.split(" ");

			for(int i = 1; i < temp.length; i++) {

				comparisson_values[i-1] = temp[i];

			}

			

			String column = comparisson_values[0];

			String symbol = comparisson_values[1];

			String value = comparisson_values[2]; 

			if(value.contains("\""))

				value = value.replace("\"", "");

			

			if(input_names.contains("*")) {

				newTable_names.addAll(column_names);

				newTable_types.addAll(column_types);

				

				Object[] state = table.getState().entrySet().toArray();

				int compare_index = 0;

				

				for (int j = 0; j < state.length; j++) {

					String string = state[j].toString();

					int a = string.indexOf("=");

					String primary_value = string.substring(0, a);

					int openBraket = string.indexOf("[");

					int closeBraket = string.indexOf("]");

					String state_values = string.substring(openBraket + 1, closeBraket);

					String[] rowValues = state_values.split(",");

					ArrayList<Object> row = new ArrayList<Object>();

					String p;

					

					for(int k = 0; k < column_names.size(); k++) {

						if(column_names.get(k).equals(column)) {

							compare_index = k;

						}

					}

					

					if(column_types.get(compare_index).equals("boolean")) {

						

						if(!value.equals("true") && !value.equals("false") && !value.trim().equals("null"))

							return new Response(false, "Booleans can only be compared to booleans", null);

						

						if(symbol.trim().equals("=")) {

						

							if(rowValues[compare_index].trim().equals(value.trim())) {

								

								for(int k = 0; k < rowValues.length; k++) {

								

									if (rowValues[k].trim().equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].trim().equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].trim().equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k].trim());

									}

								newTable.getState().put(row.get(0), row);	

							}

							}

				

						 else if(symbol.trim().equals("<>")) {

							 if(value.trim().equals("null"))

									value = " null";

							 if(!rowValues[compare_index].trim().equals(value)) {

									

									for(int k = 0; k < rowValues.length; k++) {

									

										if (rowValues[k].trim().equals("null")) {

											row.add(k, null);

										} else if (rowValues[k].trim().equals("true")) {

											row.add(k, true);

										} else if (rowValues[k].trim().equals("false")) {

											row.add(k, false);

										} else if (column_types.get(k).toLowerCase().equals("integer")) {

											row.add(k, Integer.valueOf(rowValues[k].trim()));

										} else

											row.add(k, rowValues[k].trim());

										}

									newTable.getState().put(row.get(0), row);	

								}

						} else if(symbol.trim().equals("<")) {

							return new Response(false, "Invalid Boolean comparison", null);

						} else if(symbol.trim().equals("<=")) {

							return new Response(false, "Invalid Boolean comparison", null);

						} else if(symbol.trim().equals(">")) {

							return new Response(false, "Invalid Boolean comparison", null);

						} else if(symbol.trim().equals(">=")) {

							return new Response(false, "Invalid Boolean comparison", null);

						}

						

					} else if(column_types.get(compare_index).equals("integer")) {

						

						if(symbol.trim().equals("=")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								 try {

										intValue = Integer.valueOf(value);

									}catch (NumberFormatException e) {

										return new Response(false, "Integers con only be compared to integers", null);

									}

								 intValue = Integer.valueOf(value);

							}

							

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}else rowValue = intValue + 1;

							if(value.trim().equals("null"))

								intValue = rowValue + 1;

							

							

							if(intValue == rowValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<>")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								try {

									intValue = Integer.valueOf(value);

								}catch (NumberFormatException e) {

									return new Response(false, "Integers con only be compared to integers", null);

								}

								intValue = Integer.valueOf(value);

							}

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}

							if(value.trim().equals("null"))

								intValue = rowValue + 1;

							

							

							if(intValue != rowValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								try {

									intValue = Integer.valueOf(value);

								}catch (NumberFormatException e) {

									return new Response(false, "Integers con only be compared to integers", null);

								}

								intValue = Integer.valueOf(value);

							}

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}else rowValue = intValue + 1;

							if(value.trim().equals("null"))

								intValue = rowValue - 1;

							

							

							if(rowValue < intValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<=")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								try {

									intValue = Integer.valueOf(value);

								}catch (NumberFormatException e) {

									return new Response(false, "Integers con only be compared to integers", null);

								}

								intValue = Integer.valueOf(value);

							}

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}else rowValue = intValue + 1;

							if(value.trim().equals("null"))

								intValue = rowValue - 1;

							

							

							if(rowValue <= intValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals(">")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								try {

									intValue = Integer.valueOf(value);

								}catch (NumberFormatException e) {

									return new Response(false, "Integers con only be compared to integers", null);

								}

								intValue = Integer.valueOf(value);

							}

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}else intValue = rowValue + 1;

							if(value.trim().equals("null"))

								intValue = rowValue + 1;

							

							

							if(rowValue > intValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals(">=")) {

							int rowValue = 0;

							int intValue = 0;

							if(!value.trim().equals("null")) {

								try {

									intValue = Integer.valueOf(value);

								}catch (NumberFormatException e) {

									return new Response(false, "Integers con only be compared to integers", null);

								}

								intValue = Integer.valueOf(value);

							}

							if(!rowValues[compare_index].trim().equals("null")) {

								rowValue = Integer.valueOf(rowValues[compare_index].trim());

							}else intValue = rowValue + 1;

							if(value.trim().equals("null"))

								intValue = rowValue + 1;

							else try {

								Integer.valueOf(value);

							}catch (NumberFormatException e) {

								return new Response(false, "Integers con only be compared to integers", null);

							}

							

							if(rowValue >= intValue) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						}

					} else if(column_types.get(compare_index).equals("string")) {

						try {

							Integer.valueOf(value);

							return new Response(false, "Strings can only be compared to strings", null);

						}catch(NumberFormatException e) {

							

						}

						

						if(value.equals("true") || value.equals("false"))

							return new Response(false, "Strings can only be compared to strings", null);

						

						if(symbol.trim().equals("=")) {

					

							if(rowValues[compare_index].trim().compareTo(value.trim()) == 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<>")) {

							if(rowValues[compare_index].trim().compareTo(value.trim()) != 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<")) {

							if(rowValues[compare_index].trim().compareTo(value.trim()) < 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals("<=")) {

							if(rowValues[compare_index].trim().compareTo(value.trim()) <= 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals(">")) {

							if(rowValues[compare_index].trim().compareTo(value.trim()) > 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						} else if(symbol.trim().equals(">=")) {

							if(rowValues[compare_index].trim().compareTo(value.trim()) >= 0) {

								

								for(int k = 0; k < rowValues.length; k++) {

									rowValues[k] = rowValues[k].trim();

									if (rowValues[k].equals("null")) {

										row.add(k, null);

									} else if (rowValues[k].equals("true")) {

										row.add(k, true);

									} else if (rowValues[k].equals("false")) {

										row.add(k, false);

									} else if (column_types.get(k).toLowerCase().equals("integer")) {

										row.add(k, Integer.valueOf(rowValues[k].trim()));

									} else

										row.add(k, rowValues[k]);

									}

								newTable.getState().put(row.get(0), row);	

							}

						}

					}

					

				}

				

			}

		}

		

		newTable.getSchema().put("column_names", newTable_names);

		newTable.getSchema().put("column_types", newTable_types);

		newTable.getSchema().put("table_name", null);

		newTable.getSchema().put("primary_column", pindex);

		

		return new Response(true, "Chosen rows were selected from table", newTable);

	}



}