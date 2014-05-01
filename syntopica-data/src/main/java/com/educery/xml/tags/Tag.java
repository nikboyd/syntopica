package com.educery.xml.tags;

import java.util.*;

import com.educery.utils.Registry;

/**
 * Represents an X/HTML element and its contents.
 * 
 * <h4>Tag Responsibilities:</h4>
 * <ul>
 * <li>knows the attributes and contents of an element</li>
 * <li>formats an element as an X/HTML fragment</li>
 * </ul>
 */
public class Tag implements Registry.KeySource {
	
	/**
	 * Defines a protocol for building tags.
	 */
	public static interface Factory {

		public static final String TextAnchor = "text-anchor";
		public static final String FontFamily = "font-family";
		public static final String FontWeight = "font-weight";
		public static final String FontStyle = "font-style";
		public static final String FontSize = "font-size";

		public static final String Fill = "fill";
		public static final String FillOpacity = "fill-opacity";

		public static final String StrokeWidth = "stroke-width";
		public static final String Stroke = "stroke";

		public static final String Black = "#000000";
		public static final String SansSerif = "sans-serif";
		public static final String Middle = "middle";
		public static final String Normal = "normal";
		public static final String Italic = "italic";
		public static final String None = "none";
		
		/**
		 * Builds a new Tag.
		 * @return a new Tag
		 */
		public Tag buildElement();

	} // Factory

	private static final String Slash = "/";
	private static final String Quote = "\"";
	private static final String SemiColon = ";";

	private static final String LeftBracket = "<";
	private static final String RightBracket = ">";
	
	private static final String Context = "svg";
	private static final String Graphic = "g";
	private static final String Italics = "i";
	private static final String Anchor = "a";
	private static final String HyperLink = "href";

	private static final String Style = "style";
	private static final String TextSpan = "tspan";
	private static final String Text = "text";
	
	private static final String Rectangle = "rect";
	private static final String Polyline = "polyline";
	private static final String Polygon = "polygon";
	private static final String Height = "height";
	private static final String Width = "width";

	private String name = Empty;
	private ArrayList<Tag> contentTags = new ArrayList<Tag>();
	private ArrayList<String> names = new ArrayList<String>();
	private HashMap<String, String> namedValues = new HashMap<String, String>();
	
	public static Tag polyline() {
		return Tag.named(Polyline);
	}
	
	public static Tag polygon() {
		return Tag.named(Polygon);
	}
	
	public static Tag rectangle() {
		return Tag.named(Rectangle);
	}
	
	public static Tag textSpan() {
		return Tag.named(TextSpan);
	}

	public static Tag textBox() {
		return Tag.named(Text);
	}
	
	public static Tag context() {
		return Tag.named(Context);
	}

	/**
	 * Returns a new graphic Tag.
	 * @return a new Tag
	 */
	public static Tag graphic() {
		return Tag.named(Graphic);
	}
	
	/**
	 * Returns a new Tag for an italicized HTML element.
	 * @param content element content
	 * @return a new Tag
	 */
	public static Tag italics(String content) {
		return Tag.named(Italics).withContent(content);
	}
	
