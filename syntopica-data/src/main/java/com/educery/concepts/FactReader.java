package com.educery.concepts;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.educery.utils.Registry;

/**
 * Reads facts from a domain model file.
 * 
 * <h4>FactReader Responsibilities:</h4>
 * <ul>
 * <li>creates the facts and topics found in a model file</li>
 * </ul>
 */
public class FactReader implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(FactReader.class);
	
	private BufferedReader reader;
	
	/**
	 * Returns a new FactReader.
	 * @param modelFile a model file containing facts
	 * @return a new FactReader
	 */
	public static FactReader from(File modelFile) {
		try {
			return with(new FileInputStream(modelFile));
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Returns a new FactReader.
	 * @param stream a stream of fact messages
	 * @return a new FactReader
	 */
	public static FactReader with(InputStream stream) {
		FactReader result = new FactReader();
		result.reader = new BufferedReader(new InputStreamReader(stream));
		return result;
	}
	
	/**
	 * Constructs a new FactReader.
	 */
	private FactReader() { }
	
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

} // FactReader