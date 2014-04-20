package com.educery.concept.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains an informal discussion on a topic.
 * 
 * <h4>Discussion Responsibilities:</h4>
 * <ul>
 * <li>knows the content of a discussion</li>
 * </ul>
 */
public class Discussion {

	private static final Log Logger = LogFactory.getLog(Discussion.class);
	
	private String content = "";
	
	/**
	 * Returns a new (empty) Discussion.
	 * @return a Discussion
	 */
	public static Discussion withoutContent() {
		return new Discussion();
	}

	/**
	 * Constructs a new Discussion.
	 */
	private Discussion() { }

	/**
	 * Returns the content of this discussion.
	 * @return the discussion content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Sets the content of this discussion.
	 * @param content the content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Logs a copy of this discussion.
	 */
	public void dump() {
		Logger.info("discussion: " + getContent());
	}

} // Discussion