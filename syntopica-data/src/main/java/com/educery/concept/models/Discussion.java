package com.educery.concept.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains an informal discussion.
 * 
 * <h4>Discussion Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Discussion {

	private static final Log Logger = LogFactory.getLog(Discussion.class);
	
	private String content = "";
	
	public static Discussion withoutContent() {
		return new Discussion();
	}

	private Discussion() { }

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void dump() {
		Logger.info("discussion: " + getContent());
	}

} // Discussion