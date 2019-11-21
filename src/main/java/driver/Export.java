package driver;

import model.Database;
import model.Table;
import model.Response;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import main.java.xml.jaxb3.TypedElement;

//import groovyjarjarantlr4.v4.runtime.misc.FlexibleHashMap.Entry;


public class Export extends AbstractDriver {
	private static final Pattern pattern;
	static {
		//EXPORT\s+([a-z1-9_]+)\s*(?:TO\s+([a-z1-9]+)\s*(?:\.|AS)\s*((?:xml|json)))?
		pattern = Pattern.compile(
			"EXPORT\\s+([a-z1-9_]+)\\s*(?:TO\\s+([a-z1-9]+)\\s*(?:\\.|AS)\\s*((?:xml|json)))?",
			Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public Response execute(String query, Database db) {
		Matcher matcher = pattern.matcher(query.trim());
		if (!matcher.matches())
			return null;
		
		String table_name = matcher.group(1);
		String file_name = matcher.group(2);
		String export_type = matcher.group(3);
		
		

		if( db.get(table_name) == null) {
			return new Response(false, "Table doesn't exist", null);
		}
		
		Table table = db.get(table_name);
		List<String> column_names = table.getSchema().getStringList("column_names");
		List<String> column_types = table.getSchema().getStringList("column_types");
		int pindex = (int) table.getSchema().get("primary_column");
		String primary = table.getSchema().getStringList("column_names").get(pindex);
				
			if(export_type.equals("json")) {
				writeJson(table, table_name, file_name);
			}
			
			if(export_type.equals("xml")) {
				write(table, table_name, file_name);
			}
			
			
		return new Response(true, table_name + "was exported", table);
		
	}
	
	
	
	
	public static void writeJson(Table table, String table_name, String file_name) {
		try {
			JsonObjectBuilder object_builder = Json.createObjectBuilder();
		    for (Entry<String, Object> schema_entry: table.getSchema().entrySet()) {
		    	
		    	
		    	Object schema_value = schema_entry.getValue();
				if (schema_value instanceof String) {
					object_builder.add(schema_entry.getKey(), (String) schema_value.toString());
				} else if (schema_value instanceof List) {
					List<String> columns = (List) schema_value;
					JsonArrayBuilder schema_array = Json.createArrayBuilder();
					for (String value : columns) {
						schema_array.add(value.toString());
					}
					object_builder.add(schema_entry.getKey(), schema_array);
				} else if (schema_value instanceof Integer) {
					object_builder.add(schema_entry.getKey(), (Integer) schema_value);
				}
			}
		    
		   
		    
		    for (Entry<Object, List<Object>> state_entry: table.getState().entrySet()) {
		    	
		    	
		    	Object state_value = state_entry.getValue();
				if (state_value instanceof String) {
					object_builder.add((String) state_entry.getKey(), (String) state_value);
				} 						else if (state_value instanceof List) {
					List<String> columns = (List) state_value;
					JsonArrayBuilder state_array = Json.createArrayBuilder();
					for (Object value : columns) {
						if(value == null) {
							value = "null";
						}
						if (value instanceof String) {
							state_array.add((String) value);
						} else if (value instanceof Integer) {
							state_array.add((int) value);
						} else if (value instanceof Boolean) {
							state_array.add((boolean) value);
						} 
					}
					object_builder.add((String) state_entry.getKey(), state_array);
				} else if (state_value instanceof Integer) {
					object_builder.add((String) state_entry.getKey(), (Integer) state_value);
				}
			}
		    
		    JsonObject json_root_object = object_builder.build();
		    
		    String filename = "Data/" + file_name + ".json";
			Map<String, Boolean> props = new HashMap<>();
			props.put(JsonGenerator.PRETTY_PRINTING, true);
		    JsonWriter writer = Json.createWriterFactory(props).createWriter(new FileOutputStream(filename));
		    writer.writeObject(json_root_object);
		    writer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static void write(Table table, String table_name, String file_name) {
		try {
			StringWriter output = new StringWriter();
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
			
			writer.writeStartDocument();
			writer.writeStartElement(table_name);
			writer.writeStartElement("Schema");
		    for (Entry<String, Object> schema_entry: table.getSchema().entrySet()) {
		    	
		    	Object schema_value = schema_entry.getValue();
		    	writer.writeStartElement("list");
		    	writer.writeAttribute("key", schema_entry.getKey());
		    	
		    	
				if (schema_value instanceof String) {
					writer.writeEmptyElement("element");
					writer.writeAttribute("value", (String) schema_value);
				} else if (schema_value instanceof List) {
					List<String> columns = (List) schema_value;
					JsonArrayBuilder schema_array = Json.createArrayBuilder();
					for (String element: columns) {
						writer.writeEmptyElement("element");
						writer.writeAttribute("value", element.toString());
			    	}
					
				} else if (schema_value instanceof Integer) {
					
						writer.writeEmptyElement("element");
						writer.writeAttribute("value", (String) schema_value.toString());

				}
				

				writer.writeEndElement();
		    }
			writer.writeEndElement();
			
			
			
			writer.writeStartElement("state");
		    for (Entry<Object, List<Object>> state_entry: table.getState().entrySet()) {
		    	
		    	Object state_value = state_entry.getValue();
		    	writer.writeStartElement("list");
		    	writer.writeAttribute("key", state_entry.getKey().toString());
		    	
		    	
				if (state_value instanceof String) {
					writer.writeEmptyElement("element");
					writer.writeAttribute("value", (String) state_value);
				} else if (state_value instanceof List) {
					List<String> columns = (List) state_value;
				
					for (Object element: columns) {
						if(element == null) {
							element = "null";
						}
						if (element instanceof String) {
							writer.writeEmptyElement("element");
							writer.writeAttribute("value", element.toString());
						} else if (element instanceof Integer) {
							writer.writeEmptyElement("element");
							writer.writeAttribute("value", element.toString());
						} else if (element instanceof Boolean) {
							writer.writeEmptyElement("element");
							writer.writeAttribute("value", element.toString());
						} 
						
			    	}
					
				} else if (state_value instanceof Integer) {
					
						writer.writeEmptyElement("element");
						writer.writeAttribute("value", (String) state_value.toString());

				}
				

				writer.writeEndElement();
		    }
			writer.writeEndElement();
			
			writer.writeEndElement();
			writer.writeEndDocument();
			
			writer.close();		
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    
		    String filename = "Data/" + file_name + ".xml";
		    Source from = new StreamSource(new StringReader(output.toString()));
		    Result to = new StreamResult(new FileWriter(filename));
		    transformer.transform(from, to);
		}
		catch (XMLStreamException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

