package driver;

import model.Database;
import model.Table;
import model.Response;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class Import extends AbstractDriver {
	private static final Pattern pattern;
	static {
		//IMPORT\s+([a-z1-9]+)\.(xml|json)\s*(?:TO\s+([a-z1-9_]+))?
		pattern = Pattern.compile(
			"IMPORT\\s+([a-z1-9]+)\\.(xml|json)\\s*(?:TO\\s+([a-z1-9_]+))?",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		
		if(!matcher.matches())
			return null;
		
		String table_name = matcher.group(3);
		Table table = new Table();
		String file_name = matcher.group(1);
		
		if(table_name == null) {
			if(matcher.group(2).equals("json")) {
	
				Map<String, Object> result = null;
				try {
					String filename = "Data/" + file_name + ".json";
					JsonReader reader = Json.createReader(new FileInputStream(filename));
				    JsonObject json_object = reader.readObject();
				    reader.close();

				    result = new HashMap<>();
				    for (String key: json_object.keySet()) {
				    	
				    	//table.getSchema().put(key, json_object.g);
				    	

				    	for (int i = 0; i < json_object.entrySet().size(); i++) {
			    	
				    		try {
					    		table.getSchema().put(key, json_object.getBoolean(key));
					    	}catch(ClassCastException e) {
					    		
					    	} 
					    	try {
					    		table.getSchema().put(key, json_object.getInt(key));
					    	}catch(ClassCastException e) {
					    		
					    	}try {
					    		table.getSchema().put(key, json_object.getString(key));
					    	}catch(ClassCastException e) {
					    		
					    	}
				    		
				    	}
				    	//table_schema.;
				    }
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(matcher.group(2).equals("xml")) {
				table.getSchema().put("table_name", file_name);
				try {
					String filename = "Data/"+ file_name + ".xml";
					XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileReader(filename));
					
					Entry<String, Object> result = null;
					List<Object> list = null;
					String value = null;
					Object put_value = null;
					String key = null;
					while (reader.hasNext()) {
						reader.next();
						
						
						if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
							
							if (reader.getLocalName().equalsIgnoreCase("Schema")) {
								
							
							 if (reader.getLocalName().equalsIgnoreCase("list")) {
								list = new LinkedList<>();
								key = reader.getAttributeValue(null, "list");
								
							}
							else if (reader.getLocalName().equalsIgnoreCase("element")) {
								
								value = reader.getAttributeValue(null, "value");
								
								
									
								
							}
							}
						}
						
						if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {
							if (reader.getLocalName().equalsIgnoreCase("set")) {
								//return new Response(true, file_name + " was imported", table);
								
							if (reader.getLocalName().equalsIgnoreCase("list")) {
								key = null;
//								result.setValue(list);
								list = null;
							}
							else if (reader.getLocalName().equalsIgnoreCase("element")) {
//								table.getSchema().put(key, value);
								list.add(value);
								value = null;
							}
							}
						}
					}
					reader.close();
				}
				catch (XMLStreamException e) {
					e.printStackTrace();
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new Response(true, file_name + " was imported", table);
		
	}
	
	
	
}


