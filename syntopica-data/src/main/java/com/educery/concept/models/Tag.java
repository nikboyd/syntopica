package com.educery.concept.models;

import java.util.HashMap;

/**
 * Generates an XML element from its contents.
 * 
 * <h4>Tag Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class Tag {

	private static final String Empty = "";
	private static final String Slash = "/";
	private static final String Blank = " ";
	private static final String Quote = "\"";
	private static final String Equals = "=";
	private static final String LeftBracket = "<";
	private static final String RightBracket = ">";

	private String name = Empty;
	private HashMap<String, String> namedValues = new HashMap<String, String>();
	
	public static Tag italics(String content) {
		return Tag.named("i").withContent(content);
	}
	
	public static Tag linkWith(String reference) {
		return Tag.named("a").with("href", reference);
	}
	
	public static Tag named(String tagName) {
		Tag result = new Tag();
		result.name = tagName;
		return result;
	}
	
	public Tag withContent(String content) {
		return with(Empty, content);
	}

	public Tag with(String name, String value) {
		this.namedValues.put(name, value);
		return this;
	}
	
	public boolean hasContent() {
		return (this.namedValues.containsKey(Empty));
	}
	
	public String getContent() {
		return this.namedValues.get(Empty);
	}
	
	public String format() {
		StringBuilder builder = new StringBuilder();
		if (this.hasContent()) {
			builder.append(LeftBracket);
			builder.append(this.name);
			buildTag(builder);
			builder.append(RightBracket);
			builder.append(getContent());
			builder.append(LeftBracket);
			builder.append(Slash);
			builder.append(this.name);
			builder.append(RightBracket);
		}
		else {
			builder.append(LeftBracket);
			builder.append(this.name);
			buildTag(builder);
			builder.append(Slash);
			builder.append(RightBracket);
		}
		return builder.toString();
	}
	
	private void buildTag(StringBuilder builder) {
		for (String key : this.namedValues.keySet()) {
			if (!key.isEmpty()) {
				builder.append(Blank);
				builder.append(key);
				builder.append(Equals);
				builder.append(Quote);
				builder.append(this.namedValues.get(key));
				builder.append(Quote);
			}
		}
	}

} // Tag