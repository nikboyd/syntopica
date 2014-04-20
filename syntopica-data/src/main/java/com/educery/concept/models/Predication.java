package com.educery.concept.models;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents the skeleton of a complete predicate, as a template for a concrete statement of fact.
 * 
 * <h4>Predicate Responsibilities:</h4>
 * <ul>
 * <li>knows a predicate (a verb + some parts)</li>
 * <li>knows a valence count (number of terms)</li>
 * <li>produces a message selector</li>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Predication implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(Predication.class);
	static final String Separator = ":";
	
	private int valenceCount = 0;
	private ArrayList<String> parts = new ArrayList<String>();

	/**
	 * Returns a new unary Predication.
	 * @param verb a verb
	 * @return a new Predication
	 */
	public static Predication withUnary(String verb) {
		if (verb.isEmpty()) throw reportMissingVerb();
		Predication result = new Predication();
		result.parts.add(verb);
		result.valenceCount = 1;
		return result;
	}

	/**
	 * Returns a new n-ary Predication.
	 * @param verb a verb
	 * @param parts additional parts (typically prepositions)
	 * @return a new n-ary Predication
	 */
	public static Predication withVerb(String verb, String ... parts) {
		if (verb.isEmpty()) throw reportMissingVerb();
		Predication result = new Predication();
		result.valenceCount = 2;
		result.parts.add(verb);
		return result.with(parts);
	}
	
	/**
	 * Returns a new Predication.
	 * @param selector a selector
	 * @return a new Predication
	 */
	public static Predication fromSelector(String selector) {
		if (!selector.endsWith(Separator)) return withUnary(selector);
		String[] parts = selector.split(Separator);
		return Predication.withParts(Arrays.asList(parts));
	}
	
	private static Predication withParts(List<String> parts) {
		Predication result = new Predication();
		result.valenceCount = parts.size() + 1;
		result.parts.addAll(parts);
		return result;
	}

	/**
	 * Indicates whether this predication has been constructed (and thus locked).
	 * @return whether this predication is locked
	 */
	public boolean isLocked() {
		return Domain.currentlyHasPredicate(this.getKey());
	}
	
	/**
	 * Adds some more parts to this predication.
	 * @param parts some parts
	 * @return this Predication
	 */
	public Predication with(String ... parts) {
		if (isLocked()) {
			reportLockedPredicate();
			return this;
		}

		if (parts.length > 0) {
			this.parts.addAll(Arrays.asList(parts));
			this.valenceCount += parts.length;
		}
		return this;
	}

	/**
	 * Returns the verb of this predication.
	 * @return a verb
	 */
	public String getVerb() {
		return this.parts.get(0);
	}
	
	/**
	 * Returns all the parts that comprise this predication.
	 * @return the parts of this predication
	 */
	public String[] getParts() {
		String[] empty = { };
		return this.parts.toArray(empty);
	}
	
	/**
	 * Returns the valence count of this predication.
	 * @return a count
	 */
	public int getValenceCount() {
		return this.valenceCount;
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() { return getSelector(); }

	/**
	 * Returns a message selector that represents this predication.
	 * @return a message selector
	 */
	public String getSelector() {
		String[] selectorParts = getSelectorParts();
		StringBuilder builder = new StringBuilder();
		for (String part : selectorParts) {
			builder.append(part);
		}
		return builder.toString();
	}
	
	/**
	 * Returns the parts of a message selector for this predication.
	 * @return the selector parts
	 */
	public String[] getSelectorParts() {
		int count = getValenceCount();
		String[] results = getParts();
		for (int index = 0; index < results.length; index++) {
			if (count > 1) results[index] += Separator;
		}
		return results;
	}
	
	/**
	 * Builds a new Fact from this predication.
	 * @param subject a subject
	 * @param topics additional topics
	 * @return a new Fact
	 */
	public Fact buildFact(String subject, String ... topics) {
		Domain.register(this);
		return Fact.with(this).with(subject).with(topics);
	}
	
	public Fact buildFact(List<String> topics) {
		Domain.register(this);
		return Fact.with(this).with(topics);
	}
	
	/**
	 * Dumps a representation of this predication.
	 */
	public void dump() {
		Logger.info("predicate selector = " + getSelector());
	}
	
	private static RuntimeException reportMissingVerb() {
		return new IllegalArgumentException("predicate requires a verb");
	}
	
	private void reportLockedPredicate() {
		Logger.warn(getSelector() + " was locked after building a fact");
	}

} // Predication
