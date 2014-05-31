package com.educery.concepts;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.educery.concepts.Topic.Number;
import com.educery.utils.Registry;
import com.educery.utils.Tag;

/**
 * Expresses a statement of fact.
 * 
 * <h4>Fact Responsibilities:</h4>
 * <ul>
 * <li>knows the predicate from which it was derived</li>
 * <li>knows the topics associated with this fact, esp. its subject</li>
 * <li>knows the placement of each topic within the predicate</li>
 * <li>formats XHTML fragments</li>
 * <li>creates facts from messages</li>
 * </ul>
 */
public class Fact implements Registry.KeySource {
	
	private static final Log Logger = LogFactory.getLog(Fact.class);

	private Domain domain;
	private Selector predicate;
	private String definedTopic = Empty;
	private ArrayList<String> topics = new ArrayList<String>();
	
	/**
	 * Parses a message to produce a fact.
	 * @param message a message
	 * @return a new Fact
	 */
	public static Fact parseFrom(String message) {
		String definedTopic = Empty;
		String[] terms = message.split(Blank);
		
		String topic = Empty;
		String selector = Empty;
		ArrayList<String> topics = new ArrayList<String>();
		for (int index = 0; index < terms.length; index++) {
			String term = terms[index].trim().replace(Period, Blank);
			if (!term.isEmpty()) {
				if (term.equals(Equals.trim())) {
					definedTopic = topic.trim();
					topic = Empty;
				}
				else
				if (term.endsWith(Colon)) {
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
		if (Domain.accepts(selector, topics)) {
			Domain.named(topics.get(1));
			return null;
		}

		Selector p = Selector.fromSelector(selector);
		Fact result = p.buildFact(topics);
		if (!definedTopic.isEmpty()) {
			result.define(Topic.named(definedTopic));
			definedTopic = Empty;
		}
		return result;
	}
	
	/**
	 * Returns a new Fact.
	 * @param p a predicate
	 * @return a new Fact
	 */
	public static Fact with(Selector p) {
		Fact result = new Fact();
		result.predicate = p;
		return result;
	}
	
	/**
	 * Constructs a new Fact. Prevents external construction 
	 * without use of the static factory method.
	 */
	private Fact() { }

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
	 * Returns the predicate from which this fact was derived.
	 * @return a Predication
	 */
	public Selector getPredicate() {
		return this.predicate;
	}
	
	/**
	 * The topics associated with this fact.
	 * @return a list of Topics
	 */
	public String[] getTopics() {
		return this.topics.stream().toArray(String[]::new);
	}
	
	/**
	 * Return a specific topic.
	 * @param index indicates which topic
	 * @return a topic
	 */
	private String getTopic(int index) {
		return this.topics.get(index);
	}

	/**
	 * Returns a sentence that expresses this fact.
	 * @return a sentence
	 */
	public String getSentence() {
		return getMessage().replace(Colon, Empty);
	}
	
	/**
	 * Returns a message that expresses this fact.
	 * @return a message
	 */
	public String getMessage() {
		String[] topics = getTopics();
		String[] parts = getPredicate().getSelectorParts();
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < topics.length; index++) {
			builder.append(Blank);
			builder.append(topics[index]);
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
		topic.getFactRegistry().register(this);
		getDomain().getTopics().register(topic);
		return this;
	}
	
	/**
	 * Returns the (potentially) related subjects.
	 * @return the related subjects
	 */
	public List<String> getRelatedSubjects() {
		return this.topics.stream()
				.filter(s -> !(s.trim().isEmpty()))
				.map(s -> Number.convertToSingular(s))
				.collect(Collectors.toList());
	}
	
	/**
	 * Formats the complete predicate of this fact as an HTML fragment.
	 * @return the complete predicate of this fact formatted as an HTML fragment
	 */
	public String getFormattedPredicate(Topic context) {
		String subject = getTopic(0);
		String[] parts = getPredicate().getParts();
		StringBuilder builder = new StringBuilder();
		if (!this.definedTopic.isEmpty() && 
			!context.getTitle().equals(subject)) {
			builder.append(formatRelatedTopics(subject));
			builder.append(Blank);
		}

		builder.append(Tag.italics(parts[0]).format());
		if (getValenceCount() > 1) {
			builder.append(Blank);
			builder.append(formatRelatedTopics(getTopic(1)));
			if (getValenceCount() > 2) {
				for (int index = 2; index < getValenceCount(); index++) {
					builder.append(Blank);
					builder.append(parts[index - 1]);
					builder.append(Blank);
					builder.append(formatRelatedTopics(getTopic(index)));
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * Formats the topics related to this fact as HTML fragments.
	 * @param topics a comma-separated list of topics
	 * @return a comma-separated list of topics formatted as HTML fragments
	 */
	private String formatRelatedTopics(String topics) {
		List<String> topicNames = Topic.namesFrom(topics);
		StringBuilder builder = new StringBuilder();
		for (String topicName : topicNames) {
			String subject = topicName.trim();
			String singularSubject = Number.convertToSingular(subject);
			Number aNumber = Number.getNumber(subject.length() > singularSubject.length());

			if (builder.length() > 0) builder.append(Comma + Blank);
			builder.append(getTopic(singularSubject).formatReferenceLink(aNumber));
		}
		return builder.toString();
	}
	
	/**
	 * Returns a topic with a given name.
	 * @param topicName a topic name
	 * @return a Topic
	 */
	private Topic getTopic(String topicName) {
		return getDomain().containsTopic(topicName) ? 
				getDomain().getTopic(topicName) : 
				Topic.named(topicName);
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
	
	/**
	 * Returns a prefix that describes this fact.
	 * @return a fact description prefix
	 */
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