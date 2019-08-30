package core;

import model.Response;
import model.Table;

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
			out.println("Table Schema:  " + table.getSchema());
			out.println("Table State:   " + table.getState());
		
		}
		}
		in.close();
		out.close();
	}
}
