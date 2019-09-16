package core;

import driver.*;
import model.Response;
import model.Database;

import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.Closeable;
import java.io.IOException;

/**
 * This class implements a DBMS with an active connection to a backing database.
 * 
 * Finish implementing the required features but do not modify the protocols.
 */
public class Server implements Closeable {
	@SuppressWarnings("unused")
	private static final String 
			STUDENT_NAME = "David Gillum", 
			STUDENT_IDNUM = "800234355",
			STUDENT_EMAIL = "dsg0015@mix.wvu.edu";

	private Database database;
	private AbstractDriver[] drivers;

	public Server() {
		this(new Database());
	}

	public Server(Database database) {
		this.database = database;

		// TODO: Initialize available drivers in sequence.
		this.drivers = new AbstractDriver[] {
				new Echo(), 
				new Range(),
				new SquaresBelow(),
				new CreateTable(),
				new DropTable(),
				new ShowTables(),

				new Unrecognized()// unrecognized will always be the last driver and handle any previously
									// unrecognized drivers
		};
	}

	public Database database() {
		return database;
	}

	public List<Response> interpret(String script) {
		/*
		 * TODO: This wrongly assumes the entire script is a single query. However,
		 * there may be one or more semicolon-delimited queries in the script to be
		 * split and parsed distinctly.
		 */
		List<String> query = new LinkedList<String>();
		query.addAll(Arrays.asList(script.split("\\s*;\\s*")));

		/*
		 * TODO: This only tests the first driver for a response to the first query.
		 * Instead, iterate over every driver in sequence until one of them gives a
		 * non-null response for the first query, ensuring that the driver for
		 * unrecognized queries is the last driver tested. Then iterate again for the
		 * next query in sequence
		 */
		List<Response> responses = new LinkedList<Response>();

		for (int i = 0; i < query.size(); i++) {
			inside: 
				for (int j = 0; j < drivers.length; j++) {
				Response response = drivers[j].execute(query.get(i), database);
				if (response != null) {
					responses.add(response);
					break inside;
				}
			
			}
		}
		return responses;
	}

	@Override
	public void close() throws IOException {
		/*
		 * TODO: Optionally, execute any required tasks when the server is closed.
		 * Otherwise, leave unimplemented.
		 */
	}
}
