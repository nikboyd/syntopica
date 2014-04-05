package com.educery.concept.models;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageReader {

	private static final Log Logger = LogFactory.getLog(MessageReader.class);
	private static final String Empty = "";
	private static final String Blank = " ";
	private static final String Period = ".";
	private static final String Equals = "=";
	
	private BufferedReader reader;
	
	public static MessageReader with(InputStream stream) {
		MessageReader result = new MessageReader();
		result.reader = new BufferedReader(new InputStreamReader(stream));
		return result;
	}
	
	private MessageReader() { }
	
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
	
	private String readLine() throws Exception {
		return this.reader.readLine();
	}
	
	private void readFact(String line) {
		if (line.trim().isEmpty()) return;
		String definedTopic = Empty;
		String[] terms = line.split(Blank);
		
		String topic = Empty;
		String selector = Empty;
		ArrayList<String> topics = new ArrayList<String>();
		for (int index = 0; index < terms.length; index++) {
			String term = terms[index].trim().replace(Period, Blank);
			if (!term.isEmpty()) {
				if (term.equals(Equals)) {
					definedTopic = topic.trim();
					topic = Empty;
				}
				else
				if (term.endsWith(Predication.Separator)) {
					selector += term;
					topics.add(topic.trim());
					topic = Empty;
				}
				else {
					topic += term;
					topic += Blank;
				}
			}
		}

		topics.add(topic.trim());
		if (topics.get(0).equals(Domain.class.getSimpleName()) && selector.equals("named:")) {
			Domain.named(topics.get(1));
		}

		Predication p = Predication.fromSelector(selector);
		Fact result = p.buildFact(topics);
		if (!definedTopic.isEmpty()) {
			result.defines(Topic.named(definedTopic));
			definedTopic = Empty;
		}
		result.dumpMessage();
	}

} // MessageReader