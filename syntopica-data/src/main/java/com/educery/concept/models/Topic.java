package com.educery.concept.models;

import java.util.*;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Describes a topic and its associations.
 * 
 * <h4>Topic Responsibilities:</h4>
 * <ul>
 * <li>knows a title</li>
 * <li>knows a discussion (if given)</li>
 * <li>knows some associations (if given)</li>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Topic implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(Topic.class);
	private static final String[] Vowels = { "a", "e", "i", "o", "u", "h" };
	private static final String[] Articles = { "a", "an", "some" };
	private static final String Period = ".";
	private static final String Blank = " ";
	private static final String Empty = "";

	private static final List<String> VowelList = Arrays.asList(Vowels);
	private static final String[] Plurals = { "ue", "y", "ess" };
	private static final HashMap<String, String> Replacements = new HashMap<String, String>();
	static {
		Replacements.put("ue", "ues");
		Replacements.put("y", "ies");
		Replacements.put("ess", "ess");
	}

	private String title = Empty;
	private boolean defined = false;
	private Discussion discussion = Discussion.withoutContent();
	private Registry<Fact> facts = Registry.empty();
	
	/**
	 * Returns a new Topic.
	 * @param title a title
	 * @return a new Topic
	 */
	public static Topic named(String title) {
		Topic result = new Topic();
		result.title = title.trim();
		return result;
	}
	
	/**
	 * Adds a discussion to this topic.
	 * @param discussion a discussion
	 * @return this Topic
	 */
	public Topic with(Discussion discussion) {
		setDiscussion(discussion);
		return this;
	}
	
	/**
	 * Adds some facts to this Topic.
	 * @param facts some facts
	 * @return this Topic
	 */
	public Topic with(Fact ... facts) {
		for (Fact fact : facts) {
			this.facts.register(fact);
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() { return getTitle(); }

	/**
	 * The title of this topic.
	 * @return a title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * The discussion associated with this topic.
	 * @return a discussion
	 */
	public Discussion getDiscussion() {
		return this.discussion;
	}

	/**
	 * Adds a discussion to the topic.
	 * @param discussion a discussion
	 */
	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
	}
	
	/**
	 * Makes this a definition.
	 * @return this Topic
	 */
	public Topic makeDefined() {
		this.defined = true;
		return this;
	}
	
	/**
	 * Returns the facts associated with this topic.
	 * @return facts
	 */
	public Fact[] getFacts() {
		Fact[] sample = { };
		List<Fact> results = this.facts.getItems();
		return results.toArray(sample);
	}
	
	public String getCapitalizedLink() {
		return Tag.linkWith(getLinkFileName())
				.withContent(getSubject()).format();
	}
	
	public String getReferenceLink() {
		return getReferenceLink(false);
	}
	
	public String getReferenceLink(boolean plural) {
		return Tag.linkWith(getLinkFileName())
				.withContent(getSubject(plural)).format();
	}
	
	public String getArticle() {
		return getArticle(false);
	}
	
	public String getArticle(boolean plural) {
		return getArticle(getTitle(), plural);
	}
	
	public static String getArticle(String title, boolean plural) {
		if (plural) return Articles[2];
		String first = String.valueOf((title.charAt(0)));
		return (VowelList.contains(first) ? Articles[1] : Articles[0] );
	}
	
	private String getSubject(boolean plural) {
		if (!plural) return getTitle();
		String title = getTitle();
		for (String suffix : Plurals) {
			if (title.endsWith(suffix)) {
				int shortLength = title.length() - suffix.length();
				return title.substring(0, shortLength) + Replacements.get(suffix);
			}
		}
		return title + "s";
	}
	
	public String getSubject() {
		return  WordUtils.capitalize(getTitle());
	}
	
	public String getLinkFileName() {
		String linkName = getTitle().replace(Blank, Period);
		return linkName + ".html";
	}
	
	/**
	 * Dumps this topic.
	 */
	public void dump() {
		Logger.info("topic: " + getTitle());
		if (this.defined) {
			getFacts()[0].dumpMessage();
		}
	}

} // Topic