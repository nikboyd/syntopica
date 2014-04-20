package com.educery.concept.models;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.educery.concept.models.Topic.Number;

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

	private Domain domain;
	private Predication predicate;
	private String definedTopic = Empty;
	private ArrayList<String> topics = new ArrayList<String>();
	
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
		
		// register the subject of this statement of fact
		this.domain = Domain.getCurrentDomain();
		getDomain().registerTopic(Topic.named(topics.get(0))).with(this);
		this.topics.addAll(topics);
		return this;
	}
	
	/**
	 * The domain within which this fact is construed.
	 * @return a Domain
	 */
	public Domain getDomain() {
		return this.domain;
	}

	/**
	 * Returns the predication from which this fact was derived.
	 * @return a Predication
	 */
	public Predication getPredicate() {
		return this.predicate;
	}
	
	/**
	 * The topics associated with this fact.
	 * @return a list of Topics
	 */
	public List<String> getTopics() {
		return this.topics;
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
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getMessage();
	}

	/** {@inheritDoc} */
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
	public Fact define(Topic topic) {
		this.definedTopic = topic.getTitle();
		return this;
	}
	
	/**
	 * Returns the (potentially) related subjects.
	 * @return the related subjects
	 */
	public List<String> getRelatedSubjects() {
		return getTopics().stream()
				.filter(s -> !(s.trim().isEmpty()))
				.map(s -> Number.convertToSingular(s))
				.collect(Collectors.toList());
	}
	
	/**
	 * Formats the complete predicate of this fact as an HTML fragment.
	 * @return the complete predicate of this fact formatted as an HTML fragment
	 */
	public String getFormattedPredicate() {
		String[] parts = getPredicate().getParts();
		StringBuilder builder = new StringBuilder();
		builder.append(Tag.italics(parts[0]).format());
		if (getValenceCount() > 1) {
			builder.append(Blank);
			builder.append(formatRelatedTopics(1));
			if (getValenceCount() > 2) {
				for (int index = 2; index < getValenceCount(); index++) {
					builder.append(Blank);
					builder.append(formatRelatedTopics(index));
				}
			}
		}
		return builder.toString();
	}

	/**
	 * Formats the topics related to this fact as HTML fragments.
	 * @param index a topic index
	 * @return a comma-separated list of topics formatted as HTML fragments
	 */
	private String formatRelatedTopics(int index) {
		return getPredicate().getParts()[index] + Blank + 
				formatRelatedTopics(getTopics().get(index));
	}
	
	/**
	 * Formats the topics related to this fact as HTML fragments.
	 * @param topics a comma-separated list of topics
	 * @return a comma-separated list of topics formatted as HTML fragments
	 */
	private String formatRelatedTopics(String topics) {
		StringBuilder builder = new StringBuilder();
		for (String topicName : Topic.namesFrom(topics)) {
			if (builder.length() > 0) {
				builder.append(Comma + Blank);
			}
			
			String subject = topicName.trim();
			String singular = Number.convertToSingular(subject);
			boolean plural = subject.length() > singular.length();
			Number aNumber = Number.getNumber(plural);

			if (getDomain().containsTopic(singular)) {
				Topic topic = getDomain().getTopic(singular);
				builder.append(topic.formatReferenceLink(aNumber));
			}
			else {
				Topic topic = Topic.named(singular);
				builder.append(topic.formatReferenceLink(aNumber));
			}
		}
		return builder.toString();
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
		Logger.info(getRelatedSubjects().toString());
	}
	
	private String getPrefix() {
		return (this.definedTopic.isEmpty() ? 
				getClass().getSimpleName() : 
				this.definedTopic) + Equals;
	}

	private RuntimeException reportMissingSubject() {
		return new IllegalArgumentException("missing required subject for predicate " + getKey());
	}
	
	private RuntimeException reportExcessiveTopics() {
		return new IllegalArgumentException("too many topics for predicate " + getKey());
	}

} // Fact