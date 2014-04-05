package com.educery.concept.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Expresses a statement of fact.
 * 
 * <h4>Fact Responsibilities:</h4>
 * <ul>
 * <li>knows the predicate from which it was derived</li>
 * <li>knows the topics associated with this fact, esp. its subject</li>
 * <li>knows the placement of each topic within the predicate</li>
 * <li></li>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Fact implements Registry.KeySource {
	
	private static final Log Logger = LogFactory.getLog(Fact.class);
	private static final String Empty = "";
	private static final String Blank = " ";
	private static final String Period = ".";
	private static final String Equals = " = ";
	
	private Predication predicate;
	private ArrayList<String> topics = new ArrayList<String>();
	private String definedTopic = Empty;
	
	/**
	 * Returns a new Fact.
	 * @param p a predication
	 * @return a new Fact
	 */
	public static Fact with(Predication p) {
		Fact result = new Fact();
		result.predicate = p;
		return result;
	}

	/**
	 * Adds topics to this fact.
	 * @param topics some topics
	 * @return this Fact
	 */
	public Fact with(String ... topics) {
		return this.with(Arrays.asList(topics));
	}
	
	/**
	 * Adds topics to this fact.
	 * @param topics some topics
	 * @return this Fact
	 */
	public Fact with(List<String> topics) {
		if (topics.size() < 1) throw reportMissingSubject();
		if (topics.size() > getValenceCount()) throw reportExcessiveTopics();
		Domain.registerTopic(topics.get(0)).with(this);
		this.topics.addAll(topics);
		return this;
	}

	/**
	 * Returns the predication from which this fact was derived.
	 * @return a Predication
	 */
	public Predication getPredicate() {
		return this.predicate;
	}

	/**
	 * Returns a sentence that expresses this fact.
	 * @return a sentence
	 */
	public String getSentence() {
		String[] parts = getPredicate().getParts();
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < this.topics.size(); index++) {
			builder.append(Blank);
			builder.append(this.topics.get(index));
			if (index < parts.length) {
				builder.append(Blank);
				builder.append(parts[index]);
			}
		}
		return builder.toString().trim() + Period;
	}
	
	/**
	 * Returns a message that expresses this fact.
	 * @return a message
	 */
	public String getMessage() {
		String[] parts = getPredicate().getSelectorParts();
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < this.topics.size(); index++) {
			builder.append(Blank);
			builder.append(this.topics.get(index));
			if (index < parts.length) {
				builder.append(Blank);
				builder.append(parts[index]);
			}
		}
		return builder.toString().trim() + Period;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

	@Override
	public String getKey() {
		return getPredicate().getKey();
	}
	
	/**
	 * The valence count of the associated predicate.
	 * @return a count
	 */
	public int getValenceCount() {
		return getPredicate().getValenceCount();
	}
	
	/**
	 * Makes this Fact a definition.
	 * @param topic a defined topic
	 * @return this Fact
	 */
	public Fact defines(Topic topic) {
		this.definedTopic = topic.getTitle();
		return this;
	}
	
	private String getPrefix() {
		return (this.definedTopic.isEmpty() ? "fact" : this.definedTopic) + Equals;
	}

	/**
	 * Dumps a sentence.
	 */
	public void dumpSentence() {
		Logger.info(getPrefix() + getSentence());
	}

	/**
	 * Dumps a message.
	 */
	public void dumpMessage() {
		Logger.info(getPrefix() + getMessage());
	}
	
	private RuntimeException reportMissingSubject() {
		return new IllegalArgumentException("missing required subject for predicate " + getKey());
	}
	
	private RuntimeException reportExcessiveTopics() {
		return new IllegalArgumentException("too many topics for predicate " + getKey());
	}

} // Fact