	/**
	 * Returns a new Tag for an HTML link element.
	 * @param reference a reference
	 * @return a new Tag
	 */
	public static Tag linkWith(String reference) {
		return Tag.named(Anchor).with(HyperLink, reference);
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
	private Tag() {
		this.namedValues.put(Empty, Empty);
	}
	
	/**
	 * Adds the supplied content to this element.
	 * @param content the tag content
	 * @return this Tag
	 */
	public Tag withContent(String content) {
		return with(Empty, content);
	}
	
	/**
	 * Adds a value named: x.
	 * @param value x value
	 * @return this Tag
	 */
	public Tag withX(int value) {
		return this.with("x", value + Empty);
	}
	
	/**
	 * Adds a value named: y.
	 * @param value y value
	 * @return this Tag
	 */
	public Tag withY(int value) {
		return this.with("y", value + Empty);
	}
	
	/**
	 * Adds a value named: width.
	 * @param value width value
	 * @return this Tag
	 */
	public Tag withWidth(int value) {
		return this.with(Width, value + Empty);
	}

	/**
	 * Adds a value named: width.
	 * @param value width value
	 * @return this Tag
	 */
	public Tag withWidth(String value) {
		return this.with(Width, value);
	}

	/**
	 * Adds a value named: height.
	 * @param value height value
	 * @return this Tag
	 */
	public Tag withHeight(int value) {
		return this.with(Height, value + Empty);
	}
	
	/**
	 * Adds a value named: height.
	 * @param value height value
	 * @return this Tag
	 */
	public Tag withHeight(String value) {
		return this.with(Height, value);
	}
	
	/**
	 * Copies the named values from a source Tag.
	 * @param source a source tag
	 * @return this Tag
	 */
	public Tag withValues(Tag source) {
		for (String key : source.names) {
			this.with(key, source.getValue(key));
		}
		return this;
	}
	
	/**
	 * Copies the style of another Tag.
	 * @param stylingTag a styling tag
	 * @return this Tag
	 */
	public Tag withStyle(Tag stylingTag) {
		return this.withStyle(stylingTag.getValue(Style));
	}
	
	/**
	 * Adds a value named: style.
	 * @param value style value
	 * @return this Tag
	 */
	public Tag withStyle(String styleText) {
		return this.with(Style, styleText);
	}
	
	/**
	 * Adds a named value to the style attribute.
	 * @param name a value name
	 * @param value a named value
	 * @return this Tag
	 */
	public Tag withStyle(String name, int value) {
		return this.withStyle(name, value + Empty);
	}
	
	/**
	 * Adds a named value to the style attribute.
	 * @param name a value name
	 * @param value a named value
	 * @return this Tag
	 */
	public Tag withStyle(String name, String value) {
		String priorValue = getValue(Style);
		if (!priorValue.isEmpty()) priorValue += SemiColon + Blank;
		String namedValue = name + Colon + Blank + value;
		return this.withStyle(priorValue + namedValue);
	}
	
	/**
	 * Adds an attribute named value to this element.
	 * @param name an attribute name
	 * @param value an attribute value
	 * @return this Tag
	 */
	public Tag with(String name, int value) {
		return this.with(name, value + Empty);
	}

	/**
	 * Adds an attribute named value to this element.
	 * @param name an attribute name
	 * @param value an attribute value
	 * @return this Tag
	 */
	public Tag with(String name, String value) {
		if (!this.hasValue(name)) this.names.add(name);
		this.namedValues.put(name, value);
		return this;
	}
	
	/**
	 * Adds a content tag to this element.
	 * @param contentTag a content element
	 * @return this Tag
	 */
	public Tag with(Tag contentTag) {
		this.contentTags.add(contentTag);
		return this;
	}
	
	/**
	 * Indicates whether this tag has any content.
	 * @return whether this tag has any content
	 */
	public boolean hasContent() {
		return (!getContent().isEmpty() || !this.contentTags.isEmpty());
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return this.name;
	}
	
	/**
	 * Returns the content of this tag.
	 * @return the tag content
	 */
	public String getContent() {
		return getValue(Empty);
	}
	
	/**
	 * Formats the tag as an XHTML fragment.
	 * @return an XHTML fragment
	 */
	public String format() {
		StringBuilder builder = new StringBuilder();
		buildTag(builder);
		return builder.toString();
	}

	/**
	 * Appends the attributes and content of this tag to a builder.
	 * @param builder a builder
	 */
	private void buildTag(StringBuilder builder) {
		builder.append(LeftBracket);
		builder.append(this.name);
		buildAttributes(builder);

		if (this.hasContent()) {
			builder.append(RightBracket);
			buildContent(builder);
			buildTail(builder);
		}
		else {
			builder.append(Slash);
			builder.append(RightBracket);
		}
	}
	
	/**
	 * Appends the content of this tag to a builder.
	 * @param builder a builder
	 */
	private void buildContent(StringBuilder builder) {
		if (this.contentTags.isEmpty()) {
			builder.append(getContent());
			return;
		}

		for (Tag tag : this.contentTags) {
			tag.buildTag(builder);
		}
	}
	
	/**
	 * Appends the attributes of this tag to a builder.
	 * @param builder a builder
	 */
	private void buildAttributes(StringBuilder builder) {
		if (this.namedValues.isEmpty()) return;
		for (String key : this.names) {
			buildAttribute(key, builder);
		}
	}
	
	/**
	 * Appends an attribute name value pair to a builder.
	 * @param key a key name
	 * @param builder a builder
	 */
	private void buildAttribute(String key, StringBuilder builder) {
		if (key.isEmpty()) return;
		builder.append(Blank);
		builder.append(key);
		builder.append(Equals.trim());
		builder.append(Quote);
		builder.append(getValue(key));
		builder.append(Quote);
	}
	
	/**
	 * Appends the tail of this tag to a builder.
	 * @param builder a builder
	 */
	private void buildTail(StringBuilder builder) {
		builder.append(LeftBracket);
		builder.append(Slash);
		builder.append(this.name);
		builder.append(RightBracket);
	}
	
	private boolean hasValue(String valueName) {
		return this.namedValues.containsKey(valueName);
	}
	
	private String getValue(String valueName) {
		return (this.hasValue(valueName) ? 
				this.namedValues.get(valueName) : Empty);
	}

} // Tag