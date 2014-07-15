package com.educery.concepts;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.educery.concepts.Topic.Number;
import com.educery.utils.*;

/**
 * Reads a topic discussion from a file.
 * 
 * <h4>TopicReader Responsibilities:</h4>
 * <ul>
 * <li>reads a topic discussion</li>
 * <li>prepares a topic discussion for page rendering</li>
 * </ul>
 */
public class TopicReader implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(TopicReader.class);
	private static final String NewLine = "\n";
	private static final String Break = "<br/>";
	
	private BufferedReader reader;
	private StringBuilder builder = new StringBuilder();
	private HashMap<String, String> linkMap = new HashMap<>();

	/**
	 * Returns a new TopicReader.
	 * @param modelFile a model file
	 * @return a new TopicReader
	 */
	public static TopicReader from(File modelFile) {
		try {
			return with(new FileInputStream(modelFile));
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Returns a new TopicReader.
	 * @param stream a topic stream
	 * @return a new TopicReader
	 */
	public static TopicReader with(InputStream stream) {
		TopicReader result = new TopicReader();
		result.reader = new BufferedReader(new InputStreamReader(stream));
		return result;
	}
	
	private TopicReader() { }
	
	/**
	 * Contains the links defined in a discussion file.
	 * @return a link map
	 */
	public Map<String, String> getLinkMap() {
		return this.linkMap;
	}
	
	/**
	 * Reads a discussion from its backing store.
	 * @return the content of a discussion
	 */
	public String readDiscussion() {
		readTopic();
		return this.builder.toString();
	}
	
	private void readTopic() {
		try {
			for (String line = readLine(); line != null; line = readLine() ) {
				if (line.contains(Equals)) {
					readLink(line);
				}
				else {
					readTopic(line);
				}
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
	
	private void readLink(String line) {
		String[] links = line.split(Equals);
		this.linkMap.put(links[0].trim(), links[1].trim());
	}
	
	private void readTopic(String line) {
		this.builder.append(line.trim() + NewLine);
	}

	@Override
	public String getKey() {
		return Empty;
	}

} // TopicReader