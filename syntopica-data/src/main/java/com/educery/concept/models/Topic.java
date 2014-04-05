package com.educery.concept.models;

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

	private String title = "";
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
		return this.facts.getItems();
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