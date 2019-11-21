package core;

import model.Response;
import model.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/** 
 * This class implements a user console for
 * interacting with a DBMS server.
 * 
 * Finish implementing the required features
 * but do not modify the protocols.
 */
public class Console {
	/**
	 * This is an entry point for running the
	 * console as a stand-alone program.
	 */
	public static void main(String[] args) throws IOException {
		final Server srv = new Server();
		final InputStream in_stream = System.in;
		final OutputStream out_stream = System.out;
		
		prompt(srv, in_stream, out_stream);
		
		srv.close();
		in_stream.close();
		out_stream.close();
	}
	
	public static void prompt (Server server, InputStream in_stream, OutputStream out_stream) {
		final Scanner in = new Scanner(in_stream);
		final PrintStream out = new PrintStream(out_stream);
		
		/*
		 * TODO: Use a REPL to prompt the user for inputs,
		 * and send each input to the server for parsing.
		 * No inputs are to be parsed in the console, except
		 * for the case-insensitive sentinel EXIT, which
		 * terminates the console.
		*/
		String text = " ";
		while(!text.equals("Exit")) {
		out.print(">> ");
		text = in.nextLine();
		//in.close();
		
		List<Response> responses = server.interpret(text);
		
		/*
		 * TODO: This wrongly assumes that there is only
		 * one response from the server. However, there 
		 * may be one or more responses, and each response
		 * should be reported in sequence.
		 */
		for(int i = 0; i < responses.size();i++) {
		out.println("Success:       " + responses.get(i).getSuccess());
		out.println("Message:       " + responses.get(i).getMessage());
		
		}
		// TODO: Translate to tabular view in Module 4.
		Table table = responses.get(0).getTable();
		if (table != null) {
//			out.println("Table Schema:  " + table.getSchema());
//			out.println("Table State:   " + table.getState());
		
			String table_name = (String) table.getSchema().get("table_name");
			List<String> column_names = table.getSchema().getStringList("column_names");
			List<String> column_types = table.getSchema().getStringList("column_types");
			Object[] raw_state = table.getState().entrySet().toArray();
			
			if(table_name != null) {
				out.print("[" + table_name + "]\n");
			}
			
			int pindex = (int) table.getSchema().get("primary_column");
			
			int column_length = 15;
			
			
			out.print("+");
			for(int i = 0; i < column_names.size(); i++) {
				tableBoundary();
			}
			out.print("+\n");
			
			for(int i = 0; i < column_names.size(); i++) {
				String name = column_names.get(i);
				if(i == pindex) {
					name = name+"*";
				}
				out.print("|");
				out.print(name);
				for(int j = 0; j < column_length - name.length(); j++) {
					out.print(" ");
				}
				
			}
			out.print(" |\n");
			for(int i = 0; i < column_names.size(); i++) {
				
			}
			out.print("|");
			for(int i = 0; i < column_names.size(); i++) {
				tableBoundary2();
			}
			out.print("|\n");
			
			for(int i = 0; i < raw_state.length; i++) {
				raw_state[i] = raw_state[i].toString();
				int begin = raw_state[i].toString().indexOf("[");
				int end = raw_state[i].toString().indexOf("]");
				String temp = raw_state[i].toString().substring(begin + 1, end);
				String[] state_values = temp.split(",");
				
				for(int j = 0; j < state_values.length; j ++) {
					out.print("|");
					String value = state_values[j].trim();
					if(value.equals("null")) {
						value = " ";
					}
					if(column_types.get(j).toLowerCase().equals("string")) {
						value = "\"" + value + "\"";
					}
					if(column_types.get(j).toLowerCase().equals("integer")) {
						for(int k = 0; k < column_length - value.length(); k++) {
							out.print(" ");
						}
						out.print(value);
						
					}
					else if(value.length() < column_length) {
						out.print(value);
						for(int k = 0; k < column_length - value.length(); k++) {
							out.print(" ");
						}
					}
					else if(value.length() >= column_length) {
						int subtractor = value.length() - column_length + 4;
						value = value.substring(0, value.length() - subtractor);
						value = value + "...";
						out.print(value);
						for(int k = 0; k < column_length - value.length(); k++) {
							out.print(" ");
						}
					}
				}
				out.print(" |\n");
			}
			
			out.print("+");
			for(int i = 0; i < column_names.size(); i++) {
				tableBoundary();
			}
			out.print("+\n");
			
			
		}
		}
		in.close();
		out.close();
	}
	 public static void tableBoundary() {
		 System.out.print("----------------");
	 }
	 public static void tableBoundary2() {
		 System.out.print("================");
	 }
}


 