package com.educery.concept.models;

import java.util.HashMap;

/**
 * Generates an XHTML element from its contents.
 * 
 * <h4>Tag Responsibilities:</h4>
 * <ul>
 * <li>formats an element as an XHTML fragment</li>
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
	
	/**
	 * Returns a new Tag for an italicized HTML element.
	 * @param content element content
	 * @return a new Tag
	 */
	public static Tag italics(String content) {
		return Tag.named("i").withContent(content);
	}
	
	/**
	 * Returns a new Tag for an HTML link element.
	 * @param reference a reference
	 * @return a new Tag
	 */
	public static Tag linkWith(String reference) {
		return Tag.named("a").with("href", reference);
	}
	
	/**
	 * Returns a new Tag of a specific kind.
	 * @param tagName a tag name
	 * @return a new Tag
	 */
	public static Tag named(String tagName) {
		Tag result = new Tag();
		result.name = tagName;
		return result;
	}
	
	/**
	 * Constructs a new Tag. Prevents external construction 
	 * without use of the static factory methods.
	 */
	private Tag() { }
	
	/**
	 * Adds the supplied content to this tag.
	 * @param content the tag content
	 * @return this Tag
	 */
	public Tag withContent(String content) {
		return with(Empty, content);
	}

	/**
	 * Adds an attribute named value to this tag.
	 * @param name an attribute name
	 * @param value an attribute value
	 * @return this Tag
	 */
	public Tag with(String name, String value) {
		this.namedValues.put(name, value);
		return this;
	}
	
	/**
	 * Indicates whether this tag has any content.
	 * @return whether this tag has any content
	 */
	public boolean hasContent() {
		return (this.namedValues.containsKey(Empty));
	}
	
	/**
	 * Returns the content of this tag.
	 * @return the tag content
	 */
	public String getContent() {
		return this.namedValues.get(Empty);
	}
	
	/**
	 * Formats the tag as an XHTML fragment.
	 * @return an XHTML fragment
	 */
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

	/**
	 * Appends the attributes of this tag to a builder.
	 * @param builder a builder
	 */
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