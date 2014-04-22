package com.educery.concept.models;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads a domain model from a file.
 * 
 * <h4>MessageReader Responsibilities:</h4>
 * <ul>
 * <li>creates the facts and topics found in a model file</li>
 * </ul>
 */
public class MessageReader implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(MessageReader.class);
	
	private BufferedReader reader;
	
	/**
	 * Returns a new MessageReader.
	 * @param stream a stream of fact messages
	 * @return a new MessageReader
	 */
	public static MessageReader with(InputStream stream) {
		MessageReader result = new MessageReader();
		result.reader = new BufferedReader(new InputStreamReader(stream));
		return result;
	}
	
	/**
	 * Constructs a new MessageReader.
	 */
	private MessageReader() { }
	
	/**
	 * Reads facts from the configured message stream.
	 */
	public void readFacts() {
		try {
			for (String line = readLine(); line != null; line = readLine() ) {
				readFact(line);
			}
			this.reader.close();
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Reads a single line (statement) from the message stream.
	 * @return a single statement
	 * @throws Exception if raised during the read
	 */
	private String readLine() throws Exception {
		return this.reader.readLine();
	}
	
	/**
	 * Reads a Fact from a given line.
	 * @param line a line of text
	 */
	private void readFact(String line) {
		if (line.trim().isEmpty()) return;
		Fact.parseFrom(line);
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return Empty;
	}

} // MessageReader